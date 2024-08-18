package International_Trade_Union.utils;

import International_Trade_Union.config.ConditionalBigDecimalDeserializer;
import International_Trade_Union.config.ConditionalBigDecimalSerializer;
import International_Trade_Union.config.ConditionalDoubleDeserializer;
import International_Trade_Union.config.ConditionalDoubleSerializer;
import International_Trade_Union.controllers.BasisController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UtilsJson {
    private static final ObjectMapper oldMapper = new ObjectMapper();
    private static final ObjectMapper newMapper;

    static {
        // Настроить новый ObjectMapper
        newMapper = new ObjectMapper();

        // Включить настройку для записи BigDecimal как plain
        newMapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

        // Создать новый модуль для кастомных сериализаторов и десериализаторов
        SimpleModule module = new SimpleModule();

        // Добавить сериализатор и десериализатор для BigDecimal
        module.addSerializer(BigDecimal.class, new ConditionalBigDecimalSerializer());
        module.addDeserializer(BigDecimal.class, new ConditionalBigDecimalDeserializer());

        // Добавить сериализатор и десериализатор для Double
        module.addSerializer(Double.class, new ConditionalDoubleSerializer());
        module.addDeserializer(Double.class, new ConditionalDoubleDeserializer());

        // Добавить сериализатор и десериализатор для double (примитивного типа)
        module.addSerializer(double.class, new ConditionalDoubleSerializer());
        module.addDeserializer(double.class, new ConditionalDoubleDeserializer());

        // Зарегистрировать модуль в ObjectMapper
        newMapper.registerModule(module);
    }
    private static ObjectMapper getMapper() {
        return BasisController.getBlockchainSize() > Seting.JSON_VERSION_DECIMAL ? newMapper : oldMapper;
    }

    public static String objToStringJson(Object object) throws IOException {
        StringWriter writer = new StringWriter();
        getMapper().writeValue(writer, object);
        return writer.toString();
    }

    public static Object jsonToObject(String json, Class cls) throws JsonProcessingException {
        return getMapper().readValue(json, cls);
    }

    public static List<Block> jsonToObject(String json) throws JsonProcessingException {
        return getMapper().readValue(json, new TypeReference<List<Block>>(){});
    }

    public static List<Account> jsonToListAccounts(String json) throws JsonProcessingException {
        return getMapper().readValue(json, new TypeReference<List<Account>>(){});
    }

    public static Set<String> jsonToSetAddresses(String json) throws JsonProcessingException {
        return getMapper().readValue(json, new TypeReference<Set<String>>(){});
    }

    public static Block jsonToBLock(String json) throws JsonProcessingException {
        return getMapper().readValue(json, Block.class);
    }

    public static InfoDificultyBlockchain jsonToInfoDifficulty(String json) throws JsonProcessingException {
        return getMapper().readValue(json, InfoDificultyBlockchain.class);
    }

    public static Laws jsonToLaw(String json) throws JsonProcessingException {
        return getMapper().readValue(json, Laws.class);
    }

    public static InfoDemerageMoney jsonInfoDemerageMoney(String json) throws JsonProcessingException {
        return getMapper().readValue(json, InfoDemerageMoney.class);
    }

    public static CurrentLawVotes jsonToVote(String json) throws JsonProcessingException {
        return getMapper().readValue(json, CurrentLawVotes.class);
    }

    public static LawEligibleForParliamentaryApproval jsonToCurrentLaw(String json) throws JsonProcessingException {
        return getMapper().readValue(json, LawEligibleForParliamentaryApproval.class);
    }

    public static Account jsonToAccount(String json) throws JsonProcessingException {
        return getMapper().readValue(json, Account.class);
    }

    public static CurrentLawVotesEndBalance jsonToCurrentLawVotesBalance(String json) throws JsonProcessingException {
        return getMapper().readValue(json, CurrentLawVotesEndBalance.class);
    }

    public static EntityChain jsonToEntityChain(String json) throws JsonProcessingException {
        return getMapper().readValue(json, EntityChain.class);
    }

    public static DtoTransaction jsonToDtoTransaction(String json) throws JsonProcessingException {
        return getMapper().readValue(json, DtoTransaction.class);
    }

    public static DataShortBlockchainInformation jsonToDataShortBlockchainInformation(String json) throws JsonProcessingException {
        return getMapper().readValue(json, DataShortBlockchainInformation.class);
    }

    public static List<DtoTransaction> jsonToDtoTransactionList(String json) throws JsonProcessingException {
        return getMapper().readValue(json, new TypeReference<List<DtoTransaction>>(){});
    }

    public static Map<String, Account> balances(String json) throws JsonProcessingException {
        return getMapper().readValue(json, new TypeReference<Map<String, Account>>(){});
    }

    //сохранение скользящего окна
    public static void saveWindowsToFile(Map<Long, Map<String, Account>> windows, String filePath) throws IOException {
        getMapper().writeValue(new File(filePath), windows);
    }

    //загрузка скользящего окна
    public static Map<Long, Map<String, Account>> loadWindowsFromFile(String filePath) {
        try {
            LinkedHashMap<Long, Map<String, Account>> windows = getMapper().readValue(
                    new File(filePath),
                    new TypeReference<LinkedHashMap<Long, Map<String, Account>>>() {}
            );

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
