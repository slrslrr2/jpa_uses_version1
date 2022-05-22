# 1. API 개발 기본

## 1. 회원등록 API



```java
@RestController // @Controller, @ResponseBody
@RequiredArgsConstructor // 생성자 의존성 주입 함수 자동 생성
public class MemberApiController {
    private final MemberService memberService;
```

```java
 @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMember1(@RequestBody @Valid Member member){
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }
```

> @RequestBody
>
> - JSON application/json으로 데이터 전송 시 Body에 데이터 담아짐
> - HTTP 요청 메시지를 받을 때 사용하는 어노테이션

- @Valid

- 유저 데이터에서 보낸 데이터는 @RequestBody를 통해  Member에 바인딩되는데<br>해당 객체로 들어가보면 @NotEmpty와 같은 밸리데이션 체크 로직을 체크하여 BindingResult에 해당 데이터를 담아줄 수 있다.

  - ```java
    @Entity
    @Getter @Setter
    public class Member {
        @Id
        @GeneratedValue
        @Column(name = "member_id")
        private Long id;
    
        @NotEmpty
        private String name;
    
        @Embedded
        private Address address;
    
        @OneToMany(mappedBy = "member")
        List<Order> orders = new ArrayList<>();
    }
    ```





##### @Valid를 사용하지 않은 경우

```java
@PostMapping("/api/v1/members")
public CreateMemberResponse saveMember1(@RequestBody Member member){
  Long id = memberService.join(member);
  return new CreateMemberResponse(id);
}
```

> {<br>    "timestamp": "2022-05-22T11:51:09.848+00:00",<br>    "status": 500,<br>    "error": "Internal Server Error",<br>    "trace": "org.springframework.transaction.TransactionSystemException: Could not commit JPA transaction; ~~~~~<br>   "message": "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction",<br>   "path": "/api/v1/members"<br>}

> 다음과 같이 500Exception을 만들어준다.



##### @Valid를 사용한 경우

```java
@PostMapping("/api/v1/members")
public CreateMemberResponse saveMember1(@RequestBody Member member){
  Long id = memberService.join(member);
  return new CreateMemberResponse(id);
}
```

> 400 Error로 던져주며, error Message와 ErrorCode도 자동으로 만들어준다.

>{

​    "timestamp": "2022-05-22T11:50:30.704+00:00",<br>.     "status": 400,<br>.     "error": "Bad Request",<br>.     "trace": "org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public jpabook.jpashop.api.MemberApiController$CreateMemberResponse jpabook.jpashop.api.MemberApiController.saveMember1(jpabook.jpashop.domain.Member): [Field error in object 'member' on field 'name': rejected value [null]; codes [NotEmpty.member.name,NotEmpty.name,NotEmpty.java.lang.String,NotEmpty]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.name,name]; arguments []; default message [name]]; default message [비어 있을 수 없습니다]], <br>.      "message": "Validation failed for object='member'. Error count: 1",<br>.      "errors": [<br>.      {<br>.            "codes": [<br>                     "NotEmpty.member.name",<br>                      "NotEmpty.name",<br>                     "NotEmpty.java.lang.String",<br>                      "NotEmpty"<br>              ],

​            "arguments": [<br>                     {<br>                      "codes": [<br>                             "member.name",<br>                               "name"<br>                      ],<br>                     "arguments": **null**,<br>                      "defaultMessage": "name",<br>                       "code": "name"<br>                }<br>            ],

​            "defaultMessage": "비어 있을 수 없습니다",<br>                 "objectName": "member",<br>                  "field": "name",<br>                  "rejectedValue": **null**,<br>                  "bindingFailure": **false**,<br>                  "code": "NotEmpty"<br>            }<br>     ],<br>"path": "/api/v1/members"<br>}



```java
@PostMapping("/api/v2/members")
public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request){
    Member member = new Member();
    member.setName(request.getName());

    Long id = memberService.join(member); // CreateMemberRequest를 Member객체로 변환해서 join
    return new CreateMemberResponse(id);
}
```

> Request할 때의 별도 Entity를 만들었다. [CreateMemberResponse]
>     Entity와 API스펙을 명확히 구분할 수 있다.



## 2. 회원수정 API

```java
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
```

> ```java
> @Transactional
> public void update(Long id, String name){
>     Member member = memberRepository.findOne(id);
>     member.setName(name); // findOne한 후 영속성 컨텍스트에 넣어서
>   												// 특정한 값만 변경되는 
>   												// 수정 시 변경감지를 사용해야한다
> }
> ```

> @PathVariable
>
> @RequestBody @Valid UpdateMemberRequest request
>
> @AllArgsConstructor



## 3. 회원조회 API

```java
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
```



-----

# 2. API 개발 고급 - 준비

## 1. 조회용 샘플 데이터 입력

```java
@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);
            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);
            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);
            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);
            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            Delivery delivery = createDelivery(member);
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
```

```java
@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }
```

> @RequiredArgsConstructor
>
> @PostConstruct를 통해 InitDb라는 객체가 싱글톤 컨테이너에 들어간 후 <br>@PostConstruct를 사용해서 데이터를 초기화한다.<br>의존성 주입이 일어나기 전에 초기데이터를 셋팅할 때 사용하는 어노테이션

---------

# API 개발 고급 - 지연 로딩과 조회 성능 최적화

주문 + 배송정보 + 회원을 조회하는 API를 만들자. <br>지연 로딩 때문에 발생하는 성능 문제를 단계적으로 해결해보자.

<img width="833" alt="image-20220522212930427" src="https://user-images.githubusercontent.com/58017318/169703963-a671f306-24a9-46a7-a4bd-40d76ee6b3cc.png">

# 간단한 주문 V1: 엔티티를 직접 노출



```java
@GetMapping("/api/v1/simple-orders")
public List<Order> ordersV1(){
  // "select o From Order o join o.member m";
  List<Order> all = orderRepository.findAllByString(new OrderSearch());
  return all;
}
```



<img width="1306" alt="image-20220522214334582" src="https://user-images.githubusercontent.com/58017318/169703968-00842130-8466-4043-9e4a-7b55428b196c.png">


**"select o From Order o join o.member m";** 쿼리가 실행되면서 Order을 통해 Member를 가지고오려고 한다.<br>이 때, Order 안에 Member를 가지고 오려고 하고,<br>Member 안에는 Orders가 있어서<br>Orders안에 Member를 또 가지고 오려는 무한루프가 발생한다.

즉, StackOverFlow 오류가 발생

<img width="1242" alt="image-20220522215025671" src="https://user-images.githubusercontent.com/58017318/169703971-11c14539-4bd2-429b-be13-a0e720b60487.png">

**해결방법** 양방향 연관관계에서 select 조회 시 **@jsonignore**을 조회하는 반대쪽 방향에 선언해준다.

> @JsonIgnore는 직렬화 역직렬화에 사용되는 논리적 프로퍼티(속성..) 값을 무시할때 사용됩니다.

<img width="1214" alt="image-20220522220515107" src="https://user-images.githubusercontent.com/58017318/169703973-ee554d0e-8656-4919-8bc4-d712df9df075.png">


 그러고 다시한번 돌려보면 또 아래와 같은 오류가 난다.???????????????? 

프록시 객체 Member = ByteBuddy ?????

<img width="856" alt="image-20220522221526308" src="https://user-images.githubusercontent.com/58017318/169703975-3b1dae4a-9264-4c3c-8975-3f2275c69b7b.png">

**Hibernate5Module** **등록** JpashopApplication 에 다음 코드를 추가하자

```java
@Bean
Hibernate5Module hibernate5Module() {
  return new Hibernate5Module();
}
```

```gradle
# Build.gradle에 다음을 추가하자
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5'
```





```java
@GetMapping("/api/v1/simple-orders")
public List<Order> ordersV1(){
    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
        order.getMember().getName(); //Lazy 강제 초기화
        order.getDelivery().getAddress(); //Lazy 강제 초기화
    }
    return all;
}
```



> ?????????????????? 이게 뭔지 Lazy 강제 초기화가 뭐지????????





-----

## 간단한 주문 조회 V2: 엔티티를 DTO로 변환

참고: 앞에서 계속 강조했듯이 정말 간단한 애플리케이션이 아니면 엔티티를 API 응답으로 외부로 노출하는 것은 좋지 않다. <br>따라서 Hibernate5Module 를 사용하기 보다는 DTO로 변환해서 반환하는 것이 더 좋은 방법이다.

```java
// Order.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;
```

> Member와 Delivery모두 LAZY로 설정되어있다.



```java
@GetMapping("/api/v2/simple-orders")
public List<SimpleOrderDto> ordersV2() {
  List<Order> orders = orderRepository.findAllByString(new OrderSearch());
  return orders.stream()
    .map(o -> new SimpleOrderDto(o))
    .collect(Collectors.toList());
}

@Data
static class SimpleOrderDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate; //주문시간
  private OrderStatus orderStatus;
  private Address address;

  public SimpleOrderDto(Order order){
    orderId = order.getId();
    name = order.getMember().getName();
    orderDate = order.getOrderDate();
    orderStatus = order.getStatus();
    address = order.getDelivery().getAddress();
  }
}
```



- 엔티티를 DTO로 변환하는 일반적인 방법이다.
- 쿼리가 총 1 + N + N번 실행된다. (v1과 쿼리수 결과는 같다.)
  - order 조회 1번(order 조회 결과 수가 N이 된다.)
  - order -> member 지연 로딩 조회 N 번
  - order -> delivery 지연 로딩 조회 N 번
  - 예) order의 결과가 2개면 최악의 경우 1 + 2 + 2번 실행된다.(최악의 경우)
    - 지연로딩은 **영속성 컨텍스트에서 조회**하므로, 이미 조회된 경우 쿼리를 생략한다.
      - 만약 Order가 2건이고 같은 회원아이디인 경우라면 영속성컨텍스트를 찌르기에 Member를 한번만 찌를 수도있다.
      - 하지만 최악의 경우 2번이다.!

<img width="1358" alt="image-20220522233459345" src="https://user-images.githubusercontent.com/58017318/169703976-268e16a8-c648-439e-906d-8dc060968e7f.png">

<img width="586" alt="image-20220522234215762" src="https://user-images.githubusercontent.com/58017318/169703979-ff5d1604-4932-487f-96e5-3d8c77098525.png">





그럼 LAZY로 하니까 뿌리뻗어나가듯 1 + N + N Query가 발생하니 EAGER로 하면 어떻게 될까?

EAGER로 하면 함께 join해서 가져오니 상관없지않을까?

```java
// Order.java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name="member_id")
private Member member;

@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
@JoinColumn(name="delivery_id")
private Delivery delivery;
```



```java
1.
from
  orders order0_ 
  inner join
  member member1_

2.
  from
  delivery delivery0_ 

3.
  from
  orders order0_ 
  left outer join
  delivery delivery1_ 
  on order0_.delivery_id=delivery1_.delivery_id 
  left outer join
  member member2_ 
  on order0_.member_id=member2_.member_id 

4.
  from
  delivery delivery0_ 

5.
  from
  orders order0_ 
  left outer join
  delivery delivery1_ 
  on order0_.delivery_id=delivery1_.delivery_id 
  left outer join
  member member2_ 
  on order0_.member_id=member2_.member_id 
```

> EAGER로 해도 쿼리역시 많이 나가며 Join만 잔뜩 붙여있다.



-----

## 간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화

```java
 /**
* V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
* - fetch join으로 쿼리 1번 호출
* 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함) */
  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> ordersV3() {
      List<Order> orders = orderRepository.findAllWithMemberDelivery();
      List<SimpleOrderDto> result = orders.stream()
              .map(o -> new SimpleOrderDto(o))
              .collect(toList());
      return result;
}
```



```java
public List<Order> findAllWithMemberDelivery() {
      return em.createQuery(
			"select o from Order o" +
        " join fetch o.member m" +
        " join fetch o.delivery d", Order.class)
			.getResultList();
}
```

> Fetch Join으로 인해 객체의 값을 모두 한꺼번에 가져온다.
>
> 그렇기에 아래처럼 쿼리가 한번만 실행된다
>
> ```log
>     select
>         order0_.order_id as order_id1_6_0_,
>         member1_.member_id as member_i1_4_1_,
>         delivery2_.delivery_id as delivery1_2_2_,
>         order0_.delivery_id as delivery4_6_0_,
>         order0_.member_id as member_i5_6_0_,
>         order0_.order_date as order_da2_6_0_,
>         order0_.status as status3_6_0_,
>         member1_.city as city2_4_1_,
>         member1_.street as street3_4_1_,
>         member1_.zipcode as zipcode4_4_1_,
>         member1_.name as name5_4_1_,
>         delivery2_.city as city2_2_2_,
>         delivery2_.street as street3_2_2_,
>         delivery2_.zipcode as zipcode4_2_2_,
>         delivery2_.status as status5_2_2_ 
>     from
>         orders order0_ 
>     inner join
>         member member1_ 
>             on order0_.member_id=member1_.member_id 
>     inner join
>         delivery delivery2_ 
>             on order0_.delivery_id=delivery2_.delivery_id
> ```



꼭 기본을 LAZY로 하고,

지연로딩으로 설정하였기에 영속성컨텍스트에 있으면 쿼리가 또 안나가므로 <br>Fetch조인을 활용하면 쿼리가 1번나가며 Join을 통해 데이터를 한번에 한꺼번에 값을 가져온다



-----

## 간단한 주문 조회 V4: JPA에서 DTO로 바로 조회



```java
/**
     * V4. JPA에서 DTO로 바로 조회
     *  *-쿼리1번 호출
     * * - select 절에서 원하는 데이터만 선택해서 조회 */
@GetMapping("/api/v4/simple-orders")
public List<OrderSimpleQueryDto> ordersV4() {
  return orderSimpleQueryRepository.findOrderDtos();
}
```

```java
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
        " select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
```

> 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회 <br>**new 명령어**를 사용해서 JPQL의 결과를 DTO로 즉시 변환



```java
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
```





데이터를 날리면 딱 원하는 DTO의 결과만 반영이 된다

```java
    select
        order0_.order_id as col_0_0_,
        member1_.name as col_1_0_,
        order0_.order_date as col_2_0_,
        order0_.status as col_3_0_,
        delivery2_.city as col_4_0_,
        delivery2_.street as col_4_1_,
        delivery2_.zipcode as col_4_2_ 
    from
        orders order0_ 
    inner join
        member member1_ 
            on order0_.member_id=member1_.member_id 
    inner join
        delivery delivery2_ 
            on order0_.delivery_id=delivery2_.delivery_id
```





- 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회 
- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
- SELECT 절에서 원하는 데이터를 직접 선택하므로 DB => 애플리케이션 네트웍 용량 최적화(생각보다 미비)
- 리포지토리 재사용성 떨어짐, **API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점**



엔티티를 DTO로 변환하거나(v3), DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다. <br>둘중 상황에 따라서 더 나은 방법을 선택하면 된다. <br>엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다. 따라서 권장하는 방법은 다음과 같다.



**쿼리 방식 선택 권장 순서**

1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.

2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.

3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.

4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접

   사용한다.









