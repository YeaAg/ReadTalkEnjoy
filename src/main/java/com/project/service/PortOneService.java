package com.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.LoanDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

@Log4j2
@Service
@PropertySource("classpath:my.properties")
public class PortOneService {
    @Autowired private ObjectMapper objectMapper;

    private final String IMP_KEY;
    private final String IMP_SECRET;

    private final String AUTHENTICATION_TOKEN_URI = "https://api.iamport.kr/users/getToken";
    private final String TEL_AUTENTICATION_URI = "https://api.iamport.kr/certifications/{impUid}";
    private final String PAYMENT_URI = "https://api.iamport.kr/payments/{impUid}";

    private final RestTemplate restTemplate = new RestTemplate();

    public PortOneService(
            @Value("${IMP_KEY}") String impKey,
            @Value("${IMP_SECRET}") String impSecret
    ) {
        IMP_KEY = impKey;
        IMP_SECRET = impSecret;
        log.info("Import key: " + impKey);
        log.info("Import secret: " + impSecret);
    }


    public String tel_authentication(String impUid, String tel){
        String token = get_authentication_token();
        if(token == null){
            return null;
        }

        return tel_certification(token, impUid, tel);
    }

    public String get_authentication_token(){
        try{
            Map<String, String> requestBodyData = Map.of(
                    "imp_key", IMP_KEY,
                    "imp_secret", IMP_SECRET
            );

            log.info("requestBodyData: " + requestBodyData);

            RequestEntity<String> request = RequestEntity
                    .post(AUTHENTICATION_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(requestBodyData));

            ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
            if(response.getStatusCode().equals(HttpStatus.OK)){
                System.out.println("토큰 발급 성공!");
                Map responseBody = (Map) response.getBody().get("response");
                String accessToken = (String)responseBody.get("access_token");
                System.out.println("토큰 값: " + accessToken);
                return accessToken;
            }

        } catch (Exception e) {
            log.error("오류 발생: " + e.getMessage());
        }
        return null;
    }

    public String tel_certification(String token, String impUid, String tel){
        RequestEntity<Void> request = RequestEntity
                .get(TEL_AUTENTICATION_URI, impUid)
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            Map responseBody = (Map) response.getBody().get("response");
            String phone = (String) responseBody.get("phone"); // 포트원 인증할 때 쓴 전화번호
            boolean certified = (boolean) responseBody.get("certified"); // 인증 통과?
            String uniqueKey = (String) responseBody.get("unique_key");

            if(!certified || !phone.equals(tel.replace("-",""))){
                return null;
            }
            return uniqueKey;
        }
        return null;

    }

    // 🔹 포트원 결제 내역 조회
    private LoanDTO get_payments(String impUid, String token){
        RequestEntity<Void> request = RequestEntity
                .get(PAYMENT_URI, impUid)
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        // 요청이 성공했다면
        if(response.getStatusCode().equals(HttpStatus.OK)){
            Map body = response.getBody();
            Map responseBody = (Map)body.get("response");
            String merchantUid = (String) responseBody.get("merchant_uid"); // 주문번호
            Integer amount = (Integer) responseBody.get("amount"); // 결제 금액
            // 인증되지 않았거나, 인증된 번호와 회원가입 시도하는 휴대폰번호가 다르다면
            LoanDTO order = new LoanDTO();
            order.setFinalPrice(amount);
            order.setImpUid(merchantUid);
            log.info("[결제정보]: " + order);
            return order;
        }
        return null;
    }

    // 🔹 포트원 결제 검증 (GET /payments?imp_uid=...)
    public LoanDTO payments_authentication(String impUid){
        String token = get_authentication_token();
        // 토큰을 발급받지 못했으면 실패
        if(Objects.isNull(token)){
            return null;
        }
        return get_payments(impUid, token);
    }
}
