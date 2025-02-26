package com.project.service;

import com.project.dto.*;
import com.project.mapper.BookMapper;
import com.project.mapper.LoanMapper;
import com.project.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private BookMapper bookMapper;
    @Autowired private LoanMapper loanMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private Validator validator;

    public UserDTO find_user(String userId) {
        return userMapper.getUserById(userId);
    }

    public boolean join_user(UserDTO joinUser) {
        UserDTO findUser = userMapper.getUserById(joinUser.getId());
        if (findUser != null) {
            log.error("이미 회원가입이 되어있습니다.");
            return false;
        }

        String encodedPassword = passwordEncoder.encode(joinUser.getPassword());
        joinUser.setPassword(encodedPassword);
        userMapper.createUser(joinUser);
        return true;
    }

    public boolean reset_password(String id, String newPw) {
        log.info("reset_password newPw : " + newPw);
        boolean pwPatternResult = newPw.matches("^[0-9a-zA-Z~!@#$%^&*()_=+.-]{4,10}");
        if (!pwPatternResult) {
            log.info("비밀번호 조건 실패");
            return false;
        }

        UserDTO findUser = userMapper.getUserById(id);
        findUser.setPassword(passwordEncoder.encode(newPw));
        userMapper.updateUser(findUser);
        log.info("패스워드 변경 성공");
        return true;
    }

    public boolean updateUser(UserDTO existUser, UserDTO updateUser) {
        boolean updated = false;

        // 1. 이메일 변경
        if (updateUser.getEmail() != null && !updateUser.getEmail().trim().isEmpty() &&
                !updateUser.getEmail().equals(existUser.getEmail())) {
            existUser.setEmail(updateUser.getEmail());
            updated = true;
        }

        // 2. 비밀번호 변경
        if (updateUser.getPassword() != null && !updateUser.getPassword().trim().isEmpty() &&
                !updateUser.getPassword().equals(existUser.getPassword())) {
            existUser.setPassword(updateUser.getPassword());
            updated = true;
        }

        // 3. 전화번호 변경
        if (updateUser.getTel() != null && !updateUser.getTel().trim().isEmpty() &&
                !updateUser.getTel().equals(existUser.getTel())) {
            existUser.setTel(updateUser.getTel());
            updated = true;
        }

        // 4. 프로필 이미지 변경 (byte[] 비교)
        if (updateUser.getProfileImage() != null && updateUser.getProfileImage().length > 0 &&
                !Arrays.equals(updateUser.getProfileImage(), existUser.getProfileImage())) {
            existUser.setProfileImage(updateUser.getProfileImage());
            updated = true;
        }

        // 5. 닉네임 변경
        if (updateUser.getNickname() != null && !updateUser.getNickname().trim().isEmpty() &&
                !updateUser.getNickname().equals(existUser.getNickname())) {
            existUser.setNickname(updateUser.getNickname());
            updated = true;
        }

        // 6. 포인트 변경
        if (updateUser.getPoint() != null && !updateUser.getPoint().equals(existUser.getPoint())) {
            existUser.setPoint(updateUser.getPoint());
            updated = true;
        }

        // 7. base64 이미지 변경
        if (updateUser.getBase64Image() != null && !updateUser.getBase64Image().trim().isEmpty() &&
                !updateUser.getBase64Image().equals(existUser.getBase64Image())) {
            existUser.setBase64Image(updateUser.getBase64Image());
            updated = true;
        }

        // 8. SNS 정보 변경
        if (updateUser.getSnsInfo() != null && !updateUser.getSnsInfo().equals(existUser.getSnsInfo())) {
            existUser.setSnsInfo(updateUser.getSnsInfo());
            updated = true;
        }

        if(updated){
            // 유효성 검사를 위한 BindingResult 준비
            BindingResult bindingResult = new BeanPropertyBindingResult(existUser, "existUser");
            log.info("existUser: " + existUser);
            // 유효성 검사 실행
            validator.validate(existUser, bindingResult);

            // 유효성 검사 오류가 있는 경우
            if (bindingResult.hasErrors()) {
                // 유효성 검사 오류 처리 (예: 오류 메시지 기록, 예외 처리 등)
                bindingResult.getAllErrors().forEach(error -> {
                    System.out.println(error.getDefaultMessage());
                });
                return false;  // 유효성 검사가 실패하면 업데이트를 진행하지 않음
            }
            log.info("수정 검사 통과");
            existUser.setPassword(passwordEncoder.encode(existUser.getPassword()));
            userMapper.updateUser(existUser);
        }

        // 최종적으로 하나라도 업데이트된 경우 true 리턴
        return updated;
    }

    public void deductPoints(String userId, Integer points) {
        Integer updatedRows = userMapper.deductPoints(userId, points);
        if (updatedRows == 0) {
            throw new IllegalStateException("포인트가 부족하여 차감에 실패했습니다.");
        }
    }

    public void createComplain(String title, String contents, String userId) {
        ComplainDTO complain = new ComplainDTO();
        complain.setTitle(title);
        complain.setContents(contents);
        complain.setUserId(userId);
        userMapper.insertComplain(complain);
        log.info("컴플레인이 성공적으로 생성되었습니다: {}", complain);
    }

    public List<ComplainDTO> getComplains() {
        return userMapper.getComplains();
    }

    public List<ComplainDTO> getMyComplains(String userId) {
        return userMapper.getMyComplains(userId);
    }

    public ComplainDTO getComplainByNo(Integer no) {
        return userMapper.getComplainByNo(no);
    }
}
