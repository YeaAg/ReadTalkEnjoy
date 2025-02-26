package com.project.service;

import com.project.dto.AdminPostDTO;
import com.project.dto.BookDTO;
import com.project.dto.UserDTO;
import com.project.mapper.AdminMapper;
import com.project.mapper.BookMapper;
import com.project.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 관리자 권한 부여
     */
    public void promoteToAdmin(String userId) {
        adminMapper.createAdmin(userId);
        log.info("사용자 {}에게 관리자 권한을 부여했습니다.", userId);
    }

    /**
     * 최근 1주일 내 정보를 변경한 사용자 조회
     */
    public List<UserDTO> getRecentlyUpdatedUsers() {
        List<UserDTO> updatedUsers = adminMapper.getUpdatedUser();
        log.info("최근 1주일 동안 업데이트된 사용자 목록을 조회했습니다. 총 {}명의 사용자가 있습니다.", updatedUsers.size());
        return updatedUsers;
    }

    /**
     * 유저 삭제
     */
    public void deleteUser(String userId) {
        adminMapper.deleteUser(userId);
    }

    public void answerToUser(Integer complainNo, String answer) {
        adminMapper.answerToUser(complainNo, answer);
    }

    public List<UserDTO> getPublicUser() {
        return adminMapper.getPublicUser();
    }
}
