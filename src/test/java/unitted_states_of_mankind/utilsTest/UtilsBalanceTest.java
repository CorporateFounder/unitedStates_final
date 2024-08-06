package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//TODO высянить почему не списывается правильно долги
@SpringBootTest
public class UtilsBalanceTest {



    @Test
    public void rollbackBalance() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balance = new HashMap<>();
        Account customer = new Account("rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM", BigDecimal.valueOf(100),  BigDecimal.valueOf(50), BigDecimal.valueOf(50));
        Account founder = new Account("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", BigDecimal.valueOf(100),  BigDecimal.valueOf(50), BigDecimal.valueOf(50));
        System.out.println("start:");
        System.out.println( "customer: "+customer);
        System.out.println( "founder: "+founder);
        balance.put(customer.getAccount(), customer);
        balance.put(founder.getAccount(), founder);
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":24.360000000000003,\"digitalStockBalance\":24.360000000000003,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCS52v8TM+rVYBv+Y+FRabj6Lts328TYHyKMbUhY0+uPwIhAPGiwmPhBWdd0lL0cG3CuUgQqxxhCJUWT3I3m/oQ+c46\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":243.60000000000002,\"digitalStockBalance\":243.60000000000002,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCH21sF95mIHj3b3UkqU98yuHSrB94cCxE8tHIAfVlUYMCIQDet6Grc2I/qwtkLcndkWKB0KxZFSoZBIGlAnpe8T4NiQ==\"}],\"previousHash\":\"1693006549e60d1034c002d205d420002261411c4190110450257f2806143c69\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":3258116,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1708598156000,\"index\":204674,\"hashBlock\":\"0a2c080608b958b20809106a22d81aa90bb845463258200a9e00b00064b0a80c\"}");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":24.360000000000003,\"digitalStockBalance\":24.360000000000003,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDvIQqLDf8oU+FC73DkiiNR1+HDT73tkEsVE4b1rRvnUQIhALcU228EKZoAcYpWfjn8gOvR5/C1CiAihubX6k0bfb9N\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":243.60000000000002,\"digitalStockBalance\":243.60000000000002,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCaQuPGZdm4ppoh8ucNL8qRExIRsfCjp1ExO/3oz6OsXgIhANURKjk+eWiAgk8wrazfm9VRn5UOt7LKdSfUjCoIJrqG\"}],\"previousHash\":\"80035027844db00f540911b0114c0bb2020829208ca13595054108607d01d840\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":10808639114936202,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1708598058000,\"index\":204673,\"hashBlock\":\"1693006549e60d1034c002d205d420002261411c4190110450257f2806143c69\"}");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":24.360000000000003,\"digitalStockBalance\":24.360000000000003,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCflVNwa/M3pX7plbI9ZMzBfWt4quDnp8ZWCG7Pqk/aagIgUFQ6vUgaN+ADvrHZhmvq4QlI9C6ywV+5bDTJEwPdajg=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":243.60000000000002,\"digitalStockBalance\":243.60000000000002,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCftyoFk2J02CKkWFRX3GvSOpqTa+O1p9xC61nyowiX+AIhALlguCYrtURReIwTmwSXmrk7hNfCNgm9eYPKZNVnESGq\"}],\"previousHash\":\"194860380c5816204b000882440196769d73828184973c10e020648040dc4059\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":1801439851358743,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1708598043000,\"index\":204672,\"hashBlock\":\"80035027844db00f540911b0114c0bb2020829208ca13595054108607d01d840\"}");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":24.360000000000003,\"digitalStockBalance\":24.360000000000003,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIBlX9dnYHvIUR5niNTdx1DNZ4JkMUxRa1Nv+s9LJ1SgeAiEA//mwqJaBTxF65k2O1qTVCA031IXVN4OiilXfvJ97pVc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":243.60000000000002,\"digitalStockBalance\":243.60000000000002,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDeRELf6YRcdlFqenxDYkdFdoFSCX/T82yWjbdG0b6U5AiBMquOg0M8U13KtWFGWA1BsUzx/XO41B0aTZ74uxICQ1w==\"}],\"previousHash\":\"11280ab864c82430c442a1b8d0010c078043114039b4580026d0da203c148809\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5404319553581238,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1708598021000,\"index\":204671,\"hashBlock\":\"194860380c5816204b000882440196769d73828184973c10e020648040dc4059\"}");
        Block block4 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":24.360000000000003,\"digitalStockBalance\":24.360000000000003,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIAR+DpLNaRLQby7C7OtSt6IUTUz3KAop1ieZK+B/LL4MAiBUP/gCviAIjPeZTlNv+KNsiu6n85nfq/HSddEkZ72zoA==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":243.60000000000002,\"digitalStockBalance\":243.60000000000002,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCu0qw+dXPEf2XMBgzq30/v7LJDUqTWg/VxKbn/NGEOgAIgfJCgxQ7LhUky4yzATapG7HMOn2JU1ALvAadj6oSQz/Y=\"}],\"previousHash\":\"883160054251024194c18d061488761c15280c18d91a0041c919b0004ac801a5\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":9907919181603188,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1708597990000,\"index\":204670,\"hashBlock\":\"11280ab864c82430c442a1b8d0010c078043114039b4580026d0da203c148809\"}");
        UtilsBalance.calculateBalance(balance, block4, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block3, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block2, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block1, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block, new ArrayList<>());
        System.out.println("---------------------------------------------------------------------");
        System.out.println("before");
        System.out.println(balance.get(customer.getAccount()));
        System.out.println(balance.get(founder.getAccount()));
        System.out.println("---------------------------------------------------------------------");
        UtilsBalance.rollbackCalculateBalance(balance, block );
        UtilsBalance.rollbackCalculateBalance(balance, block1);
        UtilsBalance.rollbackCalculateBalance(balance, block2);
        UtilsBalance.rollbackCalculateBalance(balance, block3);
        UtilsBalance.rollbackCalculateBalance(balance, block4);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("after");

        System.out.println(balance.get(customer.getAccount()));
        System.out.println(balance.get(founder.getAccount()));
    }

    @Test
    public void test() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIC8rnPDb8ltwajdu4nY1BvyJboPn2fc/9uQC1XWom1mxAiEAiq9tDEUDlV+lEngDuJmpCnKHbt5aEbjIkFccktn6P0A=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIFKIFafSxvF0B3cAoAhfX4vtPXmWwGajaGGaHfUL11pWAiEA4thPgp8MtgJ3IwQm2xsA+/JxY5nCus6j38tFwfayBGQ=\"}],\"previousHash\":\"4aa09101b5164c401482da008285070428020011141087600204014132aa0ac2\",\"minerAddress\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345686317122146,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722857830000,\"index\":289335,\"hashBlock\":\"8aa912e6301406109a489e80064105ccaa040b9005100044222094380e218441\"}\n");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":27.84,\"digitalStockBalance\":27.84,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDJviFDIUD158qpEBHqMrGNWbovELMNyUS0JGJtaGlTmwIhAPLg5M+VTUCk43D8ivjXchG4ANfe3upwy95c2cRL73Ti\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":278.4,\"digitalStockBalance\":278.4,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDXRQON/BI4ugxRZoqX34lONq3ebbouqRfEUNche9xW4AiAcjEcsR+ouwFMaw+DGA5kObDnQYprFhGKIPaV4iZgD4Q==\"}],\"previousHash\":\"8aa912e6301406109a489e80064105ccaa040b9005100044222094380e218441\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":20000374584817,\"minerRewards\":0.0,\"hashCompexity\":23,\"timestamp\":1722858134000,\"index\":289336,\"hashBlock\":\"8780088c200953628469005799200900381ce680390e05060000480170f226c2\"}\n");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIGwz9GjU5VSwGlZSpP5SiacI2bl/YVNija4MjDpp/ezNAiEAoZzhAZMhku/yCDu5x1l04qLCjxwZ60RzaNGnofX3YdU=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCcyNZSFJMLKC1zGf9QBeNQKDRR/GE8jHviSz69/zxMggIgUtzeY0oWOCs+jnzkBi65ZpWVWV2P6eaflLfAc3p+1ow=\"}],\"previousHash\":\"8780088c200953628469005799200900381ce680390e05060000480170f226c2\",\"minerAddress\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345706449312701,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722858326000,\"index\":289337,\"hashBlock\":\"409645836d020b805a500604100ab711b24198320076484440308804a481c000\"}\n");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICvD1Siol5v/41W8WZmfshIhPiZByDRDi9lBR3CbsHx1AiEAvs5UEWJ4ql2VuA8HlAJKe16NJ1kQYeoTXIaRl34eqkc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD+RGY4EFFYAseK29n4e9nuFOCgk84dJNWrPTS6D4C0SwIhAONs065JgjFsQ/p5wuvDstb31DsQBgx4bDxrsgfgAoQr\"}],\"previousHash\":\"409645836d020b805a500604100ab711b24198320076484440308804a481c000\",\"minerAddress\":\"oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345693289400101,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722858875000,\"index\":289338,\"hashBlock\":\"000c1c9034c011ca8224768040018050acc8d5115e0e620d011000c430883168\"}\n");
        List<Block> blocks = new ArrayList<>();
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        Block prev = block;
        Map<String, Account> balance = new HashMap<>();
        balance.put("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        DataShortBlockchainInformation dataShortBlockchainInformation = new DataShortBlockchainInformation();
        dataShortBlockchainInformation.setValidation(true);
        dataShortBlockchainInformation.setSize(0);
        dataShortBlockchainInformation.setBigRandomNumber(0);
        dataShortBlockchainInformation.setStaking(BigDecimal.ZERO);
        dataShortBlockchainInformation.setTransactions(0);
        dataShortBlockchainInformation.setHashCount(0);
       List<String> sign = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            List<Block> temp = new ArrayList<>();
            temp.add(blocks.get(i));
            dataShortBlockchainInformation = Blockchain.shortCheck(prev, temp, dataShortBlockchainInformation, new ArrayList<>(),  balance, sign);
            prev = blocks.get(i);
        }


        System.out.println("dataL " + dataShortBlockchainInformation);
        for (int i = 0; i < blocks.size(); i++) {
            List<Block> temp = new ArrayList<>();
            temp.add(blocks.get(i));
            dataShortBlockchainInformation = Blockchain.rollBackShortCheck(temp, dataShortBlockchainInformation, balance,sign);
            prev = blocks.get(i);
        }
        System.out.println("dataL " + dataShortBlockchainInformation);

    }


    //TODO исправить sendTest установив баланс отправилтеля
    @Test
    public void SendTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {


        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":14.5,\"digitalStockBalance\":14.5,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCkBX4IBVOq/2mPsOTWIn2N8TPfS5FItHqrEqhuU/YgHAIgKmdMlLXLM4sfnWh0aO0ezTZeInUdc/dh7SDF1qOKPAI=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"digitalDollar\":145.0,\"digitalStockBalance\":145.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":3.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD8iQWqgDwsxDV3ew0yZeUDS+xnI2iNjuNHak4MccU1WgIhAOP1cKFcZfrJ6Oek4UH6cX9FcIBYaA3OwAItctWny1k7\"}],\"previousHash\":\"904045a20888b4581d08c9587242b740cb12603802c440104f10063236603202\",\"minerAddress\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":947262,\"minerRewards\":0.0,\"hashCompexity\":16,\"timestamp\":1704736749000,\"index\":167479,\"hashBlock\":\"185ac49d8c2320b030d4ac8454000b2a21423a00816a042c5403470032439188\"}");
        DtoTransaction transaction = block.getDtoTransactions().get(1);
        transaction.setBonusForMiner(3);
        transaction.setSender("2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG");
        transaction.setCustomer("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43");

        long index = 284984 + 5000;
        System.out.println("index: " + index);
        Account sender = new Account(transaction.getSender(), BigDecimal.valueOf(1000.00000000000001), BigDecimal.valueOf(1000.00000000001), BigDecimal.ZERO);
        Account miner = new Account(block.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        Account recipient = new Account(transaction.getCustomer(), BigDecimal.valueOf(1000.00000000000001), BigDecimal.valueOf(1000.00000000001), BigDecimal.valueOf(0.0));
        System.out.println("sender before: " + sender);
        System.out.println("recipient before: " + recipient);
        System.out.println("minier before: " + miner);
        UtilsBalance.sendMoney(sender, recipient,  BigDecimal.valueOf(transaction.getDigitalDollar()),BigDecimal.valueOf(transaction.getDigitalStockBalance()),  BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
        System.out.println("minier after: " + miner);
        System.out.println("bonus for miner: " + block.getDtoTransactions().get(1).getBonusForMiner());

        System.out.println("roll back");
        UtilsBalance.rollBackSendMoney(sender, recipient,  BigDecimal.valueOf(transaction.getDigitalDollar()),BigDecimal.valueOf(transaction.getDigitalStockBalance()),  BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
        System.out.println("minier after: " + miner);
    }
}


