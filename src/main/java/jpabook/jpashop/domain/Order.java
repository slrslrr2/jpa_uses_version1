package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name="orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderitems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem){
        this.setOrderitems(orderitems);
        orderItem.setOrder(this);
    }

    /**
         @Entity
         @Getter @Setter
         @Table(name="orders")
         @NoArgsConstructor(access = AccessLevel.PROTECTED)
         public class Order {
     */

    //== 생성 메서드 ==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비즈니스 로직==//
    /** 주문 취소 **/
    public void cancle(){
        // 배송 완료된 경우
        if(delivery.getStatus() == DeliveryStaus.COMP){
            throw new IllegalArgumentException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCLE);
        for (OrderItem orderitem : orderitems) {
            orderitem.cancle();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 **/
    public int getTotalPrice(){
        int totalPrice = orderitems.stream().mapToInt(OrderItem::getTotalPrice).sum();
        return totalPrice;
        /**
        int totalPrice = 0;
        for (OrderItem orderitem : orderitems) {
            totalPrice += orderitem.getTotalPrice();
        }

         1단계: Stream으로 1단계 변신
         int totalPrice = orderitems.stream().mapToInt((OrderItem)-> OrderItem.getTotalPrice()).sum();

         2단계: 메소드참조표현식(:: – 이중 콜론 연산자) 사용
            메소드참조표현식이란? 람다식에서 파라미터를 중복해서 쓰기 싫은경우 사용

             변경 전)
             .mapToInt(  (OrderItem)-> OrderItem.getTotalPrice()     )

             변경 후)
             .mapToInt(  OrderItem::getTotalPrice                    )
         위 내용을 메소드참조표현식
         **/
    }
}























