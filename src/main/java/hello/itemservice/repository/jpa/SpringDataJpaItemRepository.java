package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNameLike(String itemName);

    List<Item> findByPriceLessThanEqual(Integer price);

    /*
    1. 쿼리 메서드
    메서드명을 보고 Spring Data JPA가 자동적으로 쿼리를 생성하여 수행
     */
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    /*
    2. 쿼리 직접 실행
    쿼리 파라미터가 많고 복잡한 경우는 메서드 이름으로 담기 어렵기 떼문에 @Query 어노테이션을 붙이고 직접 JPQL을 작성할 수 있음
     */
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
