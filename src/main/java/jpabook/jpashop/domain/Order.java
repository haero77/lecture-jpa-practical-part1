package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // FK 'member_id'
    private Member member;

    @OneToMany(mappedBy = "order") // OrderItem.order
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    * 보통 Delivery보다 Order를 우선적으로 사용하게 되므로, Order 쪽에 FK를 두었다.
    * 따라서 Order가 연관관계 주인.
    * @JoinColumn: 이 필드가 주인이다.
     */
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
}