package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        // Member 의 경우 한 번 save 를 하면 이후 변경되지 않지만,
        // Item 의 경우 item을 생성하여 id값을 부여받고 나서 수정이 이뤄진다.
        if (item.getId() == null) { // Item 객체를 새로 생성하는 경우
            em.persist(item);
        } else { // 업데이트의 경우
            em.merge(item); // 강제 업데이트
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
