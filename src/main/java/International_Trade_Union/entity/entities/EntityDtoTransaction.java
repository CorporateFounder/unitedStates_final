package International_Trade_Union.entity.entities;


import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "entity_dto_transaction")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EntityDtoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public EntityDtoTransaction() {
    }


    public EntityDtoTransaction(String sender, String customer, double digitalDollar, double digitalStockBalance, EntityLaws entityLaws, double bonusForMiner, VoteEnum voteEnum, byte[] sign) {
        this.sender = sender;
        this.customer = customer;
        this.digitalDollar = digitalDollar;
        this.digitalStockBalance = digitalStockBalance;
        this.entityLaws = entityLaws;
        this.bonusForMiner = bonusForMiner;
        this.voteEnum = voteEnum;
        this.sign = sign;
    }

    private String sender;
    private String customer;
    private double digitalDollar;
    private double digitalStockBalance;

    @OneToOne(mappedBy = "entityDtoTransaction", cascade = CascadeType.ALL)
    private EntityLaws entityLaws;

    private double bonusForMiner;
    private VoteEnum voteEnum;
    private byte[] sign;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_block_id")
    private EntityBlock entityBlock;
    // Остальные поля класса

    // Геттеры и сеттеры для поля entityBlock
    public EntityBlock getEntityBlock() {
        return entityBlock;
    }

    public void setEntityBlock(EntityBlock entityBlock) {
        this.entityBlock = entityBlock;
    }
}
