package com.project.controller;


import com.project.dto.DiscussionDTO;
import com.project.service.DiscussionService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import com.project.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@PropertySource("classpath:my.properties")
public class MainRestController {
    @Autowired private DiscussionService discussionService;

    private final String IMP_INIT;


    public MainRestController(@Value("${IMP_INIT}") String impInit) {
        if (impInit == null) {
            log.error("IMP_INIT 값이 null입니다. my.properties 또는 환경 변수 설정을 확인하세요.");
        }
        IMP_INIT = impInit;
        log.info("impInit: " + IMP_INIT);
    }


    @GetMapping("/imp_init")
    ResponseEntity<String> get_imp_init() {
        return ResponseEntity.status(HttpStatus.OK).body(IMP_INIT);
    }
}
