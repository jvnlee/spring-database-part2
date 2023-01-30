package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.jpa.SpringDataJpaItemRepository;
import hello.itemservice.repository.querydsl.QueryDslItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaAndQueryDslConfig {

    private final EntityManager em;

    /*
    Spring Boot의 기본 설정인 @EnableJpaRepositories 어노테이션에 따라 JpaRepository를 상속받는 인터페이스는 자동적으로 빈으로 등록됨
    따라서 별도로 등록하지 않더라도 원래 있는 것처럼 사용할 수 있음
     */
    private final SpringDataJpaItemRepository springDataJpaItemRepository;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV2(springDataJpaItemRepository, queryDslItemRepository());
    }

    @Bean
    public QueryDslItemRepository queryDslItemRepository() {
        return new QueryDslItemRepository(em);
    }

    /*
    원래는 더 이상 필요한 빈이 아니지만, TestDataInit 클래스에서 참조하고 있으므로 그냥 둠.
     */
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }

}
