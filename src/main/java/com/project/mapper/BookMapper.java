package com.project.mapper;

import com.project.dto.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    List<BookDTO> getAllBooks();
    List<BookDTO> searchBooksByNameWithCount(@Param("pageInfo") PageInfoDTO<BookDTO> pageInfo,@Param("title") String title);
    BookDTO getBookByIsbn(@Param("isbn") String isbn);
    List<BookDTO> getPopularBook();
    Integer getDiscussionCountByBookIsbn(@Param("isbn") String isbn);
    Integer getParticipantCountByBookIsbn(@Param("isbn") String isbn);
    List<ReviewDTO> selectPaginatedReviewsByBookIsbn(@Param("pageInfo") PageInfoDTO<ReviewDTO> pageInfo, @Param("isbn") String isbn);
    @MapKey("rate")
    Map<String, Map<String, Object>> selectPaginatedReviewTotalCountByIsbn(@Param("isbn")String isbn);
    Integer selectPaginatedBooksTotalCount(@Param("pageInfo")PageInfoDTO<BookDTO> pageInfo);
    List<BookDTO> getPaginatedBooks(@Param("pageInfo") PageInfoDTO<BookDTO> pageInfo);
    List<BookDTO> getPopularBook5();
    List<BookDTO> getPopularBook2();
    List<CartDTO> selectCartsByUser(UserDTO user);
    void insertBookToCart(@Param("cart") CartDTO cart, @Param("user") UserDTO user);
    void deleteBookFromCart(@Param("cartNo") Integer cartNo);
    Integer selectCartCountByUser(@Param("userId")String userId);
    List<CategoryDTO> selectCategoryByIsbn(@Param("isbn") String isbn);
    void insertReview(@Param("userId")String userId, @Param("isbn") String isbn, @Param("content")String content, @Param("rate") Integer rate);
    // 제목으로 검색된 총 책 개수 조회
    Integer getTotalBookCountByTitle(@Param("title") String title);
    Integer plusReviewLike(@Param("bookIsbn") String bookIsbn, @Param("content") String content, @Param("userId") String userId);
}
