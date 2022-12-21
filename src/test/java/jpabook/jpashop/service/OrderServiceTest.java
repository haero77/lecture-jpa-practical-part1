package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문")
    void order() {
        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).as("상품 주문 시 상태는 ORDER")
                .isEqualTo(getOrder.getStatus());
        assertThat(1).as("주문한 상품 종류 수가 정확해야한다.")
                .isEqualTo(getOrder.getOrderItems().size());
        assertThat(10000 * orderCount).as("주문 가격은 '상품 가격 * 수량' 이다.")
                .isEqualTo(getOrder.getTotalPrice());
        assertThat(8).as("주문 수량만큼 재고가 줄어야한다.")
                .isEqualTo(book.getStockQuantity());
    }

    /**
     * Item 엔티티의 removeStock 자체에 대해 단위테스트를 작성하는 것이 좋다.
     */
    @Test
    @DisplayName("상품 주문 재고 수량 초과시 예외가 발생한다.")
    void should_throw_exception_when_orderCount_exceed_stockQuantity() {
        // given
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        // when & then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });
    }

    @Test
    @DisplayName("주문 취소")
    void cancel_order() {
        // given
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), 2);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.CANCEL).as("주문 취소시 상태는 CANCEL 이다")
                .isEqualTo(getOrder.getStatus());
        assertThat(10).as("주문이 취소된 상품은 그만큼 재고가 증가해야한다.")
                .isEqualTo(book.getStockQuantity());
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원 1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}