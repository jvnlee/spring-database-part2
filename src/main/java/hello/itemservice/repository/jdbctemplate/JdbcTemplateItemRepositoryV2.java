package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        // 쿼리의 values() 부분에 "?"가 아닌 ":파라미터명"이 들어감
        String sql = "insert into item(item_name, price, quantity) values(:itemName, :price, :quantity)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        /*
        BeanPropertySqlParameterSource
        주어진 객체(자바 빈)의 프로퍼티를 가지고 실제 SQL 파라미터에 들어갈 데이터를 모아둔 것
        이 때 SQL 파라미터들의 이름과 주어진 객체의 프로퍼티들의 이름이 일치해야함
         */
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name = :itemName, price = :price, quantity = :quantity where id = :id";

        /*
        MapSqlParameterSource
        위의 save() 메서드에서 사용한 방식대로 해도 되고, 해당 방법대로 해도 됨
        내부적으로 Map을 사용하고 있으므로 인스턴스 생성 후, addValue()를 필요한만큼 호출해서 쿼리 파라미터의 이름과 값을 넣어주면 됨.
         */
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> param = Map.of("id", id);
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%', :itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        return template.query(sql, param, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        /*
         이전처럼 ResultSet을 파라미터로 콜백을 만들고, 콜백 내부에서 수동으로 Item 객체를 생성해서 setter로 값들을 바인딩시키는 대신,
         BeanPropertyRowMapper에 바인딩 타겟 클래스를 넘겨주면 내부적으로 위 과정을 수행해주기 때문에 코드를 단순화시킬 수 있음.
         */
        return BeanPropertyRowMapper.newInstance(Item.class);
    }

}
