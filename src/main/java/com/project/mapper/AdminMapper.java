package com.project.mapper;

import com.project.dto.AdminPostDTO;
import com.project.dto.BookDTO;
import com.project.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    void createAdmin(@Param("userId") String userId);
    List<UserDTO> getUpdatedUser();
    void deleteUser(@Param("id") String userId);
    void answerToUser(@Param("complainNo") Integer complainNo, @Param("answer") String answer);
    List<UserDTO> getPublicUser();
}
