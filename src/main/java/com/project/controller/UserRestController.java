package com.project.controller;

import com.project.dto.BookDTO;
import com.project.dto.LoanDTO;
import com.project.dto.UserDTO;
import com.project.service.BookService;
import com.project.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import com.project.mapper.UserMapper;
import com.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserRestController {
    @Autowired private UserMapper userMapper;
    @Autowired private UserService userService;
    @Autowired private EmailService emailService;
    private final Map<String, String> emailCertRepository = new HashMap<>();
    @Autowired
    private BookService bookService;

    /*******************************************/
    @GetMapping("/id/{userId}")
    public ResponseEntity<Void> find_user_by_id(
            @PathVariable String userId
    ) {
        if(userMapper.getUserById(userId) == null){ // 중복 아니면 200
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).build(); // 중복이면
    }

    /**********************************************************/
    // 전화번호 인증
    @PostMapping("/tel/auth")
    public ResponseEntity<Void> post_tel_auth(
            @RequestBody String impUid,
            HttpSession session
    ) {
        session.setAttribute("impUid", impUid);
        return ResponseEntity.ok().build();
    }
    /*********************************************************/
    // 이메일 인증 확인 버튼 클릭
    @GetMapping("/email/auth")
    public ResponseEntity<Void> get_email_auth(
            @RequestParam String email,
            @RequestParam String certNumber,
            HttpSession session
    ) {
        if(certNumber == null || !emailCertRepository.get(email).equals(certNumber)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("이메일 인증 통과");
        session.setAttribute("emailAuth", email); // 회원가입 submit 검증할 때 필요
        return ResponseEntity.ok().build();
    }

    // 이메일 인증 버튼 클릭
    @PostMapping("/email/auth")
    public ResponseEntity<Void> post_email_auth(
            @RequestBody String email_to,
            @RequestParam String requestAuth,
            @RequestParam(required = false) String id
    ){
        try{
            String certNumber = emailService.send_cert_mail(email_to, requestAuth, id);
            emailCertRepository.put(email_to, certNumber);
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            log.error("인증 이메일 전송 중 오류 발생... : " + e.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }

    /**********************************************************/
    @GetMapping("/findId/{email}")
    public ResponseEntity<String> get_findId_by_email(
            @PathVariable String email
    ) {
        log.info("email: " + email);
        String foundId = userMapper.findIdByEmail(email);
        log.info("foundId: " + foundId);
        if(foundId != null){
            return ResponseEntity.status(HttpStatus.FOUND).body(foundId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/resetPw/password")
    public ResponseEntity<Void> post_reset_password(
            Authentication auth,
            @RequestBody String password
    ){
        String id = auth.getName();
        userService.reset_password(id, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /***************************************************/
    @PostMapping("/info")
    public ResponseEntity<Void> post_user_info(
            @RequestBody Integer bookIsbn
    ){
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}