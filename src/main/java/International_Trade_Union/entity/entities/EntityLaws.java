package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity(name = "entity_laws")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityLaws {



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_dto_transaction_id")
    @JsonIgnore
    private EntityDtoTransaction entityDtoTransaction;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    boolean lawsIsNull;

    String packetLawName;


    @ElementCollection(fetch = FetchType.EAGER)
    @Type(type = "org.hibernate.type.TextType")
    List<String> laws;
    //
//    @Lob
//    String laws;
    String hashLaw;

    public EntityLaws() {
    }

    public EntityLaws( boolean lawsIsNull, String packetLawName, List<String> laws, String hashLaw) {
        this.lawsIsNull = lawsIsNull;
        this.packetLawName = packetLawName;
        this.laws = laws;
        this.hashLaw = hashLaw;
    }

    @Override
    public String toString() {
        return "EntityLaws{" +
                ", id=" + id +
                ", packetLawName='" + packetLawName + '\'' +
                ", laws=" + laws +
                ", isNull=" + lawsIsNull +
                ", hashLaw='" + hashLaw + '\'' +
                '}';
    }
}
