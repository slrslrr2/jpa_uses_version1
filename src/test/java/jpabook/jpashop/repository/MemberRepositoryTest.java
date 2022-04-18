package jpabook.jpashop.repository;

import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.fail;


// TEST에 있는 부분은
// application.yml에 설정된 DB를 가지고오게 되는데
// 아무것도 설정 안했을 경우 SpirngBoot는
// jdbc:h2:mem 로, TEST DB 메모리로 띄위준다

// 순수 단위테스트가 아닌 DB까지 엮어지는 메모리모드를 하기 위해 아래 어노테이션 작성
@RunWith(SpringRunner.class)
@SpringBootTest // 스프링컨테이너 안에서 테스트 할 수 있도록 도와준다(@Component 친구들을 스프링 컨테이너에 올려준다)
@Transactional // Transaction 걸고 기본적으로 Rollback한다 (단, Test부분에서만 그럼)
public class MemberRepositoryTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
//    @Rollback(false) // @Transactional은 기본적으로 rollaback을 하기에
//                     // Rollback(false)로 하여 commit을 하게 하여
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kimㅎㅎㅎㅎ");

        // when
        Long saveId = memberService.join(member);

        // then
        em.flush(); // 영속성 컨텍스트에 있는 내용을 DB에 반영한다는 의미
                    // 하지만 @Transactional 에 의해 Rollback을 하게된다.
        /**
         * 2022-04-17 18:10:46.681  INFO 1111 --- [           main] o.s.t.c.
         * wtransaction.TransactionContext   : Rolled back transaction for test:  ~~~ 블라블라
         */
        Assert.assertEquals(member, memberRepository.findOne(saveId));
    }

    // 결과로 예외가 발생해야 성공하는 테스트임을 지정하는것
    @Test(expected = IllegalArgumentException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member);
//        try {
        memberService.join(member2);
//        } catch (IllegalArgumentException e){
//            return;
//        }

        // then
        fail("예외가 발생해야 한다.");
    }
}