package International_Trade_Union.utils;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.InfoDificultyBlockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.vote.CurrentLawVotes;
import International_Trade_Union.vote.CurrentLawVotesEndBalance;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class UtilsJsonNew {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addSerializer(Double.class, new DoubleSerializer());
        module.addSerializer(double.class, new DoubleSerializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        module.addDeserializer(Double.class, new DoubleDeserializer());
        module.addDeserializer(double.class, new DoubleDeserializer());
        mapper.registerModule(module);
    }

    private static class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(formatBigDecimal(value).toPlainString());
        }
    }

    private static class DoubleSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).toPlainString());
        }
    }

    private static class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return formatBigDecimal(new BigDecimal(p.getText()));
        }
    }

    private static class DoubleDeserializer extends JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return formatDouble(Double.parseDouble(p.getText()));
        }
    }

    public static BigDecimal formatBigDecimal(BigDecimal value) {
        return value.setScale(10, RoundingMode.HALF_UP);
    }

    public static double formatDouble(double value) {
        return BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).doubleValue();
    }

    public static String objToStringJson(Object object) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, object);
        return writer.toString();
    }

    public static Object jsonToListBLocks(String json, Class cls) throws JsonProcessingException {

        return mapper.readValue(json, cls);
    }

    public static List<Block> jsonToListBLocks(String json) throws JsonProcessingException {

        return mapper.readValue(json, new TypeReference<List<Block>>(){});
    }

    public static List<Account> jsonToListAccounts(String json) throws JsonProcessingException {
        List<Account> accounts = mapper.readValue(json, new TypeReference<List<Account>>(){});
        for (Account account : accounts) {
            account.setDigitalDollarBalance(formatBigDecimal(account.getDigitalDollarBalance()));
            account.setDigitalStockBalance(formatBigDecimal(account.getDigitalStockBalance()));
            account.setDigitalStakingBalance(formatBigDecimal(account.getDigitalStakingBalance()));
        }
        return accounts;
    }

    public static Set<String> jsonToSetAddresses(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<Set<String>>(){});
    }

    public static Block jsonToBLock(String json) throws JsonProcessingException {
        return mapper.readValue(json, Block.class);
    }

    public static InfoDificultyBlockchain jsonToInfoDifficulty(String json) throws JsonProcessingException {
        return mapper.readValue(json, InfoDificultyBlockchain.class);
    }

    public static Laws jsonToLaw(String json) throws JsonProcessingException {
        return mapper.readValue(json, Laws.class);
    }

    public static InfoDemerageMoney jsonInfoDemerageMoney(String json) throws JsonProcessingException {
        return mapper.readValue(json, InfoDemerageMoney.class);
    }

    public static CurrentLawVotes jsonToVote(String json) throws JsonProcessingException {
        return mapper.readValue(json, CurrentLawVotes.class);
    }

    public static LawEligibleForParliamentaryApproval jsonToCurrentLaw(String json) throws JsonProcessingException {
        return mapper.readValue(json, LawEligibleForParliamentaryApproval.class);
    }

    public static Account jsonToAccount(String json) throws JsonProcessingException {
        Account account = mapper.readValue(json, Account.class);
        account.setDigitalDollarBalance(formatBigDecimal(account.getDigitalDollarBalance()));
        account.setDigitalStockBalance(formatBigDecimal(account.getDigitalStockBalance()));
        account.setDigitalStakingBalance(formatBigDecimal(account.getDigitalStakingBalance()));
        return account;
    }

    public static CurrentLawVotesEndBalance jsonToCurrentLawVotesBalance(String json) throws JsonProcessingException {
        return mapper.readValue(json, CurrentLawVotesEndBalance.class);
    }

    public static EntityChain jsonToEntityChain(String json) throws JsonProcessingException {
        return mapper.readValue(json, EntityChain.class);
    }

    public static DtoTransaction jsonToDtoTransaction(String json) throws JsonProcessingException {
        return mapper.readValue(json, DtoTransaction.class);
    }

    public static DataShortBlockchainInformation jsonToDataShortBlockchainInformation(String json) throws JsonProcessingException {
        return mapper.readValue(json, DataShortBlockchainInformation.class);
    }

    public static List<DtoTransaction> jsonToDtoTransactionList(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<DtoTransaction>>(){});
    }

    public static Map<String, Account> balances(String json) throws JsonProcessingException {
        Map<String, Account> balances = mapper.readValue(json, new TypeReference<Map<String, Account>>(){});
        for (Account account : balances.values()) {
            account.setDigitalDollarBalance(formatBigDecimal(account.getDigitalDollarBalance()));
            account.setDigitalStockBalance(formatBigDecimal(account.getDigitalStockBalance()));
            account.setDigitalStakingBalance(formatBigDecimal(account.getDigitalStakingBalance()));
        }
        return balances;
    }

    public static void saveWindowsToFile(Map<Long, Map<String, Account>> windows, String filePath) throws IOException {
        Map<Long, Map<String, Account>> formattedWindows = new LinkedHashMap<>();
        for (Map.Entry<Long, Map<String, Account>> entry : windows.entrySet()) {
            Map<String, Account> formattedAccounts = new HashMap<>();
            for (Map.Entry<String, Account> accountEntry : entry.getValue().entrySet()) {
                Account formattedAccount = accountEntry.getValue();
                formattedAccount.setDigitalDollarBalance(formatBigDecimal(formattedAccount.getDigitalDollarBalance()));
                formattedAccount.setDigitalStockBalance(formatBigDecimal(formattedAccount.getDigitalStockBalance()));
                formattedAccount.setDigitalStakingBalance(formatBigDecimal(formattedAccount.getDigitalStakingBalance()));
                formattedAccounts.put(accountEntry.getKey(), formattedAccount);
            }
            formattedWindows.put(entry.getKey(), formattedAccounts);
        }
        mapper.writeValue(new File(filePath), formattedWindows);
    }

    public static Map<Long, Map<String, Account>> loadWindowsFromFile(String filePath) {
        try {
            LinkedHashMap<Long, Map<String, Account>> windows = mapper.readValue(
                    new File(filePath),
                    new TypeReference<LinkedHashMap<Long, Map<String, Account>>>() {}
            );

            // Format the loaded values
            for (Map<String, Account> accountMap : windows.values()) {
                for (Account account : accountMap.values()) {
                    account.setDigitalDollarBalance(formatBigDecimal(account.getDigitalDollarBalance()));
                    account.setDigitalStockBalance(formatBigDecimal(account.getDigitalStockBalance()));
                    account.setDigitalStakingBalance(formatBigDecimal(account.getDigitalStakingBalance()));
                }
            }

            return reapplySlidingWindowBehavior(windows, Seting.SLIDING_WINDOW_BALANCE);
        } catch (IOException e) {
            return reapplySlidingWindowBehavior(new LinkedHashMap<>(), Seting.SLIDING_WINDOW_BALANCE);
        }
    }

    private static LinkedHashMap<Long, Map<String, Account>> reapplySlidingWindowBehavior(
            LinkedHashMap<Long, Map<String, Account>> originalMap, int windowSize) {
        LinkedHashMap<Long, Map<String, Account>> windows = new LinkedHashMap<Long, Map<String, Account>>(windowSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Map<String, Account>> eldest) {
                return size() > windowSize;
            }
        };

        windows.putAll(originalMap);
        return windows;
    }
}