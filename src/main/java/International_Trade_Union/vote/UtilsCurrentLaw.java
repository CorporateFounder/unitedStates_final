package International_Trade_Union.vote;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsCurrentLaw {
    //подсчет по штучно баланса
    public static Map<String, CurrentLawVotes> calculateVote(Map<String, CurrentLawVotes> votes, List<Account> governments, Block block) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        List<String> signs = new ArrayList<>();


        System.out.println("calculate voting: index: " + block.getIndex());
        for (int j = 0; j < block.getDtoTransactions().size(); j++) {

            DtoTransaction transaction = block.getDtoTransactions().get(j);


            if (signs.contains(transaction.getSign())) {
                System.out.println("this transaction signature has already been used and is not valid: sender: "
                        + transaction.getSender() + " customer: " + transaction.getCustomer());
                continue;
            } else {

                signs.add(base.encode(transaction.getSign()));
//                System.out.println("we added new sign transaction");
            }
            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            if (transaction.verify() && transaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                for (Account account : governments) {
                    //основатель не может участвовать в голосовании
                    //!block.getFounderAddress().equals(transaction.getSender())
                    if (transaction.getSender().equals(account.getAccount())) {
                        CurrentLawVotes currentLawVotes = votes.get(transaction.getCustomer());

                        if (currentLawVotes == null) {
                            currentLawVotes = new CurrentLawVotes();
                            currentLawVotes.setAddressLaw(transaction.getCustomer());
                            currentLawVotes.setYES(new HashSet<>());
                            currentLawVotes.setNO(new HashSet<>());

                            votes.put(transaction.getCustomer(), currentLawVotes);
                        }

                        if (transaction.getVoteEnum().equals(VoteEnum.YES)) {

                            currentLawVotes.getYES().add(transaction.getSender());
                            currentLawVotes.getNO().remove(transaction.getSender());

                        } else if (transaction.getVoteEnum().equals(VoteEnum.NO)) {
                            currentLawVotes.getNO().add(transaction.getSender());
                            currentLawVotes.getYES().remove(transaction.getSender());
                        } else if (transaction.getVoteEnum().equals(VoteEnum.REMOVE_YOUR_VOICE)) {
                            currentLawVotes.getNO().remove(transaction.getSender());
                            currentLawVotes.getYES().remove(transaction.getSender());
                        }
                    }
                }

            }

        }


        return votes;

    }


    //подсчет целиком баланса
    public static Map<String, CurrentLawVotes> calculateVotes(List<Account> governments, List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, CurrentLawVotes> votes = new HashMap<>();
        for (Block block : blocks) {
            calculateVote(votes, governments, block);
        }

        return votes;

    }

    //возвращаяет усредненное количество голосов,
    //суть проста если есть один акаунт и он имеет 100 акций
    //и проголосовал за один закон то все сто акций будут для этого закона как сто голосов
    //если за два закона то 100/2 то есть если он на протяжении трех лет проголосовал
    //за n законов, то его голоса делятся на n.
    public static Map<String, Integer> calculateAverageVotesYes(Map<String, CurrentLawVotes> votesMap) {
        Map<String, Integer> voteAverage = new HashMap<>();
        for (Map.Entry<String, CurrentLawVotes> current : votesMap.entrySet()) {
            for (String yesVoteAddress : current.getValue().getYES()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress, count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress, count);
                }
            }

        }
        return voteAverage;
    }

    //подсчитывает голоса No
    public static Map<String, Integer> calculateAverageVotesNo(Map<String, CurrentLawVotes> votesMap) {
        Map<String, Integer> voteAverage = new HashMap<>();
        for (Map.Entry<String, CurrentLawVotes> current : votesMap.entrySet()) {
            for (String yesVoteAddress : current.getValue().getNO()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress, count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress, count);
                }
            }

        }
        return voteAverage;
    }


}
