package com.project.mapper;

import com.project.dto.*;
import jakarta.validation.constraints.Pattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface UserMapper {
    void createUser(UserDTO user);
    UserDTO getUserById(@Param("id") String id);
    UserDTO getUserByCi(@Param("ci") String ci);
    void updateUser(UserDTO userDTO);
    void insertSnsInfo(SnsInfoDTO snsInfo);
    void addPointToUser(@Param("userId") String userId, @Param("points") Integer points);
    Integer deductPoints(@Param("userId") String userId, @Param("points") Integer points);
    List<Integer> getAllDiscussionsByUser(@Param("userId") String userId);
    void insertComplain(ComplainDTO complain);
    String findIdByEmail(@Param("email") String email);
    List<ComplainDTO> getComplains();
    List<ComplainDTO> getMyComplains(@Param("userId") String userId);
    @Select("SELECT * FROM complain WHERE no = #{no}")
    ComplainDTO getComplainByNo(@Param("no") Integer no);
}
