package com.project.controller;

import com.project.dto.FileDTO;
import com.project.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileController {
    @Autowired private FileMapper fileMapper;

    // 일반 문자열, 일반 숫자 이외는 전부 byte[]==2진수 데이터 라고 생각하면 됨
    // 2진수 데이터 == 그림, 영상, 문자, 뭐든지 전부
    @GetMapping("/image/{imageNo}")
    public ResponseEntity<byte[]> get_book_image( // ResponseEntity는 Header, Body, Status Code를 구성해서 응답이 가능합!
            @PathVariable Integer imageNo
    ) {
        FileDTO fileDTO = fileMapper.getImageFileByNo(imageNo);
        byte[] data = fileDTO.getData();

        // header 넣는 거 습관화하기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(data.length);
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
