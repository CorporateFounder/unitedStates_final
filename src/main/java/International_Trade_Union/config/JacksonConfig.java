package International_Trade_Union.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new ConditionalBigDecimalSerializer());
        module.addDeserializer(BigDecimal.class, new ConditionalBigDecimalDeserializer());
        module.addSerializer(Double.class, new ConditionalDoubleSerializer());
        module.addDeserializer(Double.class, new ConditionalDoubleDeserializer());
        module.addSerializer(double.class, new ConditionalDoubleSerializer());
        module.addDeserializer(double.class, new ConditionalDoubleDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
