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

public class ConditionalDoubleDeserializer extends JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        double value = Double.parseDouble(p.getText());
        int currentSize = BasisController.getBlockchainSize();
        System.out.println("Current blockchain size: " + currentSize);
        if (currentSize > Seting.JSON_VERSION_DECIMAL) {
            value = BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).doubleValue();
        }
        return value;
    }
}
