package com.project.mapper;

import com.project.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM book_image WHERE no = #{imageNo}")
    FileDTO getImageFileByNo(Integer imageNo);
}
