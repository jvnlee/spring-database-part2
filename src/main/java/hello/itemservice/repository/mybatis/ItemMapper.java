package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;


/*
해당 인터페이스의 메서드를 호출하면 XML에 있는 쿼리가 실행됨
 */
@Mapper
public interface ItemMapper {

    void save(Item item);

    // 파라미터 개수가 2개 이상일 때는 @Param을 붙여줘야 함
    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond cond);

}
