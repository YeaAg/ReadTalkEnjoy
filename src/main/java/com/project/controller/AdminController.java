package com.project.controller;

import com.project.dto.*;
import com.project.mapper.AdminMapper;
import com.project.mapper.BookMapper;
import com.project.mapper.UserMapper;
import com.project.service.AdminService;
import com.project.service.BookService;
import com.project.service.UserService;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LogManager.getLogger(AdminController.class);
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    // ok
    @GetMapping("/manager")
    public String manager(Model model) {
//        List<UserDTO> users = adminService.getAllUser();
        List<UserDTO> updatedUsers = adminService.getRecentlyUpdatedUsers();
        List<BookDTO> books = bookService.getAllBooks();
        List<ComplainDTO> complains = userService.getComplains();
        List<UserDTO> publicUsers = adminService.getPublicUser();
        model.addAttribute("publicUsers", publicUsers);
        model.addAttribute("books", books);
        model.addAttribute("updatedUsers", updatedUsers);
//        model.addAttribute("users", users);
        model.addAttribute("complains", complains);
        return "manager/manager";
    }

    /******************* 책 관련 *************************/
    // ok
    @DeleteMapping("/book/delete")
    public ResponseEntity<String> deleteBook(@AuthenticationPrincipal UserDTO user,
                                             @RequestBody Map<String, String> payload) {
        if (user.getRole().equals("관리자")) {
            String bookIsbn = payload.get("bookIsbn");

            if (bookIsbn == null) {
                return ResponseEntity.badRequest().body("ISBN이 제공되지 않았습니다.");
            }
            log.info("삭제 요청된 책 ISBN: " + bookIsbn);
            return ResponseEntity.ok("책 삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
        }
    }

    /******************* 유저 관련 ********************/
    // ok
    @PatchMapping("/update-user")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDTO user, @RequestBody Map<String, String> payload) {
        if (!user.getRole().equals("관리자")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        String userId = payload.get("userId");
        adminService.promoteToAdmin(userId);
        return ResponseEntity.ok("유저 승격 완료");
    }

    // ok
    @DeleteMapping("/drop-user")
    public ResponseEntity<String> dropUser(@AuthenticationPrincipal UserDTO user, @RequestBody Map<String, List<String>> payload) {
        if (user.getRole().equals("관리자")) {
            List<String> userIds = payload.get("userIds");
            for (String userId : userIds) {
                adminService.deleteUser(userId);
                log.error("삭제된 유저 ID : " + userId);
            }
            return ResponseEntity.ok("유저 삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
        }
    }

    /*************** 유저 컴플레인 답글 작성 ********************/
    @PostMapping("/update/answer")
    public ResponseEntity<String> updateAnswer(@AuthenticationPrincipal UserDTO user, @RequestBody Map<String, String> payload) {
        if (user.getRole().equals("관리자")) {
            if (payload == null || !payload.containsKey("complainNo") || !payload.containsKey("answer")) {
                return ResponseEntity.badRequest().body("잘못된 요청 데이터입니다.");
            }
            try {
                Integer complainNo = Integer.parseInt(payload.get("complainNo"));
                String answer = payload.get("answer");
                adminService.answerToUser(complainNo, answer);
                return ResponseEntity.ok("답변이 업데이트되었습니다.");
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("complainNo가 숫자가 아닙니다.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("서버 오류 발생 : " + e.getMessage());
            }
        }
        return ResponseEntity.status(403).body("권한이 없습니다.");
    }
}