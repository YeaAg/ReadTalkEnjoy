package com.project.controller;

import com.project.dto.*;
import com.project.mapper.AdminMapper;
import com.project.mapper.LoanMapper;
import com.project.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import com.project.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private PortOneService portOneService;
    @Autowired private BookService bookService;
    @Autowired private LoanService loanService;
    @Autowired private DiscussionService discussionService;
    @Autowired private PasswordEncoder passwordEncoder;

    /***********************************************/

    @GetMapping("/join")
    public String get_join(Authentication auth) {
        if (auth != null) {
            System.out.println("회원가입 할 필요 없습니다");
            return "redirect:/";
        }
        return "user/join";
    }

    @PostMapping("/join")
    public String post_join(
            @ModelAttribute @Validated UserDTO joinUser,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) { // 유효성 검사 실패 시
            log.error("에러 발생!");
            log.error(bindingResult.getAllErrors());
            return "user/join";
        }
        // 전화번호 인증 확인 여부
        String impUid = (String) session.getAttribute("impUid");
        if(impUid == null) {
            log.error("전화번호 인증 확인 실패");
            return "user/join";
        }
        // 포트원 인증 통과 여부
        log.info("Tel:" + joinUser.getTel());
        String ci = portOneService.tel_authentication(impUid, joinUser.getTel());
        if(ci == null) {
            log.error("포트원 인증 확인 실패");
            return "user/join";
        }
        joinUser.setCi(ci);

        // 이메일 인증 확인 여부
        String certCompleteEmail = (String) session.getAttribute("emailAuth");
        if(certCompleteEmail == null || !certCompleteEmail.equals(joinUser.getEmail())) {
            log.error("이메일 인증 확인 실패");
            return "user/join";
        }

        log.info("가입할 user" + joinUser);
        boolean signUpResult = userService.join_user(joinUser);
        if (signUpResult) {
            log.info("가입 완료");
            return "redirect:/user/login";
        }
        log.error("원인 모를 이유로 실패");
        return "user/join";
    }

    /************************************************/
    @GetMapping("/find-id")
    public String get_email_auth(){
        return "user/find-id";
    }


    /***********************************************/
    @GetMapping("/login")
    public String get_login(Authentication auth) {
        if (auth != null) {
            System.out.println("로그인 할 필요 없습니다");
            System.out.println("이미 로그인된 유저 : " + auth.getName());
            return "redirect:/";
        }

        System.out.println("로그인 안되어있음");
        return "user/login";
    }


    /***********************************************/
    // 내 회원 정보 메뉴
    @GetMapping("/my-page")
    public String get_my_page(Authentication auth, Model model) {
        if (auth != null) {
            String userId = auth.getName();
            UserDTO user = userService.find_user(userId);
            if (user.getProfileImage() != null) {
                String base64Image = "data:image/jpeg;base64," +
                        java.util.Base64.getEncoder().encodeToString(user.getProfileImage());
                user.setBase64Image(base64Image);
            } else {
                user.setBase64Image("data:image/jpeg;base64,[defaultBase64EncodedImage]");
            }
            model.addAttribute("user", user);
            return "user/my-page";
        }
        return "redirect:/user/login";
    }

    /************************************************/
    @GetMapping("/my-talk")
    public String get_my_discussion(Authentication auth, Model model, PageInfoDTO<DiscussionDTO> pageInfo) {
        if(auth != null) {
            String userId = auth.getName();
            List<DiscussionDTO> myDiscussion =  discussionService.getMyDiscussion(new PageInfoDTO<>(), userId);
            Integer myDiscussionCount = discussionService.getMyDiscussionCount(userId);
            model.addAttribute("myDiscussionCount", myDiscussionCount);
            model.addAttribute("myDiscussion", myDiscussion);
            model.addAttribute("pageInfo", pageInfo);
            return "user/my-talk";
        }
        return "redirect:/user/login";
    }

    /************************************************/
    @GetMapping("/lendbook")
    public String getLendbook(Authentication auth, Model model, PageInfoDTO<LoanDTO> pageInfo) {
        Map<LoanDTO, BookDTO> loanBookMap = new HashMap<>(); // loanBookMap 초기화
        Integer activeLoanCount = 0; // 대여한 권수 기본값 설정

        if (auth != null) {
            String userId = auth.getName();
            loanBookMap = loanService.getActiveLoanByUser(userId);
            activeLoanCount = loanService.getActiveLoanCountByUserId(userId);

            // loanBookMap이 null이면 빈 HashMap으로 초기화 (NullPointerException 방지)
            if (loanBookMap == null) {
                loanBookMap = new HashMap<>();
            }
        }

        model.addAttribute("activeLoanCount", activeLoanCount);
        model.addAttribute("loanBookMap", loanBookMap);
        model.addAttribute("pageInfo", pageInfo);

        return "user/lendbook";
    }


    /************************************************/

    @GetMapping("/wishlist")
    public String getWishlist(Authentication auth, Model model) {
        if (auth != null) {
            String userId = auth.getName();
            UserDTO user = new UserDTO();
            user.setId(userId);

            List<CartDTO> wishlist = bookService.getCartsByUser(user);
            wishlist = wishlist.stream()
                    .filter(cart -> cart != null && cart.getBook() != null)
                    .collect(Collectors.toList()); // Null 요소 제거

            Integer cartCount = bookService.getCartCountByUser(userId);

            model.addAttribute("cartCount", cartCount);
            model.addAttribute("wishlist", wishlist);

            return "user/wishlist";
        }
        return "redirect:/user/login";
    }

    /********* 카트 목록 추가 *************/
    @PostMapping("/wishlist/add")
    public ResponseEntity<?> addBookToWishlist(
            @RequestBody BookDTO book,
            Authentication auth
    ) {
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            log.error("ISBN 값이 누락되었습니다!");
            return ResponseEntity.badRequest().body(Map.of("message", "ISBN 값이 필요합니다."));
        }

        if (auth == null || !auth.isAuthenticated()) {
            log.error("인증되지 않은 사용자가 접근 시도");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        String userId = auth.getName();
        UserDTO user = new UserDTO();
        user.setId(userId);

        bookService.insertBookToCart(book, user);

        return ResponseEntity.ok(Map.of("message", "찜하기 성공"));
    }

    @PostMapping("/wishlist/delete/{cartNo}")
    public String deleteBookFromWishlist(@PathVariable("cartNo") Integer cartNo, RedirectAttributes redirectAttributes) {
        try {
            if (cartNo == null) {
                redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
                return "redirect:/user/wishlist"; // ✅ 실패 시 리다이렉트
            }

            bookService.deleteBooksFromCart(cartNo);
            redirectAttributes.addFlashAttribute("message", "삭제 성공");

            return "redirect:/user/wishlist"; // ✅ 성공 시 리다이렉트
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패");
            return "redirect:/user/wishlist"; // ✅ 예외 발생 시 리다이렉트
        }
    }

    /*************************************************/

    // 회원 정보 수정을 위한 비밀번호 인증
    @GetMapping("/pw-auth")
    public String get_pw_auth(Authentication auth) {
        if (auth != null) {
            return "user/pw-auth";
        }
        return "redirect:/user/login";
    }

    @PostMapping("/pw-auth")
    public String post_pw_auth(
            @AuthenticationPrincipal UserDTO user,
            @RequestParam String password,
            HttpSession session
    ){
        // passwordEncoder.matches()로 비교 (암호화된 비밀번호와 입력된 비밀번호 비교)
        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            log.info("비밀번호 일치");
            session.setAttribute("passwordAuthenticated", true);
            return "redirect:/user/info-revise";
        }
        log.info("비밀번호 불일치");
        return "redirect:/user/login";
    }

    // 회원 정보 수정
    @GetMapping("/info-revise")
    public String get_user_info_revise(
            @AuthenticationPrincipal UserDTO user,
            HttpSession session
    ) {
        if (user == null) {
            return "redirect:/user/login";
        }
        // 비밀번호 인증 여부 체크
        if (session.getAttribute("passwordAuthenticated") == null || !(boolean) session.getAttribute("passwordAuthenticated")) {
            // 비밀번호 인증이 안 되어 있으면 인증 페이지로 리디렉션
            return "redirect:/user/pw-auth";
        }
        // 인증된 후 개인정보 수정 페이지
        return "user/info-revise";
    }

    @PostMapping("/info-revise")
    public String post_user_info_revise(
            @AuthenticationPrincipal UserDTO existUser,
            @ModelAttribute UserDTO updateUser
    ){
        if(existUser == null || !existUser.getName().equals(existUser.getId())){
            return "redirect:/user/login";
        }

        boolean updateResult = userService.updateUser(existUser, updateUser);
        if(updateResult){
            log.info("개인정보 변경 성공");
            return "redirect:/";
        }
        log.info("개인정보 변경 실패");
        return "user/info-revise";
    }

    @GetMapping("/complain")
    public String get_my_complain(Model model,
                                  Authentication auth) {
        if(auth != null) {
            String userId = auth.getName();
            List<ComplainDTO> myComplains = userService.getMyComplains(userId);
            model.addAttribute("myComplains", myComplains);
            return "user/complain";
        }
        return "redirect:/user/login";
    }

    @GetMapping("/write_QA")
    public void write_complain() {
    }

    @PostMapping("/book/return/{bookIsbn}")
    public String returnBook(@PathVariable String bookIsbn, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            loanService.returnBook(bookIsbn, principal.getName());
            redirectAttributes.addFlashAttribute("message", "책이 성공적으로 반납되었습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "책 반납에 실패했습니다.");
        }
        return "redirect:/user/lendbook"; // ✅ 리다이렉트 정상 작동
    }
}