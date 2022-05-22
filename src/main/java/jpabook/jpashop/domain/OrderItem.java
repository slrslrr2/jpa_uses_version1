package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/** protected OrderItem(){}
 * OrderItem orderItem2 = new OrderItem(); // 요거 막기 위해 Protected 생성자
 */
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;
    private int orderPrice; //주문 가격
    private int count; // 주문 수량

    //==생성 메서드==//
    // 이거 static으로 왜 한거지?!!?!?
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        /**
         * 도메인 모델 패턴이란?
         * [엔티티가 비즈니스 로직을 가지고] 객체지향 특성을 적극 활용하는것
         */
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //재고 마이너스
        return orderItem;
    }

    //== 비즈니스 로직==//
    public void cancle(){
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getCount() * getOrderPrice();
    }
}
