package International_Trade_Union.entity.entities;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EntityBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public EntityBlock() {
    }

    public EntityBlock(List<EntityDtoTransaction> dtoTransactions, String previousHash, String minerAddress, String founderAddress, long randomNumberProof, double minerRewards, int hashCompexity, Timestamp timestamp, long index, String hashBlock) {
        this.dtoTransactions = dtoTransactions;
        this.previousHash = previousHash;
        this.minerAddress = minerAddress;
        this.founderAddress = founderAddress;
        this.randomNumberProof = randomNumberProof;
        this.minerRewards = minerRewards;
        this.hashCompexity = hashCompexity;
        this.timestamp = timestamp;
        this.index = index;
        this.hashBlock = hashBlock;
    }
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_block_id")
    private List<EntityDtoTransaction> dtoTransactions;
    private String previousHash;
    private String minerAddress;
    private String founderAddress;
    private long randomNumberProof;
    private double minerRewards;
    private int hashCompexity;
    private Timestamp timestamp;
    private long index;
    private String hashBlock;
}
