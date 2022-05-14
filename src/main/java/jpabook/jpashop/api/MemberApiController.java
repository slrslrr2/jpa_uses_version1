package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     List 는 Member를 바로 return할 경우,
     사용자에게 모두 Entity를 노출하게되므로 안됩니다!
     스펙이 고정되면 확장성이 떨어진다
     */
    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(map -> new MemberDto(map.getName())) //MemberDto를 사용하면서 필요한 값만 Return되도록 한다.
                .collect(Collectors.toList());

        /**
         List를 Return할 경우 Result로 한번 감싸는 이유?
         미래 count추가 등 확장을 고려해주기 위해
         */
        // return new Result(collect.size(), collect)
        return new Result(collect);

    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        // private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /**
     * 엔티티는 Controller에서 직접 사용하는게 아닌
     * 별도의 DTO를 사용하는것이 좋다.
     *  ==> V2를 만들것음
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMember1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     Request할 때의 별도 Entity를 만들었다. [CreateMemberResponse]
        Entity와 API스펙을 명확히 구분할 수 있다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName()); // command후 수정할때 id정도만 날림..(여기선 Response객체 만듦)
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }
}
