package unitted_states_of_mankind;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.HostEndDataShortB;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.comparator.HostEndDataShortBComparator;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;

import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.*;
import static International_Trade_Union.utils.UtilsBalance.rollbackCalculateBalance;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class Testing {

    /**  if (this.index > Seting.NEW_ALGO_MINING) {
     MerkleTree merkleTree = new MerkleTree(this.getDtoTransactions());
     String hash = merkleTree.getRoot() + this.previousHash + this.minerAddress + this.founderAddress
     + this.randomNumberProof + this.minerRewards + this.hashCompexity + this.timestamp +
     this.index;
     return UtilsUse.sha256hash(hash);
     } else {
     return UtilsUse.sha256hash(jsonString());

     }*/


    @Test
    public void testAccounts () throws JSONException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
      DtoTransaction transaction = UtilsJson.jsonToDtoTransaction("DtoTransaction(sender=21Qsp2EjJhYhqP1fnWm6UufnEtavocHXJbS8MhAV9UKwJ, customer=21Qsp2EjJhYhqP1fnWm6UufnEtavocHXJbS8MhAV9UKwJ, digitalDollar=2000.0, digitalStockBalance=0.0, laws=Laws(packetLawName=, laws=[], hashLaw=), bonusForMiner=0.0, voteEnum=STAKING, sign=[48, 70, 2, 33, 0, -88, -41, 55, 25, -77, 15, 66, -32, -105, -64, 7, 101, -12, 122, -102, 97, -38, -82, -119, -121, -1, -95, 24, 88, 13, -24, 1, 61, -43, -33, -13, 127, 2, 33, 0, -117, 3, 12, 73, 56, -79, -47, -22, -118, 58, -76, -85, -128, -82, 9, -121, 15, 42, -113, 98, -86, 43, -3, -59, 87, 77, -32, -110, 74, -67, -96, -3])");
      Base base = new Base58();
        System.out.println(base.encode(transaction.getSign()));
    }
    @Test
    public void testDto() throws JsonProcessingException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.1818181818,\"digitalStockBalance\":0.1818181818,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIHWb5voJj8D5PgHAjhTbRR4NzjJ81e2qalfkIQRTSIeyAiEAsGRdj9UOcHP6AA769GxXbJzIAWxWCwRVzFb5hEhvO0M=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.375,\"digitalStockBalance\":0.375,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIB2Uk7VL0WbT3kIYmqpyY8TE/9+ttqt0yijMuxeLMSX7AiAAhdU9Ecd8i5PDDDb8BiBOL5MUaa7SwA1pPHLoMpjiow==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.3333333333,\"digitalStockBalance\":0.3333333333,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIBANj/gvJEXcib8mazHXHoabUPcJL2kYeIrLmvMCcteiAiBMSzYL5m1C5U1swKH8US3GzxSjplYz6c5hgxois5oBqg==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDmByNyAUmbslorqQHXxdaZ4uNIbGpISob9ZVZZWMFNGQIhAJ7x0fzEyY1TDYdk5yG5GfmLoEXchDqUljnzO34AgXx/\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.1428571429,\"digitalStockBalance\":0.1428571429,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD7uAbvePL9AaxuO4CmmfY9uGkYQ5aS2YjHqOzMqjiqeQIhAMX/kaVP1UAOijyJZ59PlVwHH4+0S+ZbYXBhK+WoMh/U\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD6oeNLSlXz5QKTsEamaIB/BAD1k/Iw6+NhsTdIKn0hJAIhALIrVxOfMS75gzYh13+UTR/HEyj16EuWCmjpN+ol7PVS\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2,\"digitalStockBalance\":0.2,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDlnBFbOHXFGVRVT177ep4GdzmgImn4ZhYEHdlCDL+FqAIhAPbdWPk/V5tdaABDinqtgsgbvwRycfVvOluM0dEWobs9\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3,\"digitalStockBalance\":0.3,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIF992QNkfmdQbbwpNtf5deRVrgnvdaU9g1CM36cYbNlyAiEAsmjZMotPwuHNyQE4oM66IXlVsxeUYA6mYXJBAZX8LiE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCICO+dC/uAgKKAzG7yadYT8aVyD5XXGRemL687ESdZRetAiBa8jVuolOGAcIVKBSXxkm1gx1yueUxNqC2tVlbrC7YEQ==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQD27Am6Pj3oZjWomzgLXMHQatNpG7YJLRJmlJLG2n+Z2AIgGz/WzLT33FrkBDZutlnSCAB1mfXd1yrC8H1k07x46wI=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCngg4JsnXwYLvijt0mlQyuDLUuNsA4fhDDV+EIBVAURwIgfvO9LulhXjo4gyggCvWRxLQzAfnAoxrUV0jOBF7+w4o=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2727272727,\"digitalStockBalance\":0.2727272727,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIB2Z6LKCQmV8eQXYsTzS6dDacJI+FP+AjgXRpZjRtgZdAiEAnuKAMdobl4AyKESR838WxmakXMiBNAWgJnXEDhMQxkE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIAiDpHPK4+oTGbXO3aaeM0ShIRMVisJALU7aIxeCwA2aAiEAz0sBLWb/jLKJmTGnwM9zTuzVGLrfHpWekcZyV+jvx+c=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.1111111111,\"digitalStockBalance\":0.1111111111,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICT0CNS3u86jtyY8PwudsdMXludJrhqPEYVhfZyg1LfiAiEArCF+XBOSi0T/36BtHqS16KEWXNpZBerEkDod2puxwu4=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3636363636,\"digitalStockBalance\":0.3636363636,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQC855htlhaOie87d3TEreB/pkrPfXkUyJwNik0K4VSRVgIhALpnoC8al0pzcghHYo83M8vPLQ3DjHNOFgvjqDfEJQw/\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":33.06,\"digitalStockBalance\":33.06,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICrSIQhI1qvxMRpRL/MTq8Ix0Fwfde6nlyWdB8oL0AZYAiEA3TMEaI5JzEDGpgiHaWnm98alpJOmJx4ewqGTrqlfOPc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":330.6,\"digitalStockBalance\":330.6,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIA1b2EOkfwXqUa9WCb/IiCAMA3jtmRTwomvBab8WVP8RAiEAt65RTNPJeDEb9h5wb46L6zDmoBAxAfIioHDH5dqW550=\"}],\"previousHash\":\"a083d84224200809c12c94b8148cf3375008d110188811a9424a051129251302\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5404319570533998,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1722276549000,\"index\":286893,\"hashBlock\":\"24cc51b8c8c4198102462741322100542800ee004c0165258918ee2190814106\"}");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQC9K0I1UX6IzwFXWS6SbRNFENJTKsGL2bEcuVpcjiyBLwIhAOowvrhqOgI50n9NxH9GNFy5QXLCsSCf3FTj5Y7I5Sy8\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2AxiWkZcJusJ3epWzUZ4RB3nXZAVJTDVHkERVVdaDhL7i\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIAO0+S4z+vWXfsklxDxI/Qdc93/yZ7Qo+y0sXDeIEImsAiEA0Uxnd8Jx+FOjrDuwYm9M54SPQwegDaDKb1gh+WEvM/4=\"}],\"previousHash\":\"560e302592708b3022041a34843a001e000218080008008424890514768b39c9\",\"minerAddress\":\"2AxiWkZcJusJ3epWzUZ4RB3nXZAVJTDVHkERVVdaDhL7i\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":66666738750061255,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722804706000,\"index\":289078,\"hashBlock\":\"05285a211c8a1a8893f007440030204ac213004004021e1b8829203a0542c112\"}");

        List<DtoTransaction> dtoTransactions = block.getDtoTransactions();
        DtoTransaction dtoTransaction = dtoTransactions.get(0);
        DtoTransaction dtoTransaction1 = block1.getDtoTransactions().get(0);
        System.out.println(dtoTransaction);

        dtoTransaction.setDigitalStockBalance(0.00000000);
        dtoTransaction.setDigitalDollar(0.0);
        dtoTransaction.setBonusForMiner(1);

       boolean result = UtilsUse.isTransaction(dtoTransaction);
        System.out.println("result: " + result);
    }

    @Test
    public void DeleteAddress() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<HostEndDataShortB> hosts = new ArrayList<>();
        hosts.add(new HostEndDataShortB("http://0.0.0.0:82", null));
        hosts.add(new HostEndDataShortB("http://37.27.60.116:55", null));
        hosts.add(new HostEndDataShortB("http://194.87.236.238:82", null));
        Set<String> nodes = new HashSet<>();
        initiateProcess(hosts);

//        initiateProcess(hosts);


//        Mining.deleteFiles("C://"+ORIGINAL_POOL_URL_ADDRESS_FILE);
    }
    public void initiateProcess(List<HostEndDataShortB> sortPriorityHost) {

        Set<String> allAddresses = new HashSet<>();
        try {
            // Считать все адреса из файла
            allAddresses = UtilsAllAddresses.readLineObject("C://"+Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                 NoSuchProviderException | InvalidKeyException e) {
            return;
        }

        // Потокобезопасный список для доступных узлов
        List<HostEndDataShortB> availableHosts = Collections.synchronizedList(new ArrayList<>());

        // Потокобезопасное множество для недоступных узлов
        Set<String> unresponsiveAddresses = Collections.synchronizedSet(new HashSet<>());

        // Проверяем состояние всех узлов
        List<CompletableFuture<Void>> checkFutures = sortPriorityHost.stream()
                .map(host -> CompletableFuture.runAsync(() -> {
                    boolean isResponding = false;
                    for (int attempt = 0; attempt < 3; attempt++) {
                        try {
                            String response = UtilUrl.readJsonFromUrl(host.getHost() + "/confirmReadiness", 7000);
                            isResponding = true;
                            System.out.println("conecting: " + host.getHost());
                            if ("ready".equals(response)) {
                                synchronized (availableHosts) {
                                    availableHosts.add(host);
                                }
                                break;
                            }
                        } catch (ConnectException e) {
                            synchronized (unresponsiveAddresses) {
                                String hostPort = extractHostPort(host.getHost());
                                System.out.println("host: " + hostPort);
                                unresponsiveAddresses.add(hostPort);
                            }
                            break; // Не нужно повторять попытки, если соединение отказано
                        } catch (Exception e) {
                            System.out.println("host: error" + host.getHost());
                        }
                    }
                    if (!isResponding) {
                        synchronized (unresponsiveAddresses) {
                            String hostPort = extractHostPort(host.getHost());
                            System.out.println("host: " + hostPort);
                            unresponsiveAddresses.add(hostPort);
                        }
                    }
                }))
                .collect(Collectors.toList());

        // Ждем завершения всех проверок
        CompletableFuture.allOf(checkFutures.toArray(new CompletableFuture[0])).join();

        // Логируем недоступные узлы
        System.out.println("before: " + allAddresses);
        System.out.println("for delete: " + unresponsiveAddresses);

        // Нормализуем адреса для удаления
        Set<String> normalizedAllAddresses = allAddresses.stream()
                .map(this::extractHostPort)
                .collect(Collectors.toSet());

        // Удаляем неответившие узлы из общего списка
        normalizedAllAddresses.removeAll(unresponsiveAddresses);
        System.out.println("after: " + normalizedAllAddresses);

        // Удаляем файл с адресами
        Mining.deleteFiles("C://"+Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);

        // Перезаписываем оставшиеся адреса в файл
        normalizedAllAddresses.forEach(address -> {
            try {
                UtilsAllAddresses.saveAllAddresses(address, "C://"+Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
            } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException |
                     NoSuchProviderException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

        // Ограничиваем количество ожидаемых узлов до 7
        int nodesToWait = Math.min(availableHosts.size(), 7);

        if (nodesToWait == 0) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(nodesToWait);

        // Теперь ждем, пока неготовые узлы станут готовыми
        List<CompletableFuture<Void>> waitFutures = availableHosts.stream()
                .map(host -> CompletableFuture.runAsync(() -> {
                    while (true) {
                        try {
                            String response = UtilUrl.readJsonFromUrl(host.getHost() + "/confirmReadiness", 2000);
                            if ("ready".equals(response)) {
                                latch.countDown();
                                break;
                            }
                            Thread.sleep(1000); // Пауза перед следующей проверкой
                        } catch (Exception e) {
                            latch.countDown(); // Уменьшаем счетчик, если узел стал недоступен
                            break;
                        }
                    }
                }))
                .collect(Collectors.toList());

        try {
            // Ждем максимум 25 секунд
            boolean completed = latch.await(25, TimeUnit.SECONDS);
            if (!completed) {
            }
        } catch (InterruptedException e) {
        }

    }


    private DataShortBlockchainInformation fetchDataShortBlockchainInformation(String host) throws IOException, JSONException {
        String jsonGlobalData = UtilUrl.readJsonFromUrl(host + "/datashort");
        System.out.println("jsonGlobalData: " + jsonGlobalData + " host: " + host);
        return UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
    }

    public List<HostEndDataShortB> sortPriorityHost(Set<String> hosts) {
        Set<String> modifiedHosts = new HashSet<>(hosts);
        modifiedHosts.addAll(Seting.ORIGINAL_ADDRESSES);

        Set<String> selectedHosts = modifiedHosts.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        listHost -> {
                            Collections.shuffle(listHost);
                            return listHost.stream().limit(RANDOM_HOSTS).collect(Collectors.toSet());
                        }
                ));

        List<CompletableFuture<HostEndDataShortB>> futures = new ArrayList<>();
        Set<String> unresponsiveAddresses = Collections.synchronizedSet(new HashSet<>());

        System.out.println("start: sortPriorityHost: " + selectedHosts);

        for (String host : selectedHosts) {
            CompletableFuture<HostEndDataShortB> future = CompletableFuture.supplyAsync(() -> {
                try {
                    DataShortBlockchainInformation global = fetchDataShortBlockchainInformation(host);
                    if (global != null && global.isValidation()) {
                        return new HostEndDataShortB(host, global);
                    }
                } catch (IOException | JSONException e) {
                    System.err.println("Error fetching data from host: " + host + " Error: " + e.getMessage());
                    System.err.println("Marking as unresponsive: " + host);
                    unresponsiveAddresses.add(host);
                }
                return null;
            });

            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<HostEndDataShortB>> allComplete = allFutures.thenApplyAsync(result ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(result1 -> result1 != null)
                        .collect(Collectors.toList())
        );

        List<HostEndDataShortB> resultList = allComplete.join();
        Collections.sort(resultList, new HostEndDataShortBComparator());

        System.out.println("finish: sortPriorityHost: " + resultList);

        System.out.println("---------------------------------------");
        System.out.println("hosts before: " + hosts);
        hosts.removeAll(unresponsiveAddresses);
        System.out.println("hosts after removing unresponsive: " + hosts);
        System.out.println("unresponsiveAddresses: " + unresponsiveAddresses);
        System.out.println("---------------------------------------");

        return resultList;
    }

    private String extractHostPort(String host) {
        // Assuming the format of host is "http://<ip>:<port>", we extract the host and port part
        try {
            URL url = new URL(host);
            return url.getHost() + ":" + url.getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return host;  // Return the original host if URL parsing fails
        }
    }


    public List<DtoTransaction> getDuplicateTransactions(Block block) {
        Base base = new Base58();
        Map<String, List<DtoTransaction>> groupedBySignature = block.getDtoTransactions().stream()
                .collect(Collectors.groupingBy(t -> base.encode(t.getSign())));

        List<DtoTransaction> duplicates = new ArrayList<>();

        for (List<DtoTransaction> transactions : groupedBySignature.values()) {
            if (transactions.size() > 1) {
                duplicates.addAll(transactions.subList(1, transactions.size())); // Добавляем все кроме первого
            }
        }

        return duplicates;
    }
    @Test
    public void dublicate() throws JsonProcessingException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.1818181818,\"digitalStockBalance\":0.1818181818,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIHWb5voJj8D5PgHAjhTbRR4NzjJ81e2qalfkIQRTSIeyAiEAsGRdj9UOcHP6AA769GxXbJzIAWxWCwRVzFb5hEhvO0M=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.375,\"digitalStockBalance\":0.375,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIB2Uk7VL0WbT3kIYmqpyY8TE/9+ttqt0yijMuxeLMSX7AiAAhdU9Ecd8i5PDDDb8BiBOL5MUaa7SwA1pPHLoMpjiow==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.3333333333,\"digitalStockBalance\":0.3333333333,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIBANj/gvJEXcib8mazHXHoabUPcJL2kYeIrLmvMCcteiAiBMSzYL5m1C5U1swKH8US3GzxSjplYz6c5hgxois5oBqg==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDmByNyAUmbslorqQHXxdaZ4uNIbGpISob9ZVZZWMFNGQIhAJ7x0fzEyY1TDYdk5yG5GfmLoEXchDqUljnzO34AgXx/\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.1428571429,\"digitalStockBalance\":0.1428571429,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD7uAbvePL9AaxuO4CmmfY9uGkYQ5aS2YjHqOzMqjiqeQIhAMX/kaVP1UAOijyJZ59PlVwHH4+0S+ZbYXBhK+WoMh/U\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD6oeNLSlXz5QKTsEamaIB/BAD1k/Iw6+NhsTdIKn0hJAIhALIrVxOfMS75gzYh13+UTR/HEyj16EuWCmjpN+ol7PVS\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2,\"digitalStockBalance\":0.2,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDlnBFbOHXFGVRVT177ep4GdzmgImn4ZhYEHdlCDL+FqAIhAPbdWPk/V5tdaABDinqtgsgbvwRycfVvOluM0dEWobs9\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3,\"digitalStockBalance\":0.3,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIF992QNkfmdQbbwpNtf5deRVrgnvdaU9g1CM36cYbNlyAiEAsmjZMotPwuHNyQE4oM66IXlVsxeUYA6mYXJBAZX8LiE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCICO+dC/uAgKKAzG7yadYT8aVyD5XXGRemL687ESdZRetAiBa8jVuolOGAcIVKBSXxkm1gx1yueUxNqC2tVlbrC7YEQ==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQD27Am6Pj3oZjWomzgLXMHQatNpG7YJLRJmlJLG2n+Z2AIgGz/WzLT33FrkBDZutlnSCAB1mfXd1yrC8H1k07x46wI=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCngg4JsnXwYLvijt0mlQyuDLUuNsA4fhDDV+EIBVAURwIgfvO9LulhXjo4gyggCvWRxLQzAfnAoxrUV0jOBF7+w4o=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2727272727,\"digitalStockBalance\":0.2727272727,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIB2Z6LKCQmV8eQXYsTzS6dDacJI+FP+AjgXRpZjRtgZdAiEAnuKAMdobl4AyKESR838WxmakXMiBNAWgJnXEDhMQxkE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIAiDpHPK4+oTGbXO3aaeM0ShIRMVisJALU7aIxeCwA2aAiEAz0sBLWb/jLKJmTGnwM9zTuzVGLrfHpWekcZyV+jvx+c=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.1111111111,\"digitalStockBalance\":0.1111111111,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICT0CNS3u86jtyY8PwudsdMXludJrhqPEYVhfZyg1LfiAiEArCF+XBOSi0T/36BtHqS16KEWXNpZBerEkDod2puxwu4=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3636363636,\"digitalStockBalance\":0.3636363636,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQC855htlhaOie87d3TEreB/pkrPfXkUyJwNik0K4VSRVgIhALpnoC8al0pzcghHYo83M8vPLQ3DjHNOFgvjqDfEJQw/\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":33.06,\"digitalStockBalance\":33.06,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICrSIQhI1qvxMRpRL/MTq8Ix0Fwfde6nlyWdB8oL0AZYAiEA3TMEaI5JzEDGpgiHaWnm98alpJOmJx4ewqGTrqlfOPc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":330.6,\"digitalStockBalance\":330.6,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIA1b2EOkfwXqUa9WCb/IiCAMA3jtmRTwomvBab8WVP8RAiEAt65RTNPJeDEb9h5wb46L6zDmoBAxAfIioHDH5dqW550=\"}],\"previousHash\":\"a083d84224200809c12c94b8148cf3375008d110188811a9424a051129251302\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5404319570533998,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1722276549000,\"index\":286893,\"hashBlock\":\"24cc51b8c8c4198102462741322100542800ee004c0165258918ee2190814106\"}");
        // Добавление дубликата (как в вашем оригинальном коде)
        DtoTransaction dublicate = block.getDtoTransactions().get(0);
        DtoTransaction transaction = new DtoTransaction(dublicate.getSender(),
                dublicate.getCustomer(),
                dublicate.getDigitalDollar(),
                dublicate.getDigitalStockBalance(),
                dublicate.getLaws(),
                dublicate.getBonusForMiner(),
                dublicate.getVoteEnum());
        transaction.setSign(Arrays.copyOf(dublicate.getSign(), dublicate.getSign().length));
        block.getDtoTransactions().add(transaction);

        DtoTransaction transaction2 = new DtoTransaction(dublicate.getSender(),
                dublicate.getCustomer(),
                dublicate.getDigitalDollar(),
                dublicate.getDigitalStockBalance(),
                dublicate.getLaws(),
                dublicate.getBonusForMiner(),
                dublicate.getVoteEnum());
        transaction2.setSign(Arrays.copyOf(dublicate.getSign(), dublicate.getSign().length));
        block.getDtoTransactions().add(transaction2);

        // Получение списка дубликатов
        List<DtoTransaction> duplicateTransactions = getDuplicateTransactions(block);

        // Вывод результатов
        System.out.println("find dublicate: " + duplicateTransactions.size());
        for (DtoTransaction duplicate : duplicateTransactions) {
            System.out.println("dublicate: " + new Base58().encode(duplicate.getSign()));
        }
    }
    @Test
    public void hash() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.1818181818,\"digitalStockBalance\":0.1818181818,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIHWb5voJj8D5PgHAjhTbRR4NzjJ81e2qalfkIQRTSIeyAiEAsGRdj9UOcHP6AA769GxXbJzIAWxWCwRVzFb5hEhvO0M=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.375,\"digitalStockBalance\":0.375,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIB2Uk7VL0WbT3kIYmqpyY8TE/9+ttqt0yijMuxeLMSX7AiAAhdU9Ecd8i5PDDDb8BiBOL5MUaa7SwA1pPHLoMpjiow==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.3333333333,\"digitalStockBalance\":0.3333333333,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIBANj/gvJEXcib8mazHXHoabUPcJL2kYeIrLmvMCcteiAiBMSzYL5m1C5U1swKH8US3GzxSjplYz6c5hgxois5oBqg==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDmByNyAUmbslorqQHXxdaZ4uNIbGpISob9ZVZZWMFNGQIhAJ7x0fzEyY1TDYdk5yG5GfmLoEXchDqUljnzO34AgXx/\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.1428571429,\"digitalStockBalance\":0.1428571429,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD7uAbvePL9AaxuO4CmmfY9uGkYQ5aS2YjHqOzMqjiqeQIhAMX/kaVP1UAOijyJZ59PlVwHH4+0S+ZbYXBhK+WoMh/U\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"wCfTQCrCs37ZNEShPBzPyjkZa1LjMUwnWn27dLQ8bj6o\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD6oeNLSlXz5QKTsEamaIB/BAD1k/Iw6+NhsTdIKn0hJAIhALIrVxOfMS75gzYh13+UTR/HEyj16EuWCmjpN+ol7PVS\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2,\"digitalStockBalance\":0.2,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDlnBFbOHXFGVRVT177ep4GdzmgImn4ZhYEHdlCDL+FqAIhAPbdWPk/V5tdaABDinqtgsgbvwRycfVvOluM0dEWobs9\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3,\"digitalStockBalance\":0.3,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIF992QNkfmdQbbwpNtf5deRVrgnvdaU9g1CM36cYbNlyAiEAsmjZMotPwuHNyQE4oM66IXlVsxeUYA6mYXJBAZX8LiE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCICO+dC/uAgKKAzG7yadYT8aVyD5XXGRemL687ESdZRetAiBa8jVuolOGAcIVKBSXxkm1gx1yueUxNqC2tVlbrC7YEQ==\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"m2iC2FBEcqNa4PPghE45AaAuXjeAC7f1SfanM4LMjQkM\",\"digitalDollar\":0.2222222222,\"digitalStockBalance\":0.2222222222,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQD27Am6Pj3oZjWomzgLXMHQatNpG7YJLRJmlJLG2n+Z2AIgGz/WzLT33FrkBDZutlnSCAB1mfXd1yrC8H1k07x46wI=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.125,\"digitalStockBalance\":0.125,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCngg4JsnXwYLvijt0mlQyuDLUuNsA4fhDDV+EIBVAURwIgfvO9LulhXjo4gyggCvWRxLQzAfnAoxrUV0jOBF7+w4o=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.2727272727,\"digitalStockBalance\":0.2727272727,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIB2Z6LKCQmV8eQXYsTzS6dDacJI+FP+AjgXRpZjRtgZdAiEAnuKAMdobl4AyKESR838WxmakXMiBNAWgJnXEDhMQxkE=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"274NjHBzcVSBYKHN6dmHVADJ3bjnfaDkYyrDT25aLig7m\",\"digitalDollar\":0.2857142857,\"digitalStockBalance\":0.2857142857,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIAiDpHPK4+oTGbXO3aaeM0ShIRMVisJALU7aIxeCwA2aAiEAz0sBLWb/jLKJmTGnwM9zTuzVGLrfHpWekcZyV+jvx+c=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"eHacKrkFscdJe4tVNwSQznotMv2dDY2iXsgRG1HVLHB7\",\"digitalDollar\":0.1111111111,\"digitalStockBalance\":0.1111111111,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICT0CNS3u86jtyY8PwudsdMXludJrhqPEYVhfZyg1LfiAiEArCF+XBOSi0T/36BtHqS16KEWXNpZBerEkDod2puxwu4=\"},{\"sender\":\"h392yDGLyzh4Bk3A8hfGvi8qiGK84X4TWWTSTe55jMM4\",\"customer\":\"gC49xWaTVtQVKamqM7Neq3F7UGyqma3aXHJ5ep6WwcdY\",\"digitalDollar\":0.3636363636,\"digitalStockBalance\":0.3636363636,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQC855htlhaOie87d3TEreB/pkrPfXkUyJwNik0K4VSRVgIhALpnoC8al0pzcghHYo83M8vPLQ3DjHNOFgvjqDfEJQw/\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":33.06,\"digitalStockBalance\":33.06,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICrSIQhI1qvxMRpRL/MTq8Ix0Fwfde6nlyWdB8oL0AZYAiEA3TMEaI5JzEDGpgiHaWnm98alpJOmJx4ewqGTrqlfOPc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"digitalDollar\":330.6,\"digitalStockBalance\":330.6,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIA1b2EOkfwXqUa9WCb/IiCAMA3jtmRTwomvBab8WVP8RAiEAt65RTNPJeDEb9h5wb46L6zDmoBAxAfIioHDH5dqW550=\"}],\"previousHash\":\"a083d84224200809c12c94b8148cf3375008d110188811a9424a051129251302\",\"minerAddress\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5404319570533998,\"minerRewards\":0.0,\"hashCompexity\":17,\"timestamp\":1722276549000,\"index\":286893,\"hashBlock\":\"24cc51b8c8c4198102462741322100542800ee004c0165258918ee2190814106\"}");
        String immutablePart = block.jsonString(); // Предположим, что jsonString() возвращает JSON без randomNumberProof
        String partialHash = DigestUtils.sha256Hex(immutablePart);
        String hash = DigestUtils.sha256Hex(partialHash + block.getRandomNumberProof());
        System.out.println("Calculated hash: " + hash);
        System.out.println("Expected hash: " + block.getHashBlock());
        System.out.println("hashForTransaction: " + block.hashForTransaction());


    }

    @Test
    public  void calculateScore(){


        double sumBalance = 1;
        double count = 6;
        int diff = 18;
        int result = 4;
        double transactionSumPoints = UtilsUse.calculateScore(sumBalance / count, 1);
        double transactionCountPoints = count * 0.5;
        long score = UtilsUse.calculateScore(2.01, 1);

        double maxTransactionSumPoints = (diff - 17 + score) * 0.6;
        if (transactionSumPoints > maxTransactionSumPoints) {
            transactionSumPoints = maxTransactionSumPoints;
        }

        double sum = transactionCountPoints + transactionSumPoints;
        System.out.println("score: " + score);
        result = (int) ( (result + (diff * 30)) + score + sum);
        System.out.println("result: " + result);
    }

    @Test
    public void test() throws IOException {

    }
    @Test
    public void testReadHost() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        String url = "E://resources/poolAddress/";
        Set<String> strings = UtilsAllAddresses.readLineObject(url);
        List<HostEndDataShortB> list = new ArrayList<>();

        System.out.println("before: " + strings);
       strings = strings.stream().map(t->t.replaceAll("\"", "")).collect(Collectors.toSet());

        System.out.println("after: " + strings);




        //сортировать здесь.
        // сортировка


    }
    @Test
    public void testHost() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
       String url = "http://localhost:8085/putNode";
       Set<String> strings = BasisController.getNodes();
        System.out.println(strings);



       MyHost myHost = new MyHost("http://localhost:8083/putNode", "server", "key");
       String json = UtilsJson.objToStringJson(myHost);


        UtilUrl.sendPost(json, url );

    }

    @Test
    public void TestHostComparator() {
        List<HostEndDataShortB> hostEndDataShortBS = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int size = random.nextInt(10);
            int hashCount = random.nextInt(10);
            BigDecimal staking = BigDecimal.valueOf(random.nextDouble() * 10);
            int transaction = random.nextInt(10);
            int big = i;
            if (i >= 7) {
                big = 7;
            }
            DataShortBlockchainInformation temp = new DataShortBlockchainInformation(size, true, hashCount, staking, transaction, big);
            HostEndDataShortB tempHost = new HostEndDataShortB("host: " + i, temp);
            hostEndDataShortBS.add(tempHost);
        }
        System.out.println("before list: ");
        hostEndDataShortBS.forEach(System.out::println);
        System.out.println("____________________________________");
        Collections.sort(hostEndDataShortBS, new HostEndDataShortBComparator());
        System.out.println("after list: ");
        hostEndDataShortBS.forEach(System.out::println);
    }
    @Test
    public void random() throws JsonProcessingException {
        long score = 0;
        int number = 0;

        for (int i = 0; i < 1000; i++) {
            String json = "{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":14.5,\"digitalStockBalance\":14.5,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICJUELZJqVWgIOxaPc8gOJPf6Iq9WDX0QCdTDiHXTP92AiEAkUfXC5Eouj64J7WPXkjhxoWXTw+AY/yU/SQ05+R7VRk=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"25giJad2YEEJqecPsbLkiHBp3KgVpCipENzK1rNZuPbYg\",\"digitalDollar\":145.0,\"digitalStockBalance\":145.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCJSq59MSO/2B9w84dCVxmWRggxLFB1u2e1+m001mvTSAIhAMKnH3VGhgBO0mDdvr5eim6xX4J9E/jO3fvQWgSvzTwK\"}],\"previousHash\":\"a56a0e519e1630a1c0580481430aa00621001001020810200805830387440837\",\"minerAddress\":\"25giJad2YEEJqecPsbLkiHBp3KgVpCipENzK1rNZuPbYg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":24319438452809543,\"minerRewards\":0.0,\"hashCompexity\":26,\"timestamp\":1704555268000,\"index\":167337,\"hashBlock\":\"48135472000c814010085824170a4a0f80520c62788310154480885018c1c803\"}";
            List<Block> blocks = new ArrayList<Block>();

            blocks.add(UtilsJson.jsonToBLock(json));
            blocks.get(0).setHashBlock("4111780951498215c031c98cc201b4690208828c3c844660104bacdc11040909");
            blocks.get(0).setPreviousHash("41c408c41718500200306b0200a10a040280e08f4d2844f30258a23eb9715490");
            Account miner = new Account("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1000));
             number = UtilsUse.bigRandomWinner(blocks.get(0),  miner);
             score = UtilsUse.calculateScore(5242881, 10);
        }

        System.out.println("score: " + score);
        System.out.println("number: " + number);

//        System.out.println("pow: " + Math.pow(19, 1.5));

    }


    @Test
    public void getBlock() throws IOException {
        String s = "http://localhost:8083";
        int index = 0;
        Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(index), s + "/block"));
        System.out.println("block: " + block);
    }

    @Test
    public void sub() throws IOException {
        String str = "{\"start\":167337,\"finish\":167338}";
        String s = " http://194.87.236.238:82";
        SubBlockchainEntity subBlockchainEntity = (SubBlockchainEntity) UtilsJson.jsonToClass(str, SubBlockchainEntity.class);
        List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(str, s + "/sub-blocks"));
        System.out.println(subBlocks.size());
    }

    @Test
    public void TestHashBlock() throws IOException {
        final int numThreads = Runtime.getRuntime().availableProcessors() - 2;
        final int numThreads2 = Runtime.getRuntime().availableProcessors();
        System.out.println(numThreads);
        System.out.println(numThreads2);
    }
    public static void main(String[] args) throws JsonProcessingException {
        String actualJ = "{\"dtoTransactions\":[{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":138.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDoHB7j6VJr3G6IOmZpVFd4DtouNNYgVB/Uhjx2Fwd5PAiEAyMu+wNd6y8wugT3Ki8BtLPmS5+93HfCb60p03L5dvTE=\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":20.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIGhhp8zYarceDMl15uZGcggP8yR7PWduOkpA6PX5od8eAiAMB1LaXsNfxE/s9mccnb54Eon3JL26/1CeVtZSLcbYvQ==\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":3.0,\"digitalStockBalance\":1.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfCZ9t3s+cRtPSKj7yvBIj2G33sGnOKWlesm+Z9WuVKgIgfM/MfJ4r4B/zSfb6Ky/A+bTVo9s1G+Rb1LZQMFaexRA=\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":1.0,\"digitalStockBalance\":1.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDZxb1O8Q+s9W90iZrWwSZiRhCIKik6/oaUAQ/EvpUO3wIhAPN0ZrZzuuTI3jyMXEWeYVHKarh75+kCwEgu6WDwNDcp\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":1.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDbPQc4eN0RNwnJKsIKVUN7twNqD9DARORJKIyy/e2fcwIgVjaNcJlBPwlpaIWfGyUjZZkkSZgcV4yTUjHGtD3kyzo=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":13.0,\"digitalStockBalance\":13.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDDOJXTnc5Gg6EMpkeK6rqmK/53chJORZHZQ4yANboy7gIhAOlVnGwv4ylPF9wn8JUg6OUwDczgj2p64dXr870vMISG\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"BUDGET\",\"digitalDollar\":130.0,\"digitalStockBalance\":130.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":4.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQC8AJYMNWwko4ybq2rm2OrZHHgOai6Aze6VMiUsJ9eqkQIgdpRmZETcUlBwZc51UMUE/O/b0v94za+6tR2kbBPcvdo=\"}],\"previousHash\":\"00000777a26bed8b58d10f344d94bd3048348b66cce633223aff1b39efd204b7\",\"minerAddress\":\"BUDGET\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":515012,\"minerRewards\":8.0,\"hashCompexity\":1,\"timestamp\":1702731467000,\"index\":127488,\"hashBlock\":\"00000caf287b044cec2445d666528697af51a95e079bf09a8c8e4abf5813ee77\"}";
        String prevJ = "{\"dtoTransactions\":[{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":145.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCTFqeYDMUThwkeM4D2tueoUc5Vz2PqoHt4wvSZHWJ7PgIhAO4kKiHYEcZcbbmZT54WVQ9GazDvovTdo3gtQFYX+GOb\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":13.0,\"digitalStockBalance\":13.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfMGbzuDX/CzVlybjFgR48xpKKCg7NKj4SkayKiyQxxQIgCO1RDGyWBYAO61jk2cZ8tQweqtFTqvkYzBnQ62HSu+Y=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"BUDGET\",\"digitalDollar\":130.0,\"digitalStockBalance\":130.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCID8bfgSNZPlip+WSHSLrVB8VGlp81QWdTXgkXeUde8ikAiEAi2d7UjxFifMVA/uZvOigPvZ00HoSrvcp5sfL9AhMlYg=\"}],\"previousHash\":\"000005b2203e97829d6cfb01b36f4ae9e9fb50ff7a608d0532d4da702a968e7b\",\"minerAddress\":\"BUDGET\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":425566,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1702731065000,\"index\":127487,\"hashBlock\":\"00000777a26bed8b58d10f344d94bd3048348b66cce633223aff1b39efd204b7\"}";
        Block actual = UtilsJson.jsonToBLock(actualJ);
        Block prev = UtilsJson.jsonToBLock(prevJ);
       double G =  UtilsUse.blocksReward(actual.getDtoTransactions(), prev.getDtoTransactions(), 20);
        System.out.println("G: " + G);
        double reward = (5+G) * 26;
        reward = reward/ DOLLAR;
        System.out.println("Reward: " + reward);
    }




    private static volatile boolean blockFound = false;
    private static volatile String foundHash = "-";

    @Test
    public void testFindBlock() {
        String hash = "fdgfgdg";

        //перебирает число nonce чтобы найти хеш
        int randomNumberProof = 0;
        //используется для определения кто-нибудь уже успел добыть блок.

        String target = MiningUtils.calculateTarget(1);
        while (true) {

            //перебирает число nonce чтобы найти хеш


//            hash = UtilsUse.sha256hash("hello" + randomNumberProof);
            hash = MiningUtils.calculateHash("hello" + randomNumberProof);

            System.out.println("random: " + randomNumberProof);

            //если true, то прекращаем майнинг. Правильный блок найден
//            if (UtilsUse.chooseComplexity(hash, 1, 122499)) {
//                System.out.println("block found: hash: " + hash);
//                break;
//            }//если true, то прекращаем майнинг. Правильный блок найден
//            String target = BlockchainDifficulty.calculateTarget(1);
            //33290382
            if (MiningUtils.isValidHash(hash, target)) {
                System.out.println("block found: hash: " + hash);
                break;
            }
            randomNumberProof++;
        }
        System.out.println("hash: " + hash);
    }



    @Test
    public void allCoundDollar() {
        int reward = 5;
        int mult = 29;
        double sum = 113000000;
        for (int i = 0; i < mult; i++) {
            int step = mult - i;
            step = step < 1 ? 1 : step;
            sum += (reward * step) * 576 * 365;
        }
        System.out.printf("sum %f.00000000\n", sum);
        double min = 5 * 576 * 365;
        double max = 8 * 576 * 365;
        System.out.printf("min %f.00000000\n", min);
        System.out.printf("max %f.00000000\n", max);

    }

    @Test
    public void testReward() {
        int V28 = 1;
        int index = 7072;
        int diff = 8;
        int reductionPeriod = 576 * 14;
        if (index > V28) {
            int step1 = index - V28;
            int step2 = reductionPeriod;
            int step3 = step1 / step2;
            long money = (index - V28)
                    / (576 * 14);


            System.out.println("step1: " + step1);
            System.out.println("step2: " + step2);
            System.out.println("step3: " + step3);

            money = (long) (Seting.MONEY - money);
            System.out.println("result: " + (index - V28));
            System.out.println();
            System.out.println("money: " + money);
            money = money < 1 ? 1 : money;
            System.out.println("diff: " + (diff * money));
            System.out.println("money: " + money);
            System.out.println("index: " + index);


        }
        displayBlockCountdown(V28, index, 14);
    }

    public static void displayBlockCountdown(int v28, int index, int periodInDays) {
        int blocksPerDay = 576;

        if (index > v28) {
            // Вычисляем, сколько блоков прошло с последнего снижения
            int blocksSinceReduction = (index - v28) % (blocksPerDay * periodInDays);

            // Оставшиеся блоки до следующего снижения
            int blocksRemaining = (blocksPerDay * periodInDays) - blocksSinceReduction;


            System.out.println("Blocks remaining: " + blocksRemaining);

        }
    }



    public static int countLeadingZeroBits(byte[] hash) {
        int bitLength = hash.length * 8;
        BitSet bits = BitSet.valueOf(hash);

        int count = 0;
        while (count < bitLength && !bits.get(count)) {
            count++;
        }

        return count;
    }

    @Test
    public void testCountLeadingZeroBits() {
        int nonce = 0;
        int diff = 5;
        Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());
        String hash = "";
        while (true) {
            String text = "hello world" + nonce;
            hash = UtilsUse.sha256hash(text);
            if (countLeadingZeroBits(hash.getBytes()) == diff) {
                break;
            }
            nonce++;
        }
        System.out.println("check: " + (countLeadingZeroBits(hash.getBytes()) == diff));
        System.out.println("hash: " + hash);

        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
        System.out.println("result: " + result);
    }

    @Test
    public void testChangeDiff() {
        int index = 151833;
        while (true) {
            if (index % 288 == 0) {
                break;
            }
            index++;
        }
        System.out.println("index: " + index);
    }


    @Test
    public void testServer() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        int finish = 0 + Seting.PORTION_DOWNLOAD;
        int start = 0;

        SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(start, finish);


        System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
        String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
        System.out.println("1:sublockchainJson: " + subBlockchainJson);
        String localhost = "http://localhost:8083";
        String server = "http://194.87.236.238:80";
        List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, server + "/sub-blocks"));
        List<Block> subBlocks1 = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, localhost + "/sub-blocks"));
        List<Block> fromFile = UtilsBlock.readLineObject("C://resources/blockchain");
        Block from = fromFile.get(0);

        System.out.println("******************************************");
        Block one = subBlocks.get(0);
        System.out.println(one);

        System.out.println("******************************************");
        Block two = subBlocks1.get(0);
        System.out.println(two);
        System.out.println("******************************************");
        System.out.println(one.equals(two));
        System.out.println("hash: " + one.getHashBlock().equals(two.getHashBlock()));
        System.out.println("getPreviousHash: " + one.getPreviousHash().equals(two.getPreviousHash()));
        System.out.println("getMinerAddress: " + one.getMinerAddress().equals(two.getMinerAddress()));
        System.out.println("getFounderAddress: " + one.getFounderAddress().equals(two.getFounderAddress()));
        System.out.println("getDtoTransactions: " + one.getDtoTransactions().equals(two.getDtoTransactions()));
        System.out.println("getIndex: " + (one.getIndex() == two.getIndex()));

        System.out.println("from: " + from.getTimestamp());
        System.out.println("one: " + one.getTimestamp());
        System.out.println("two: " + two.getTimestamp());

        System.out.println(one.getHashBlock().equals(one.hashForTransaction()));
        System.out.println(two.getHashBlock().equals(two.hashForTransaction()));


//        List<Block> subBlocks = UtilsBlock.readLineObject("C://resources/blockchain/");
        System.out.println("1:download sub block: " + subBlocks.size());
        Block prev = null;
        for (int i = 0; i < subBlocks.size(); i++) {
            if (prev == null) {
                prev = subBlocks.get(i);
                if (!prev.getHashBlock().equals(prev.hashForTransaction())) {
                    System.out.printf("wrong hash genesis: index: %d, actual %s, expected %s\n"
                            , prev.getIndex(), prev.getHashBlock(), prev.hashForTransaction());
                }
                continue;
            }
            if (!subBlocks.get(i).getHashBlock().equals(subBlocks.get(i).hashForTransaction())) {
                System.out.printf("wrong hash: index: %d, actual %s, expected %s\n"
                        , subBlocks.get(i).getIndex(), subBlocks.get(i).getHashBlock(), subBlocks.get(i).hashForTransaction());
            }
            System.out.println();
        }


    }

    @Test
    public void entityBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        List<Account> accountList = balances.entrySet().stream()
                .map(t -> t.getValue())
                .collect(Collectors.toList());

        List<EntityAccount> entityAccounts = UtilsAccountToEntityAccount.accountsToEntityAccounts(balances);
        List<Account> testAccount = UtilsAccountToEntityAccount.EntityAccountToAccount(entityAccounts);
        assertEquals(testAccount, accountList);

    }

    @Test
    public void entityBlock() throws IOException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"27MkHGZZnYkNtQMevRqBfAU2Pnu7LJEWC61AzMvAC31V3\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEHrC1uypFUgsXM/Z6yN/AM1qb+Q545RiU5FGFoFVvnGAiEA/adtMyE6ffnsOEVlXl+rbx2NstboFVEoY4D0EZuXfow=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDXMAVvYsJLJLDVGm0bIfJhd58Jzv2OKrAlWVWH6mWlOAiACDeYNeQlkupje6M53315qV2W5VVHLLW+6nvh3zi32sw==\"}],\"previousHash\":\"00da347d3b3d4c9fc5c826fcff7b06569b673c63fc1afdaf7b9075b175f772be\",\"minerAddress\":\"27MkHGZZnYkNtQMevRqBfAU2Pnu7LJEWC61AzMvAC31V3\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685946378876,\"index\":20,\"hashBlock\":\"039bf7b8b0be69125b93b26018df6b342d7c64305b6405cfa3b4813dae2bc682\"}");
        EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
        System.out.println("***************************************");

        System.out.println(block);
        System.out.println("***************************************");

        Block testBlock = UtilsBlockToEntityBlock.entityBlockToBlock(entityBlock);
        System.out.println(testBlock);

        System.out.println("getHashCompexity: " + (block.getHashCompexity() == testBlock.getHashCompexity()));
        System.out.println("getHashBlock: " + (block.getHashBlock().equals(testBlock.getHashBlock())));
        System.out.println("getMinerRewards: " + (block.getMinerRewards() == testBlock.getMinerRewards()));
        System.out.println("getFounderAddress: " + (block.getFounderAddress().equals(testBlock.getFounderAddress())));
        System.out.println("getMinerAddress: " + (block.getMinerAddress().equals(testBlock.getMinerAddress())));
        System.out.println("getPreviousHash: " + (block.getPreviousHash().equals(testBlock.getPreviousHash())));
        System.out.println("getIndex: " + (block.getIndex() == testBlock.getIndex()));
        System.out.println("getTimestamp: " + (block.getTimestamp().equals(testBlock.getTimestamp())));
        System.out.println("getRandomNumberProof: " + (block.getRandomNumberProof() == testBlock.getRandomNumberProof()));
        for (int i = 0; i < testBlock.getDtoTransactions().size(); i++) {
            DtoTransaction transaction = block.getDtoTransactions().get(i);
            DtoTransaction transaction1 = testBlock.getDtoTransactions().get(i);
            System.out.println("getCustomer: " + (transaction.getCustomer().equals(transaction1.getCustomer())));
            System.out.println("getSender: " + (transaction.getSender().equals(transaction1.getSender())));
            System.out.println("getBonusForMiner: " + (transaction.getBonusForMiner() == transaction1.getBonusForMiner()));
            System.out.println("getDigitalDollar: " + (transaction.getDigitalDollar() == transaction1.getDigitalDollar()));
            System.out.println("getDigitalStockBalance: " + (transaction.getDigitalStockBalance() == transaction1.getDigitalStockBalance()));
            System.out.println("getVoteEnum: " + (transaction.getVoteEnum().equals(transaction1.getVoteEnum())));
            System.out.println("getSign: " + (transaction.getSign().equals(transaction1.getSign())));

            System.out.println("transaction.getLaws(): " + transaction.getLaws());
            System.out.println("transaction1.getLaws(): " + transaction1.getLaws());

            if (transaction.getLaws().getHashLaw() == null && transaction1.getLaws().getHashLaw() == null) {
                System.out.println("getHashLaw true");
            }
            if (transaction.getLaws().getLaws() == null && transaction1.getLaws().getLaws() == null) {
                System.out.println("getLaws true");
            }


        }

//        testBlock.getDtoTransactions().get(0).getLaws().setPacketLawName("32");
        assertEquals(testBlock, block);


    }

    @Test
    public void multipleFindHash() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        System.out.println("find hash method");
        int randomNumberProofStatic = 0;
        int differrentNumber = 0;
        int INCREMENT_VALUE = 100000;
        int hashCoplexity = 2;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            System.out.println(":i: " + i);
            int finalDifferrentNumber = differrentNumber;
            Thread thread = new Thread(() -> {
                long nonce = randomNumberProofStatic + finalDifferrentNumber;
                String tempHash = "";
                int size = UtilsStorage.getSize();
                Timestamp previus = new Timestamp(UtilsTime.getUniversalTimestamp());
                String nameThread = Thread.currentThread().getName();
                while (!blockFound) {


//                    System.out.printf("\tTrying %d to find a block: ThreadName %s:\n ", nonce , nameThread);
                    Instant instant1 = Instant.ofEpochMilli(UtilsTime.getUniversalTimestamp());
                    Instant instant2 = previus.toInstant();

                    Duration duration = Duration.between(instant1, instant2);
                    long seconds = duration.getSeconds();

                    tempHash = UtilsUse.sha256hash("hello: " + nonce);


                    if (seconds > 10 || seconds < -10) {
                        long milliseconds = instant1.toEpochMilli();
                        previus = new Timestamp(milliseconds);
                        previus.setTime(milliseconds);

                        //проверяет устаревание майнинга, если устарел - прекращает майнинг

//                        int tempSize = UtilsStorage.getSize();
//                        if (size < tempSize) {
//                            Mining.miningIsObsolete = true;
//                            System.out.println("someone mined a block before you, the search for this block is no longer relevant and outdated: " + tempHash);
//
//                            synchronized (Block.class) {
//                                if (!blockFound) {
//                                    blockFound = true;
//                                    Mining.miningIsObsolete = true;
//                                    foundHash = tempHash;
//                                }
//                            }
//                            System.out.println("Block found: hash: " + tempHash);
//                            break;
//
//                        }

                    }

                    //если true, то прекращаем майнинг
//                    if (Mining.isIsMiningStop()) {
//                        System.out.println("mining will be stopped");
//
//                        synchronized (Block.class) {
//                            if (!blockFound) {
//                                blockFound = true;
//                                Mining.miningIsObsolete = true;
//                                foundHash = tempHash;
//                            }
//                        }
//                        System.out.println("Block found: hash: " + tempHash);
//
//                        foundHash= tempHash;
//                        break;
//
//                    }
//

                    String target = BlockchainDifficulty.calculateTarget(hashCoplexity);
                    BigInteger bigTarget = BlockchainDifficulty.calculateTargetV30(hashCoplexity);
                    //если true, то прекращаем майнинг. Правильный блок найден
                    if (UtilsUse.chooseComplexity(tempHash, hashCoplexity, 30001, target, bigTarget)) {
                        System.out.println("block found: hash: " + tempHash);
                        synchronized (Testing.class) {
                            if (!blockFound) {
                                blockFound = true;
//                                Mining.miningIsObsolete = true;
                                foundHash = tempHash;
                            }
                        }
                        System.out.println("Block found: hash: " + tempHash);
                        foundHash = tempHash;
                        break;
                    }
                    nonce++;
                }

            });

            differrentNumber += INCREMENT_VALUE;
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("foundhash: " + foundHash);
    }


    @Test
    public void testRollback() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIGUmscRx3/ry3+JCXEn5IUPvztkp8wssWVncXrboTT7WAiBzoushgN/njGCBu2giDT4BdPlt6lWk1ng6eDG3O6+10A==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIHSBd0UTPOBcYLp8CgcK0GDLTNWdLQjKXoX7RDIjCP5cAiBkVtlG037xou6eTEK3EnbhdmpDDwClemoUGKB3c2CoJg==\"}],\"previousHash\":\"010568240909052200811b000a049ac25a8df8ee44204046e0070c38dc108608\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5717820000077086321,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355281000,\"index\":268086,\"hashBlock\":\"08b04c22294ca9e3291d5168c2163ba000a0400900060e20452230480640909c\"}\n");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIQCq3OPKoLDRTMn39QX0Dw3j5aHkMFyB8pqGLfhUaDrFhwIfFrLYsXiNAQJpB9B8N0EL5Ft40nkf9npaOqODMlo8jQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDmb7a4gSr4HA/xOxWDxfLlhrBeWKoxKDuKYWdw4HlfDwIgBEJ4RQjbg4XRIG4VoxNpkljQ+K0dy7WZVPxbcnONlFg=\"}],\"previousHash\":\"08b04c22294ca9e3291d5168c2163ba000a0400900060e20452230480640909c\",\"minerAddress\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":3602879713466940,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355436000,\"index\":268087,\"hashBlock\":\"c0811412a835915404c01202540128a201a0052518c06d460600225a7432963a\"}\n");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEgT2vk/wOxjFlYJoADR8JtApy5pp28NrHRk4rDmD1kbAiEArjA9mpnk4LZJ2LqRBeq/kJwKanJJzqoX63CkmIws7UY=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCYGK9AwF24/oxhJsRRsu+GmkySWWtlBoqnUPsRpvYIJwIgGnOwcYPdq0UD+txIgM2B759XufOt7FjFGkE6qpqYz8w=\"}],\"previousHash\":\"c0811412a835915404c01202540128a201a0052518c06d460600225a7432963a\",\"minerAddress\":\"s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":17113678609889273,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355643000,\"index\":268088,\"hashBlock\":\"c8021823126b424287805002cb14ae4fb40022a8948842606592105218104040\"}\n");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQC/hnEtsAP4E6wMTvhu0ejO2nWVh5KoP2IwPywj3vP8NQIgMTplpc0CXXHQbCNtzrh2rgeYb6MTcnc2cBEHMi9rxZc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDyzfwiWbYCpVy3lp4fjVLp0fzAUPUtwbSZgtU7AVuycAIgSFqsdYAtrhBDbT0zn3RVYoUfEjDtziqmYj8ILvjgqcc=\"}],\"previousHash\":\"c8021823126b424287805002cb14ae4fb40022a8948842606592105218104040\",\"minerAddress\":\"hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":17113678593776157,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355820000,\"index\":268089,\"hashBlock\":\"88262801c521884304084066a1000646a11400084ed2b1642a1d2a42a4328516\"}\n");
        List<Block> deleteBlocks = new ArrayList<>();
        deleteBlocks.add(block);
        deleteBlocks.add(block1);
        deleteBlocks.add(block2);
        deleteBlocks.add(block3);

        Map<String, Account> balances = new HashMap<>();

        BigDecimal minerBalance = new BigDecimal("266.79999999999995");
        BigDecimal founderBalance = BigDecimal.valueOf(26.679999999999996 * 4); // Replace this with the actual value if needed

        balances.put("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", new Account("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", founderBalance, founderBalance, BigDecimal.ZERO));
        balances.put("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", new Account("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", minerBalance, minerBalance, BigDecimal.ZERO));
        balances.put("28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az", new Account("28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az", minerBalance, minerBalance, BigDecimal.ZERO));
        balances.put("s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq", new Account("s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq", minerBalance, minerBalance, BigDecimal.ZERO));
        balances.put("hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf", new Account("hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf", minerBalance, minerBalance, BigDecimal.ZERO));


        for (int i = deleteBlocks.size() - 1; i >= 0; i--) {
            Block temp = deleteBlocks.get(i);
            System.out.println("rollBackAddBlock4 :BasisController: addBlock3: blockchain is being updated: index" + temp.getIndex());

            balances = rollbackCalculateBalance(balances, temp);
        }
        System.out.println("===========================================");
        balances.entrySet().stream().forEach(System.out::println);
        System.out.println("===========================================");
        double balance = balances.get("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43").getDigitalDollarBalance().doubleValue();

        System.out.printf("balance: %.16f", balance);
        System.out.printf("round: %.16f" , round(balance, 10));

    }


    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Test
    public void generateOriginalBlocks() throws IOException, JSONException, InterruptedException {

        for (int i = 1; i < 2000; i++) {

            System.out.println("block generate i: " + i);
            try {
                UtilUrl.readJsonFromUrl("http://localhost:8082/mine");
            } catch (IOException e) {
                System.out.println("error test mining");
                continue;
            }

        }

    }

    @Test
    public void testSorted() {
        // Изначальное значение хэш рейта
        long hashRate = 1; // Начинаем с 1 H/s

        // Вычисление SHA-256 хешей
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // Пример вычисления хеша строки "Hello, World!"
            String input = "Hello, World!";
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;

            // Увеличиваем хэш рейт до тех пор, пока время выполнения цикла не превысит одну секунду
            while (elapsedTime < 1000) {
                long numberOfHashes = hashRate;

                for (long i = 0; i < numberOfHashes; i++) {
                    sha256.digest(inputBytes);
                }

                long endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;

                // Увеличиваем хэш рейт в 10 раз для следующей итерации
                hashRate *= 2;
            }

            System.out.println("Количество хешей SHA-256, которые может перебирать один поток процессора в одну секунду: " + (hashRate / 10));
            System.out.println("Время, затраченное на вычисление хешей: " + elapsedTime + " миллисекунд");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testLimitedMoney() {
        int block = 0;
        double digitalDollarMining = 400;
        double digitalStockMining = 400;
        double dollarPercent = 0.2;
        double stockPercent = 0.4;
        double digitalDollarAccount = 0;
        double digitalStockAccount = 0;
        int year = 360 * 600;

        for (int i = 0; i < year; i++) {
            for (int j = 0; j < 576; j++) {
                block++;
                if (block % (180 * 576) == 0) {
                    digitalDollarAccount = digitalStockAccount - digitalDollarAccount * dollarPercent / 100;
                    digitalStockAccount = digitalStockAccount - digitalStockAccount * stockPercent / 100;
                }

                digitalDollarAccount += digitalDollarMining;
                digitalStockAccount += digitalStockMining;
            }
//            if(i%360 == 2){
//                digitalDollarMining = digitalDollarMining/2;
//                digitalStockAccount/= 2;
//            }
            if (digitalDollarAccount < 56000000000.0 && digitalDollarAccount > 5100000000.0) {
                //при таких настройках, верхняя граница, должна быть достигнута к 334 году
                //денежная масса больше не будет выше пять миллиардов сто сорок миллионов
                System.out.println("block: " + block + " index: " + i + " year: " + (i % 360));
                break;
            }
        }

        System.out.printf("digital dollar balance: %f\n", digitalDollarAccount);
        System.out.printf("digital stock balance: %f\n", digitalStockAccount);
    }


    @Test
    public void testHashDifficulty() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        String hash = "8c41b1ddc22ba3402ed0489952ba45161b279abb3b3b8ebc6bede062a21f83ff";
//        hash = bytesToBinary(hash.getBytes());
//        int count = countLeadingZeros(hash);
//       printBitSet(hash.getBytes());
//       BlockchainDifficulty.printBinary(hash.getBytes());
//        System.out.println(count);
        System.out.println("**********************************************");
        String hello = "папрарпарыек546нгу авпвкпкккккккккккккккккккккуке";
        int nonce = 0;
        String testHash = "";
        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


        int diffInStr = 8;
        int difficulty = 2;
        while (true) {

            testHash = UtilsUse.sha256hash(hello + nonce);


            hash = testHash;
//            hash = bytesToBinary(testHash.getBytes());

//            String binary = bytesToBinary(hash.getBytes());
//            int leadingZeros = countLeadingZeros(binary);
            int leadingZeros = countLeadingZeroBits(hash.getBytes());
            boolean countInHash = UtilsUse.hashComplexity(hash, diffInStr);
//            boolean countInHash = true;
            boolean test = leadingZeros >= difficulty && countInHash;
//            boolean test = countInHash;

            if (test) {

                break;
            }
            nonce++;

        }
        Timestamp last = new Timestamp(UtilsTime.getUniversalTimestamp());
        Long result = actualTime.toInstant().until(last.toInstant(), ChronoUnit.SECONDS);
        System.out.println("different time: " + result);
        System.out.println("***********************************");
//        printBitSet(testHash.getBytes());

        System.out.println("***********************************");
        BlockchainDifficulty.printBinary(hash.getBytes());
        System.out.println("hash: " + hash);
    }

    @Test
    public void Account() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> originals = SaveBalances.readLineObject("C://strategy1/balance/");
        Account account = originals.get("hzhq1LUk3qCcNyrTGE5pSRrRsYf3HkdSmeu5jap1JUnx");
        System.out.println(account);
    }

    @Test
    public void testHash() throws IOException {
        String json = "{\"dtoTransactions\":[],\"previousHash\":\"previous\",\"minerAddress\":\"mineAdres\",\"founderAddress\":\"founder\",\"randomNumberProof\":1,\"minerRewards\":0.0,\"hashCompexity\":2,\"timestamp\":1694088872091,\"index\":53392,\"hashBlock\":\"8c59604158f2f4ce093e7bef8ae46fc471071d95f24221e85b8b589dcff32a13\"}";
        String wrong = "{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"beACedNewaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIHJezsfngLx2etBXHGA0eTMZOHZ0RGDL32HyJ4Qiv+ArAiEA2LellhnxyDwHUZ/8hz5KgEexhsWRg32k/Q2cd5UJbGo=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCUL3TvVodxb6aHhtfHEPcbxps20nqylU0ksaLq1XJb8AIgMnfMkbAPH8tUpw9LML5JtwLY0o1bXqr3JAuHQazGeB0=\"}],\"previousHash\":\"000006e63b836e089c34bf8d7cb4bf024feb67b3c70e03e6f24bc2fc0b6637d0\",\"minerAddress\":\"beACedNewaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":0,\"minerRewards\":0.0,\"hashCompexity\":5,\"timestamp\":1690488334985,\"index\":24281,\"hashBlock\":\"000000b0bef5b152f5ac9d68a9185e94de3e6a7368e4a2cb7b5e4d4fe2c36208\"}";
        Block block = UtilsJson.jsonToBLock(json);
        Block wrongHash = UtilsJson.jsonToBLock(wrong);
        assertEquals(block.getHashBlock(), block.hashForTransaction());
        assertNotEquals(wrongHash.getHashBlock(), wrongHash.hashForTransaction());
    }


    @Test
    public  void testBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> originals = SaveBalances.readLineObject("C://strategy1/balance/");
        Map<String, Account> forks = SaveBalances.readLineObject("C://strategy2/balance/");
        Map<String, Account> balance = SaveBalances.readLineObject("C://resources/balance/");
        Map<String, Account> differents = new HashMap<>();

        Map<String, Account> cheaterAccount = new HashMap<>();
        Map<String, Integer> countAddressCheater = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("C://cheater miner/1.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split(":");
                if (cheaterAccount.containsKey(arr[0])) {
                    BigDecimal dollar = new BigDecimal(arr[1]).add(cheaterAccount.get(arr[0]).getDigitalDollarBalance());
                    BigDecimal stock = new BigDecimal(arr[2]).add(cheaterAccount.get(arr[0]).getDigitalStockBalance());
                    Account account = new Account(arr[0], dollar, stock, BigDecimal.ZERO);
                    cheaterAccount.put(arr[0], account);
                } else {
                    String address = arr[0];
                    try {
                        BigDecimal dollar = new BigDecimal(arr[1]);
                        BigDecimal stock = new BigDecimal(arr[2]);
                        Account account = new Account(address, dollar, stock, BigDecimal.ZERO);
                        cheaterAccount.put(address, account);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        continue;
                    }
                }

                countAddressCheater.merge(arr[0], 1, Integer::sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        cheaterAccount.forEach((k, v) -> System.out.println("cheater: " + k + " " + v));
        countAddressCheater.forEach((k, v) -> System.out.println("cheater count: " + k + " " + v));

        for (Map.Entry<String, Account> original : originals.entrySet()) {
            String address = original.getKey();
            if (forks.containsKey(address)) {
                BigDecimal originalDollar = original.getValue().getDigitalDollarBalance();
                BigDecimal originalStock = original.getValue().getDigitalStockBalance();
                BigDecimal forkDollar = forks.get(address).getDigitalDollarBalance();
                BigDecimal forkStock = forks.get(address).getDigitalStockBalance();
                BigDecimal resultDollar = originalDollar.subtract(forkDollar).max(BigDecimal.ZERO);
                BigDecimal resultStock = originalStock.subtract(forkStock).max(BigDecimal.ZERO);

                if (resultDollar.compareTo(BigDecimal.ZERO) < 0) {
                    resultDollar = BigDecimal.ZERO;
                    System.out.println("dollar original: " + originalDollar + " fork: " + forkDollar);
                }

                if (resultStock.compareTo(BigDecimal.ZERO) < 0) {
                    resultStock = BigDecimal.ZERO;
                    System.out.println("stock original: " + originalStock + " fork: " + forkStock);
                }

                Account account = new Account(address, resultDollar, resultStock, BigDecimal.ZERO);
                differents.put(address, account);
            } else {
                BigDecimal originalDollar = original.getValue().getDigitalDollarBalance();
                BigDecimal originalStock = original.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock, BigDecimal.ZERO);
                differents.put(address, account);
            }
        }

        HashMap<String, Account> afterFork = new HashMap<>();
        for (Map.Entry<String, Account> different : differents.entrySet()) {
            String address = different.getKey();
            if (forks.containsKey(address)) {
                BigDecimal originalDollar = different.getValue().getDigitalDollarBalance();
                BigDecimal originalStock = different.getValue().getDigitalStockBalance();
                BigDecimal forkDollar = forks.get(address).getDigitalDollarBalance();
                BigDecimal forkStock = forks.get(address).getDigitalStockBalance();

                BigDecimal resultDollar = forkDollar.add(originalDollar);
                BigDecimal resultStock = forkStock.add(originalStock);
                Account account = new Account(address, resultDollar, resultStock, BigDecimal.ZERO);
                afterFork.put(address, account);
            } else {
                BigDecimal originalDollar = different.getValue().getDigitalDollarBalance();
                BigDecimal originalStock = different.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock, BigDecimal.ZERO);
                afterFork.put(address, account);
            }
        }

        for (Map.Entry<String, Account> different : differents.entrySet()) {
            String address = different.getKey();
            if (cheaterAccount.containsKey(address)) {
                BigDecimal dollar = different.getValue().getDigitalDollarBalance().subtract(cheaterAccount.get(address).getDigitalDollarBalance()).max(BigDecimal.ZERO);
                BigDecimal stock = different.getValue().getDigitalStockBalance().subtract(cheaterAccount.get(address).getDigitalStockBalance()).max(BigDecimal.ZERO);

                differents.get(address).setDigitalDollarBalance(dollar);
                differents.get(address).setDigitalStockBalance(stock);
            }
        }

        List<Account> originalAccounts = originals.values().stream().collect(Collectors.toList());
        List<Account> afterForkAccounts = afterFork.values().stream().collect(Collectors.toList());
        List<Account> differentAccounts = differents.values().stream().collect(Collectors.toList());

        BigDecimal originalSumDollar = originalAccounts.stream().map(Account::getDigitalDollarBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal originalSumStock = originalAccounts.stream().map(Account::getDigitalStockBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal afterForkSumDollar = afterForkAccounts.stream().map(Account::getDigitalDollarBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal afterForkSumStock = afterForkAccounts.stream().map(Account::getDigitalStockBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Account> forkAccounts = forks.values().stream().collect(Collectors.toList());

        BigDecimal forkSumDollar = forkAccounts.stream().map(Account::getDigitalDollarBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal forkSumStock = forkAccounts.stream().map(Account::getDigitalStockBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.printf("dollar: original: %f after fork: %f%n", originalSumDollar, afterForkSumDollar);
        System.out.printf("stock: original: %f after fork: %f%n", originalSumStock, afterForkSumStock);
        System.out.printf("different dollar: %f%n", originalSumDollar.subtract(afterForkSumDollar));
        System.out.printf("different stock: %f%n", originalSumStock.subtract(afterForkSumStock));
        System.out.printf("different fork dollar: %f%n", originalSumDollar.subtract(forkSumDollar));
        System.out.printf("different fork stock: %f%n", originalSumStock.subtract(forkSumStock));
        System.out.printf("dollar: %f stock %f%n", forkSumDollar, forkSumStock);

        String address = "tjghGks15LdppYYvZKwb79w6wU2NwgpEeq5Rktj7smHH";
        System.out.println("****************************");
        System.out.println(address + ":" + cheaterAccount.containsKey(address));
        System.out.println("different balance: " + differents.get(address));
        System.out.println("original: " + originals.get(address));
        System.out.println("balance: " + balance.get(address));
        if (forks != null) {
            System.out.println("fork: " + forks.get(address));
            System.out.println("cheater account: " + cheaterAccount.get(address));
        }
    }


    @Test
    public void TestChangeDiff() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InterruptedException {
        Timestamp first = Timestamp.from(Instant.now());
        Thread.sleep(60000);
        Timestamp second = Timestamp.from(Instant.now());
        long result = second.toInstant().until(first.toInstant(), ChronoUnit.MINUTES);
        Long result2 = first.toInstant().until(second.toInstant(), ChronoUnit.MINUTES);
        System.out.println("result1 " + result);
        System.out.println("result2: " + result2);

    }
}
