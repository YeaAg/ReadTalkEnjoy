package com.project.service;

import com.project.dto.SnsInfoDTO;
import com.project.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import com.project.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

//@PropertySource("classpath:my.properties")
@Log4j2
@Service
public class SecurityOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired private UserMapper userMapper;

    private final String CI;

    public SecurityOAuth2UserService(
            @Value("${CI}") String ci
    ) {
        CI = ci;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName().toUpperCase();
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        SnsInfoDTO snsInfoDTO = switch (clientName){
            case "NAVER" -> get_naver_sns_info(attributes);
            case "KAKAO" -> get_kakao_sns_info(attributes);
            case "GOOGLE" -> get_google_sns_info(attributes);
            default -> null;
        };

        if(snsInfoDTO == null){
            throw new OAuth2AuthenticationException("Invalid SNS");
        }

        UserDTO existsUser = userMapper.getUserByCi(snsInfoDTO.getCi());
        if(existsUser == null){
            throw new OAuth2AuthenticationException("Invalid User");
        }

        boolean isSnsExists = existsUser.getSnsInfo().stream()
                .anyMatch(snsInfo -> snsInfo.getName().equals(clientName));
        if(!isSnsExists){
            existsUser.getSnsInfo().add(snsInfoDTO);
            snsInfoDTO.setUserId(existsUser.getId());
            userMapper.insertSnsInfo(snsInfoDTO);
        }

        return existsUser;

    }

    SnsInfoDTO get_naver_sns_info(Map<String, Object> attributes){
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        String id = response.get("id");
        return SnsInfoDTO.builder()
                .id(id)
                .ci(CI)
                .name("NAVER")
                .build();
    }

    SnsInfoDTO get_kakao_sns_info(Map<String, Object> attributes) {
        String id = attributes.get("id").toString();
        return SnsInfoDTO.builder()
                .id(id)
                .ci(CI)
                .name("KAKAO")
                .build();
    }

    SnsInfoDTO get_google_sns_info(Map<String, Object> attributes) {
        String id = attributes.get("sub").toString();
        log.error(attributes);
        return SnsInfoDTO.builder()
                .id(id)
                .ci(CI)
                .name("GOOGLE")
                .build();
    }
}
