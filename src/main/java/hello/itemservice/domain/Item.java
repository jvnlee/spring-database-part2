package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

/*
@Entity: JPA가 사용하는 객체임을 명시
 */
@Data
@Entity
public class Item {

    /*
    @Id: 해당 프로퍼티가 PK임을 명시
    @GeneratedValue: auto-increment처럼 DB에서 값을 생성하고 관리하는 PK 전략을 지정
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @Column: 해당 프로퍼티의 이름이 DB 테이블에서의 컬럼명과 다른 경우에 매핑을 위해 사용 (이름이 같다면 생략 가능)
    - name: 컬럼의 이름 명시. (원래 camel case를 snake case로 자동 변환해주기 때문에 현재의 경우에도 생략 가능함)
    - length: 컬럼의 길이 명시. 단, 문자열 타입의 컬럼에만 적용 가능 (ex. varchar 10)
     */
    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    /*
    JPA 사용 시, public 또는 protected 기본 생성자 필수
     */
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
