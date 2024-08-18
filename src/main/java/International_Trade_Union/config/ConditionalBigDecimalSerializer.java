package International_Trade_Union.config;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.setings.Seting;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConditionalBigDecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (BasisController.getBlockchainSize() > Seting.JSON_VERSION_DECIMAL) {
            value = value.setScale(10, RoundingMode.HALF_UP);
        }
        gen.writeString(value.toPlainString());
    }
}
