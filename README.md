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

