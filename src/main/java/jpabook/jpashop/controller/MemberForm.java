package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    @NotEmpty(message = "도시 이름은 필수 입니다.")
    @Length(min = 3, max=10)
    private String city;

    private String street;
    private String zipcode;
}
