package com.project.mapper;

import com.project.dto.BookDTO;
import com.project.dto.CartDTO;
import com.project.dto.LoanDTO;
import com.project.dto.PageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoanMapper {
    void createLoan(LoanDTO loan);
    Integer getActiveLoanCountByUserId(@Param("userId") String userId);
    Integer getAvailableCopies(@Param("bookIsbn") String bookIsbn); // Integer → String
    void decreaseCopiesAvailable(@Param("bookIsbn") String bookIsbn); // Integer → String
    List<LoanDTO> getActiveLoanByUserAndBook(@Param("userId") String userId); // Integer → String
    LocalDateTime getFirstReturnDateByBookIsbn(@Param("isbn") String isbn);
    Integer getUserPoints(@Param("userId") String userId);
    void deductUserPoints(@Param("userId") String userId, @Param("points") Integer points);
    void returnBook(@Param("bookIsbn") String bookIsbn, @Param("userId") String userId);
}

