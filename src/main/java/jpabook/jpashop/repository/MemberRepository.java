package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

// 스프링컨테에너에 빈 등록
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    // springBoot 가 @Autowired로도 주입되도록 만들어줬음.
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    // jpql이 insert가 있었나?
    // jpql실행 시 flush가 자동으로 호출되니까
    // TEST코드에서 실행하면 insert문은 나가겠지만 rollbak은 되겠다

    // TEST 시 INSERT 안나가고 ROLLBACK했으니까

    /**
     *  flush 날리면 영속성 Context에 sql문이 생성되면서 Query가 나간다.
     *  MemberRepositoryTest.회원가입 할 경우 안나갔고 flush() 하니까 insert 나감

     * 정리 : flush 호출 조건
     *      1. em.flush()
     *      2. jpql ==> 로 insert 날리면 insert문 나가겠지? flush를 안써도!
     *      3. commit
     */
}
