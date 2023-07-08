package International_Trade_Union.utils;

import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.InfoDificultyBlockchain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.vote.CurrentLawVotes;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

public class UtilsJson {
    public static String objToStringJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, object);
        return writer.toString();
    }

    public static Object jsonToListBLock(String json, Class cls) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, cls);
    }

    public static List<Block> jsonToListBLock(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<Block>>(){});
    }
    public static List<Account> jsonToListAccounts(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<Account>>(){});
    }

    public static Set<String> jsonToSetAddresses(String json) throws  JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Set<String>>(){});
    }
    public static Block jsonToBLock(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Block.class);
    }

    public static InfoDificultyBlockchain jsonToInfoDifficulty(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, InfoDificultyBlockchain.class);
    }

    public static Laws jsonToLaw(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Laws.class);
    }

    public static InfoDemerageMoney jsonInfoDemerageMoney(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, InfoDemerageMoney.class);
    }

    public static CurrentLawVotes jsonToVote(String json) throws  JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CurrentLawVotes.class);
    }

    public static LawEligibleForParliamentaryApproval jsonToCurrentLaw(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, LawEligibleForParliamentaryApproval.class);
    }
    public static Account jsonToAccount(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Account.class);
    }
    public static EntityChain jsonToEntityChain(String json) throws  JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, EntityChain.class);
    }

    public static DtoTransaction jsonToDtoTransaction(String json) throws  JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, DtoTransaction.class);

    }

    public static List<DtoTransaction> jsonToDtoTransactionList(String json) throws  JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<DtoTransaction>>(){});
    }
}
