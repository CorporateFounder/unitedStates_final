package International_Trade_Union.entity.entities;


import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;


@Getter
@Setter
@Entity
@Table(name = "entity_dto_transaction")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDtoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


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

    @OneToOne(mappedBy = "entityDtoTransaction", cascade = CascadeType.ALL,  orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private EntityLaws entityLaws;

    private double bonusForMiner;
    private VoteEnum voteEnum;
    private byte[] sign;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_block_id")
    @JsonIgnore
    private EntityBlock entityBlock;
    // Остальные поля класса

    // Геттеры и сеттеры для поля entityBlock
    public EntityBlock getEntityBlock() {
        return entityBlock;
    }

    public void setEntityBlock(EntityBlock entityBlock) {
        this.entityBlock = entityBlock;
    }

    @Override
    public String toString() {
        return "EntityDtoTransaction{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", customer='" + customer + '\'' +
                ", digitalDollar=" + digitalDollar +
                ", digitalStockBalance=" + digitalStockBalance +
                ", entityLaws=" + entityLaws +
                ", bonusForMiner=" + bonusForMiner +
                ", voteEnum=" + voteEnum +
                ", sign=" + Arrays.toString(sign) +
                '}';
    }
}
