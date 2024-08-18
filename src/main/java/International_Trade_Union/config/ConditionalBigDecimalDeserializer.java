package International_Trade_Union.config;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.setings.Seting;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConditionalBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        BigDecimal value = new BigDecimal(p.getText());
        int currentSize = BasisController.getBlockchainSize();
        System.out.println("Current blockchain size: " + currentSize);
        if (currentSize > Seting.JSON_VERSION_DECIMAL) {
            value = value.setScale(10, RoundingMode.HALF_UP);
        }
        return value;
    }
}
