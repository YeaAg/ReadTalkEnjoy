package com.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@Log4j2
public class UserDTO implements UserDetails, OAuth2User {

    @Length(min = 4, max = 15)
    @Pattern(regexp = "^[a-z][0-9a-zA-Z]*$")
    private String id;

    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z~!@#$%^&*()_=+.-]{4,10}")
    private String password;

    private String ci;

    @Pattern(regexp = "^(010|011|017|019|018)[0-9]{3,4}[0-9]{4}$")
    private String tel;

    @Email
    private String email;

    private LocalDateTime joinDate;
    private Integer point;
    private String base64Image;
    private LocalDateTime updatedAt;
    private String role;
    private String nickname;
    private byte[] profileImage;

    private List<SnsInfoDTO> snsInfo; // 이 유저가 로그인 할 때 사용한 SNS 데이터

    // 이메일 저장 시 , → @ 자동 변경
    public void setEmail(String email) {
        this.email = email.replace(",", "@");
    }

    // OAuth2 관련 메서드
    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    // Spring Security 권한 반환 (ROLE_ 추가)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role));

        // ✅ 반환 값 로그 출력
        log.info("UserDTO.getAuthorities() called - Returning: {}", authorities);

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
