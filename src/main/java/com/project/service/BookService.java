package com.project.service;

import com.project.dto.*;
import com.project.mapper.BookMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger log = LogManager.getLogger(BookService.class);

    @Autowired
    private BookMapper bookMapper;

    /**
     * 모든 책 리스트 조회
     */
    public List<BookDTO> getAllBooks() {
        return bookMapper.getAllBooks();
    }

    /**
     * 책 제목으로 검색
     */
    public PageInfoDTO<BookDTO> searchBooksByNameWithCount(PageInfoDTO<BookDTO> pageInfo, String title) {
        List<BookDTO> result = bookMapper.searchBooksByNameWithCount(pageInfo, title);
        Integer totalElementCount = bookMapper.getTotalBookCountByTitle(title); // 총 개수 가져오기
        pageInfo.setTotalElementCount(totalElementCount);
        pageInfo.setElements(result);
        return pageInfo;
    }

    /**
     * ISBN으로 책 조회
     */
    public BookDTO getBookByIsbn(String isbn) {
        try {
            return bookMapper.getBookByIsbn(isbn);
        } catch (Exception e) {
            log.error("Error while fetching book by ISBN: {}", isbn, e);
            throw new RuntimeException("Failed to fetch book by ISBN. Please try again later.");
        }
    }

    /**
     * 평균 평점이 가장 높은 책 조회
     */
    public List<BookDTO> getPopularBook() {
        return bookMapper.getPopularBook();
    }

    /**
     * 평균 평점이 높은 상위 5권 조회
     */
    public List<BookDTO> getPopularBook5() {
        return bookMapper.getPopularBook5();
    }

    /**
     * 평균 평점이 높은 상위 2권 조회
     */
    public List<BookDTO> getPopularBook2() {
        return bookMapper.getPopularBook2();
    }

    /**
     * 특정 책의 토론 주제 개수 조회
     */
    public Integer getDiscussionCountByBookIsbn(String isbn) {
        return bookMapper.getDiscussionCountByBookIsbn(isbn);
    }

    /**
     * 특정 책의 토론 참여자 수 조회
     */
    public Integer getParticipantCountByBookIsbn(String isbn) {
        return bookMapper.getParticipantCountByBookIsbn(isbn);
    }

    /**
     * 페이징된 책 리스트 반환
     */
    public PageInfoDTO<BookDTO> getPaginatedBooks(PageInfoDTO<BookDTO> pageInfo) {
        // 기본값 설정: 페이지 번호와 페이지 크기
        if (pageInfo.getPage() < 1) {
            pageInfo.setPage(1);
        }
        if (pageInfo.getSize() == null || pageInfo.getSize() <= 0) {
            pageInfo.setSize(5); // 기본 페이지 크기: 5개
        }

        // 총 책 개수 조회
        Integer totalBookCount = bookMapper.selectPaginatedBooksTotalCount(pageInfo);

        if (totalBookCount != null && totalBookCount > 0) {
            // 페이징된 책 리스트 조회
            List<BookDTO> books = bookMapper.getPaginatedBooks(pageInfo);
            pageInfo.setTotalElementCount(totalBookCount);
            pageInfo.setElements(books);
        }

        return pageInfo;
    }

    /**
     * 페이징된 리뷰 리스트와 통계 정보 반환
     */
    public Map<String, Map<String, Object>> getPaginatedReviews(PageInfoDTO<ReviewDTO> pageInfo, String isbn) {
        pageInfo.setSize(3);

        if (pageInfo.getPage() < 1) {
            return null;
        }
        // 총 리뷰 개수 가져오기
        Map<String, Map<String, Object>> result = bookMapper.selectPaginatedReviewTotalCountByIsbn(isbn);
        log.error(result);
        if (!result.isEmpty()) {
            Integer totalElementCount = Integer.parseInt(result.get("result").get("count").toString());
            // 리뷰 목록 가져오기
            List<ReviewDTO> reviews = bookMapper.selectPaginatedReviewsByBookIsbn(pageInfo, isbn);
            // 🔥 모든 리뷰 객체에 대해 Base64 변환 실행
            for (ReviewDTO review : reviews) {
                review.setReviewImage(review.getUserImage()); // byte[] → Base64 변환 실행
            }
            // ✅ pageInfo에 변환된 리뷰 목록 저장
            pageInfo.setTotalElementCount(totalElementCount);
            pageInfo.setElements(reviews);
        }
        return result;
    }


    /**
     * 유저의 장바구니 상품 조회
     */
    public List<CartDTO> getCartsByUser(UserDTO user) {
        List<CartDTO> wishlist = bookMapper.selectCartsByUser(user);

        // Null 요소 제거
        wishlist = wishlist.stream()
                .filter(cart -> cart != null && cart.getBook() != null)
                .collect(Collectors.toList());

        return wishlist;
    }


    public Integer getCartCountByUser(String userId) {
        return bookMapper.selectCartCountByUser(userId);
    }

    /**
     * 특정 책을 카트에 추가
     */
    public CartDTO insertBookToCart(BookDTO book, UserDTO user) {
        if (book == null || user == null) {
            throw new IllegalArgumentException("책 정보나 사용자 정보가 null입니다.");
        }
        log.info("Book Info: {}", book);
        log.info("User Info: {}", user);

        CartDTO cart = new CartDTO();
        cart.setBook(book);
        cart.setUser(user);

        bookMapper.insertBookToCart(cart, user); // MyBatis 매퍼 호출
        return cart;
    }


    /**
     * 특정 책을 카트에서 삭제
     */
    public void deleteBooksFromCart(Integer cartNo) {
        try {
            bookMapper.deleteBookFromCart(cartNo);
        } catch (Exception e) {
            throw new RuntimeException("찜 목록 삭제에 실패했습니다.");
        }
    }

    public List<CategoryDTO> getCategoryHierarchyByIsbn(String isbn) {
        List<CategoryDTO> categoryHierarchy = bookMapper.selectCategoryByIsbn(isbn);
        log.error(categoryHierarchy);
        return categoryHierarchy;
    }

    public void insertReview(String userId, String isbn, String content, Integer rate) {
        try {
            System.out.println("[DEBUG] SQL 실행 전 - User: " + userId + ", ISBN: " + isbn + ", Content: " + content + ", Rate: " + rate);
            bookMapper.insertReview(userId, isbn, content, rate);
            System.out.println("[DEBUG] SQL 실행 완료!");
        } catch (Exception e) {
            System.err.println("[ERROR] 리뷰 저장 중 오류 발생!");
            e.printStackTrace(); // 🔥 에러 출력
            throw e; // 예외 다시 던짐
        }
    }

    public Integer plusReviewLike(String bookIsbn, String content, String userId) {
        return bookMapper.plusReviewLike(bookIsbn, content, userId);
    }
}
