package com.project.service;

import com.project.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserRestService {
    @Autowired private UserMapper userMapper;
    public boolean find_user_by_id(String userId) {
        return Objects.isNull(userMapper.getUserById(userId));
    }
}
