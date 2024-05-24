package proj.tricount.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import proj.tricount.domain.user.LoginForm;
import proj.tricount.domain.user.Member;
import proj.tricount.service.MemberService;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login/loginForm")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login/loginForm";
    }

    // 로그인
    @PostMapping("/login/loginForm")
    public String login(@Validated @ModelAttribute LoginForm loginForm, HttpSession httpSession, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        Optional<Member> optionalMember = Optional.ofNullable(memberService.getMemberByLoginId(loginForm.getLoginId()));
        if (optionalMember.isPresent() && optionalMember.get().getPassword().equals(loginForm.getPassword())) {
            httpSession.setAttribute("member", optionalMember.get());
            httpSession.setAttribute("isLogin",true);
            return "redirect:/";
        } else {
            log.info("로그인 실패");
            return "login/loginForm";
        }
    }

    @GetMapping("/member/register")
    public String showRegister(@ModelAttribute Member member) {
        return "member/register";
    }

    @PostMapping("/member/register")
    public String registerProcess(@ModelAttribute Member member){
        memberService.registerMember(member);
        log.info("회원가입 성공");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }

    // 회원가입
//    @PostMapping("/member/register")
    @ResponseBody
    public ResponseEntity<Object> register(@ModelAttribute Member member) {
        try {
            memberService.registerMember(member);
            log.info("회원가입 성공");
            return ResponseEntity.ok(member);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
