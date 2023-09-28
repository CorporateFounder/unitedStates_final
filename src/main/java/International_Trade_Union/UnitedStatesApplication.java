package International_Trade_Union;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;



@SpringBootApplication
@EntityScan("International_Trade_Union.entity.entities")
public class UnitedStatesApplication {
	public static void main(String[] args) {
		SpringApplication.run(UnitedStatesApplication.class, args);
	}

}
