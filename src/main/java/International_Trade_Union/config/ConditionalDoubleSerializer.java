package International_Trade_Union.config;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.setings.Seting;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConditionalDoubleSerializer extends JsonSerializer<Double> {
    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        int currentSize = BasisController.getBlockchainSize();
        System.out.println("Current blockchain size: " + currentSize);
        if (currentSize > Seting.JSON_VERSION_DECIMAL) {
            value = BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).doubleValue();
        }
        gen.writeString(value.toString());
    }
}
