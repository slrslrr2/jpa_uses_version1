package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberservice;

    @GetMapping("/members/new")
    public String createForm(Model model){
        // form에 MemberForm()을 넣어주어
        // 타임리프에서 해당 폼을 인식하여 Validation 체크 해줄 수 있도록 도와준다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    /**
     @Valid : MemberForm안에 선언한 어노테이션 Validation을 체크한다.
     MemberForm 뒤에 BindingResult를 선언하면,
        form안에 오류를 흡수하여 Controller안에 코드가 실행된다.
        타임리프 안에서는 #fields로 BindingResult안에 접근 가능하다.
            ex) ${#fields.hasErrors('name')}
     */
    public String crete(@Valid MemberForm form, BindingResult result) {
        System.out.println("result = " + result);

        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberservice.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberservice.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
