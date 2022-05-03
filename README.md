# jpa_uses_version1
실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발

# 01.__0~2_도메인분석 및 설계
![KakaoTalk_Photo_2022-04-17-16-18-16 001](https://user-images.githubusercontent.com/58017318/166151638-dd73d285-41de-4ce2-9918-c9adaed7f60a.jpeg)
![KakaoTalk_Photo_2022-04-17-16-18-16 002](https://user-images.githubusercontent.com/58017318/166151642-a44e5898-9146-43e9-8e68-453148fc9b8a.jpeg)
![KakaoTalk_Photo_2022-04-17-16-18-16 003](https://user-images.githubusercontent.com/58017318/166151643-1c3e5f02-1802-4d74-91d1-2a9782df58ad.jpeg)



<img width="1723" alt="image-20220501194829060" src="https://user-images.githubusercontent.com/58017318/166151667-26f4cd08-f826-42f1-ab32-a636eae46420.png">
<img width="1725" alt="image-20220501195354579" src="https://user-images.githubusercontent.com/58017318/166151673-06589a89-db5b-4a7e-aaa2-eb62172fdd90.png">
<img width="1716" alt="image-20220501195419317" src="https://user-images.githubusercontent.com/58017318/166151678-b7a5926d-e5ef-4093-9811-7d19e02b7e02.png">
<img width="830" alt="image-20220501200148633" src="https://user-images.githubusercontent.com/58017318/166151682-3e6c3ba1-c8e9-4698-85e0-f4be214d8c45.png">
<img width="1727" alt="image-20220501200205945" src="https://user-images.githubusercontent.com/58017318/166151686-487629c0-9b9b-4dd4-ab05-6d5927c3e1b4.png">
<img width="1034" alt="image-20220501200245046" src="https://user-images.githubusercontent.com/58017318/166151690-799d5f78-1135-4b2d-9bf2-6814b200839e.png">
<img width="1717" alt="image-20220501200358657" src="https://user-images.githubusercontent.com/58017318/166151691-f7d23a21-f6fa-4fc6-86f0-8053b9ffd0ed.png">
<img width="1693" alt="image-20220501200442576" src="https://user-images.githubusercontent.com/58017318/166151693-2058ecd4-9c91-4431-81b5-7df299402a1e.png">
<img width="1659" alt="image-20220501200506068" src="https://user-images.githubusercontent.com/58017318/166151695-861d0fd7-2ff3-4eb6-baf6-8e8c237044bd.png">


# 03 7.웹계층개발
Shift + Command + F9<br> thymeleaf 리컴파일 됨

```gradle
dependencies {
	// 타임리프 리컴파일 도와줌
	implementation 'org.springframework.boot:spring-boot-devtools'
}
```

```java
@Slf4j
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home";
    }
}
```

```html
<!-- include -->
<head th:replace="fragments/header :: header">
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
```



-----

<img width="1679" alt="image-20220501224707165" src="https://user-images.githubusercontent.com/58017318/166151825-28e72d94-d72d-41cc-886b-1054cd18d09e.png">


```
@Valid : MemberForm안에 선언한 어노테이션 Validation을 체크한다.
MemberForm 뒤에 BindingResult를 선언하면,
   form안에 오류를 흡수하여 Controller안에 코드가 실행된다.
   타임리프 안에서는 #fields로 BindingResult안에 접근 가능하다.
       ex) ${#fields.hasErrors('name')}
```

-------

##### 회원목록 중 th for문 사용법

```html
<tr th:each="member : ${members}">
  <td th:text="${member.id}"></td>
  <td th:text="${member.name}"></td>
  <td th:text="${member.address?.city}"></td>
  <td th:text="${member.address?.street}"></td>
  <td th:text="${member.address?.zipcode}"></td>
</tr>
```

-----

**상품등록 중 링크 action 문법**

```html
<form th:action="@{/items/new}" th:object="${form}" method="post">
```

**상품 목록 중 for문 안에 있는 href**

```html
<a href="#" 
   th:href="@{/items/{id}/edit (id=${item.id})}" 
   class="btn btn-primary" role="button">수정</a>
```

URL 링크

- \<a th:href="@{/hello}">basic url</a>
- \<a th:href="@{/hello(param1=${param1}, param2=${param2})}">
- \<a th:href="@{/hello/{param1}/{param2}(param3=${param3})}">

---------




**플러시?**

**영속성 컨텍스트**의 **변경**내용을 **데이터베이스에 반영**

**변경감지**
<img width="1016" alt="image" src="https://user-images.githubusercontent.com/58017318/166397240-09806ea2-8606-49fa-a1f2-fbe02c2af01b.png">

트랜잭션을 커밋하면 엔티티 매니저 내부에서 먼저 flush(플러시)가 호출된다.
엔티티와 스냅샷을 비교해서 변경된 엔티티를 찾는다.
변경된 엔티티가 있으면 수정 쿼리를 생성해서 쓰기지연 SQL저장소에 보낸다.
쓰기지연 저장소의 SQL을 데이터베이스에 보낸다.
데이터베이스 트랜잭션을 커밋한다.

----

## 변경 감지와 병합(merge)

**준영속 엔티티?**

영속성 컨텍스트가 더는 관리되지 않는 엔티티를 말한다.<br>(여기서는 itemService.saveItem(book)에서 수정을 시도하는 Book객체다.<br>Book객체는 **이미 DB에 한번 저장**되어서 **식별자가 존재**한다.<br>이렇게 **임의로 만들어낸 엔티티**도 **기존 식별자**를 가지고 있으면<br>준영속 엔티티로 볼 수 있다.)

**준영속 엔티티를 수정하는 2가지 방법**

1. 변경 감지 기능 사용

   > **영속성 컨텍스트**에서 **엔티티를 다시 조회**한 후에 **데이터를 수정**하는 방법
   >
   > > 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택<br>--> 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)이 동작해서<br>--> DB에 UPDATE SQL 실행	

   ```java
   /**
   1. itemRepository.findOne는 em.find로 가져온것이기때문에 영속성컨텍스트에 영속되어있다.
   2. set으로 변경하였다면
   3. @Transactional에 의하여 commit 이 될 것이고
   4. flush가 발생하여
   		영속성컨텍스트에 1차캐시안에 엔티티와 스냅샷을 비교하여
       변경된 점을 감지하여 [쓰기 지연 SQL 저장소]에 쿼리를 저장한 후,
       SQL문을 DB에 보낸다.
       이대로 변경됨!
   */
   @PostMapping(value = "/items/{itemId}/edit")
   public String updateItem(@ModelAttribute("form") BookForm form) {
     itemService.updateItem(form.getId(), form.getName(), form.getPrice());
     return "redirect:/items";
   }
   
   @Transactional
   public void updateItem(Long id, String name, int price) {
     Item item = itemRepository.findOne(id);
     item.setName(name);
     item.setPrice(price);
   }
   ```

   > 

2. 병합(merge) 사용

```java
public void save(Item item){
    // 이미 DB에 등록된 것을 가지고 온것
    // 이 한줄만 쓰면 find해서 item과 객체를 비교하여 변경된 값을 저장해줌
    // [변경 감지 기능 사용]에 써놓은 내용을 한다고 보면됨
    // 또한 해당 객체를 반환한다 (Item)
    Item item = em.merge(item);
}
```

<img width="765" alt="image" src="https://user-images.githubusercontent.com/58017318/166397297-26f92c57-586b-4c5f-b796-e34472b9fc21.png">

**병합 동작 방식**

1. merge()를실행한다.

2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.

   2-1. 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다.

3. 조회한 영속 엔티티( mergeMember )에 member 엔티티의 값을 채워 넣는다. (member 엔티티의 모든 값

   을 mergeMember에 밀어 넣는다. 이때 mergeMember의 “회원1”이라는 이름이 “회원명변경”으로 바

   뀐다.)

4. 영속 상태인 mergeMember를 반환한다.

> 주의: **1.변경 감지** 기능을 사용하면 **원하는 속성만 선택**해서 변경할 수 있지만, <br>**2. 병합을 사용**하면 **모든 속성이 변경**된다. <br>**병합시 값이 없으면 null 로 업데이트 할 위험**도 있다. (병합은 모든 필드를 교체한다.)
>
> 즉, **1.변경 감지 기능 사용**를 사용한다.

-------

### 주문 화면

```html
<form role="form" action="/order" method="post">
  <div class="form-group">
    <label for="member">주문회원</label>
    <select name="memberId" id="member" class="form-control">
      <option value="">회원선택</option>
      <option th:each="member : ${members}"
              th:value="${member.id}"
              th:text="${member.name}"/>
    </select>
  </div>
  <div class="form-group">
    <label for="item">상품명</label>
    <select name="itemId" id="item" class="form-control">
      <option value="">상품선택</option>
      <option th:each="item : ${items}"
              th:value="${item.id}"
              th:text="${item.name}"/>
    </select>
  </div>
  <div class="form-group">
    <label for="count">주문수량</label>
    <input type="number" name="count" class="form-control" id="count" placeholder="주문 수량을 입력하세요"></div>
  <button type="submit" class="btn btn-primary">Submit</button>
</form>
```

```html
for문으로 option을 넣을 수 있다.
<select name="memberId" id="member" class="form-control">
  <option value="">회원선택</option>
  <option th:each="member : ${members}"
          th:value="${member.id}"
          th:text="${member.name}"/>
</select>
```

## @ModelAttribute

```java
@GetMapping("/orders")

/**
파라미터에 @ModelAttribute("orderSearch") OrderSearch orderSearch
위 내용을 입력하면
	model.addAttribute("orderSearch", orderSearch); 자동으로 이 내용이 포함된다
**/
public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
  List<Order> orders = orderService.findOrders(orderSearch);
  model.addAttribute("orders", orders);

  return "order/orderList";
}
```

>파라미터에 @ModelAttribute("orderSearch") OrderSearch orderSearch
>위 내용을 입력하면
>model.addAttribute("orderSearch", orderSearch); 자동으로 이 내용이 포함된다
>
> 그렇기에 아래 html에서 다음과 같이 사용 가능하다
>
>```java
><form th:object="${orderSearch}" class="form-inline">
>```



## Enum 값 가져오기

```java
public enum OrderStatus {
    ORDER, CANCLE
}
```



OrderStatus Enum으로 선언된 값을 모두 가져오고싶다면 아래와 같이 사용 가능하다.

>```java
><select th:field="*{orderStatus}" class="form-control">
> 	<option value="">주문상태</option>
>  <option th:each= "status : ${T(jpabook.jpashop.domain.OrderStatus).values()}"
>              th:value="${status}"
>              th:text="${status}">
>  </option>
></select>
>```


