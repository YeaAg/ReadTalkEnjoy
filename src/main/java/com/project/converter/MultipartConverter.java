package com.project.converter;

import com.project.dto.BookImageDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class MultipartConverter implements Converter<MultipartFile, BookImageDTO> {
    @Override
    public BookImageDTO convert(MultipartFile multipartFile) {
        try {
            log.error("전송된 파일명 : " + multipartFile.getOriginalFilename());
            log.error("전송된 파일의 크기 : " + multipartFile.getSize());
            log.error("전송된 파일의 형태 : " + multipartFile.getContentType());
            String originalFilename = multipartFile.getOriginalFilename();
            byte[] data = multipartFile.getBytes();
            return BookImageDTO.builder()
                    .originalFilename(originalFilename)
                    .data(data)
                    .build();
        } catch (Exception e) {
            log.error("파일 변환 중 오류 발생 : " + e.getMessage());
        }
        return null;
    }
}
