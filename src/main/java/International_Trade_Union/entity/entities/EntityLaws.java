package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.Type;


import javax.persistence.*;
import java.util.List;


@Data
@Entity(name = "entity_laws")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EntityLaws {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_dto_transaction_id")
    private EntityDtoTransaction entityDtoTransaction;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    String packetLawName;
    @ElementCollection
    @Type(type = "org.hibernate.type.TextType")
    List<String> laws;
    String hashLaw;

    public EntityLaws() {
    }

    public EntityLaws(String packetLawName, List<String> laws, String hashLaw) {
        this.packetLawName = packetLawName;
        this.laws = laws;
        this.hashLaw = hashLaw;
    }
}
