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

    /**
     * (CascadeType.ALL 적용 전)
     * persist(orderItemA)
     * persist(orderItemB)
     * persist(orderItemC)
     * persist(order)
     *
     * (CascadeType.ALL 적용 후)
     * persist(order)
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // OrderItem.order
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    * 보통 Delivery보다 Order를 우선적으로 사용하게 되므로, Order 쪽에 FK를 두었다.
    * 따라서 Order가 연관관계 주인.
    * @JoinColumn: 이 필드가 주인이다.
     */
    /**
     * (CascadeType.ALL 적용 전)
     * persist(delivery)
     * persist(order)
     *
     * (CascadeType.ALL 적용 후)
     * persist(order)
     */
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    //==연관관계 편의 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}