package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public EntityBlock() {
    }

    public EntityBlock( List<EntityDtoTransaction> dtoTransactions,
                        String previousHash,
                        String minerAddress,
                        String founderAddress,
                        long randomNumberProof,
                        double minerRewards,
                        long hashCompexity,
                        long timestamp,
                        long index,
                        String hashBlock,
                        long specialIndex) {
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
        this.specialIndex = specialIndex;
    }
    @OneToMany(mappedBy = "entityBlock", cascade = CascadeType.ALL)
    private List<EntityDtoTransaction> dtoTransactions;
    private String previousHash;
    private String minerAddress;
    private String founderAddress;
    private long randomNumberProof;
    private double minerRewards;
    private long hashCompexity;


    private long timestamp;
    private long index;
    private String hashBlock;

    //специальный индекс блока который позволит отделить блоки
    private long specialIndex;

    @Override
    public String toString() {
        return "EntityBlock{" +
                "id=" + id +
                ", dtoTransactions=" + dtoTransactions +
                ", previousHash='" + previousHash + '\'' +
                ", minerAddress='" + minerAddress + '\'' +
                ", founderAddress='" + founderAddress + '\'' +
                ", randomNumberProof=" + randomNumberProof +
                ", minerRewards=" + minerRewards +
                ", hashCompexity=" + hashCompexity +
                ", timestamp=" + timestamp +
                ", index=" + index +
                ", hashBlock='" + hashBlock + '\'' +
                '}';
    }
}
