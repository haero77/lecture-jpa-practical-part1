package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 중간 테이블 'category_item'이 필요하다.
    // 객체 끼리는 다대다 매핑이 가능하지만,
    // DB 관점에서는 불가능하기 때문에 다대다 관계를 일대다, 다대일 관계로 풀어주기 위한 매핑용 테이블이 필요하다.
    // 확장이 불가능하므로 실무에서는 사용하지 않는다.
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    // 계층구조 만들기
    @ManyToOne(fetch = LAZY) // 하나의 부모에 여러 자식
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}
