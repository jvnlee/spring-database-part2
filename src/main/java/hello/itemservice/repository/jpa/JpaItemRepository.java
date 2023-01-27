package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
JPA의 모든 데이터 변경은 트랜잭션 아래에서 이루어지기 때문에 @Transactional 어노테이션 필요
(등록, 수정, 삭제에 해당. 조회는 트랜잭션 없이도 가능하기 때문에 제외)
원래는 서비스 계층에서 트랜잭션을 시작하고 종료하는게 맞지만, 특별한 비즈니스 로직이 없는 예제라서 이례적으로 리포지토리에 트랜잭션을 설정함.
 */
@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {

    /*
    JPA의 모든 동작은 EntityManager를 통해서 이루어짐
    내부에 DataSource를 가지고 있고, DB에 접근할 수 있음
     */
    private final EntityManager em;

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item item = em.find(Item.class, itemId);
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());

        /*
        em.update() 같은 업데이트 명령이 필요해 보이지만,
        따로 명시하지 않아도 setter로 변경한 데이터들을 JPA가 내부적으로 인식할 수 있기 때문에 트랜잭션 커밋 시점에 UPDATE 쿼리에 담아 DB에 반영해줌.
        마치 자바 컬렉션에 담긴 객체를 꺼내와서 프로퍼티를 변경하면 참조에 의해 실제 컬렉션 내부의 객체 프로퍼티가 변경되는 것과 같은 이치.
         */
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        /*
        여러 데이터를 복합적인 조건으로 조회할 때, JPQL 이라는 객체 지향 쿼리 언어를 사용함
        JPQL 문법은 DB 테이블을 대상으로 하는 것이 아닌, 객체를 대상으로 함
        따라서 Item은 설정해둔 Item Entity를 가리키는 것임
         */
        String jpql = "select i from Item i";

        // 여기서부터 동적 쿼리 작성 파트
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }

}
