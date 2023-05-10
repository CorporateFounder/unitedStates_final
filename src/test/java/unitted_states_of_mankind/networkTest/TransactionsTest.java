package unitted_states_of_mankind.networkTest;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.network.Transactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class TransactionsTest extends Transactions {
    private double sendDollarRandom = 10.0;
    private double sendPowerRandom = 10;


    private int howMuchTransactions;
    private List<AccountSimulation> list;

    public void setSendDollarRandom(double sendDollarRandom) {
        this.sendDollarRandom = sendDollarRandom;
    }

    public void setSendPowerRandom(double sendPowerRandom) {
        this.sendPowerRandom = sendPowerRandom;
    }

    public TransactionsTest() {
    }

    public TransactionsTest(int howMuchTransactions, List<AccountSimulation> list) {
        this.howMuchTransactions = howMuchTransactions;
        this.list = list;
    }


    @Override
    public List<DtoTransaction> getTransactions() {
        Directors directors = new Directors();
        List<DtoTransaction> transactions = new ArrayList<>();
        try {

            Base base = new Base58();
            Random random = new Random();



            for (int i = 0; i < howMuchTransactions; i++) {
                transactions = new ArrayList<>();
                AccountSimulation senderSim = list.get(random.nextInt(list.size()));
                AccountSimulation custmerSim = list.get(random.nextInt(list.size()));
                double sendMoney = 0.0;
                sendMoney = random.nextDouble() * sendDollarRandom;

                if(senderSim.equals(Seting.BASIS_ADDRESS))
                {
                    System.out.println("basis sener cannot send before mining");
                    senderSim = list.get(random.nextInt(list.size()));
                }


                //внедрение тестовых законов тест
                int createLaws = random.nextInt(10) ;

                boolean created = false;
                Laws laws = new Laws();

                double sendPower = 0.0;
                int timeSendPower = random.nextInt(10) ;

                if (timeSendPower == 2){
                    sendPower = random.nextDouble() * sendPowerRandom;


                    switch (createLaws){
                        case 1:
                            String weapon = "Firearms laws";


                            String articleFirst = "Persons over the age of 23 have the right to own a service weapon";
                            String articleSecond = "it is allowed to carry firearms outside the storage case" +
                                    "firearms, in public places," +
                                    "only in those states that allow the carrying of weapons in public places by state law";
                            List<String> lawsList = new ArrayList<>();
                            lawsList.add(articleFirst);
                            lawsList.add(articleSecond);

                            laws = new Laws(weapon, lawsList);
                            created = true;
                            break;
                        case 2:
                            String private_military_companies = "private military company laws";


                            List<String> lawsList2 = new ArrayList<>();
                            laws = new Laws(private_military_companies, lawsList2);
                            created = true;
                            break;

                        case 3:
                            String drug_law = "drug law";

                            List<String> lawsList3 = new ArrayList<>();
                            String firstLaws = "narcotic substances are allowed to be produced only in those states, " +
                                    "which are permitted by the laws of these states";
                            String secondLaws = "Drug use is not a crime";
                            String thirdLaws = "Possession of narcotic substances is not a crime if the quantity" +
                                    "no more than 3 doses of this substance, where a safe dose is determined for each substance, " +
                                    "not causing overdose in each state separately";
                            String fourthLaws = "Permission to sell narcotic substances is determined by the legislation of each " +
                                    "state individually, and also determines at what age to sell these substances";
                            String fiveLaws = "each narcotic substance must be sold only after the buyer " +
                                    "informed of the possible consequences and signed that he agreed to purchase, the receipt " +
                                    "is a document that can be presented to the court as the fact that the seller informed " +
                                    "the buyer, the receipt remains with the seller and the same copy with the buyer ";
                            lawsList3.add(firstLaws);
                            lawsList3.add(secondLaws);
                            lawsList3.add(thirdLaws);
                            lawsList3.add(fourthLaws);
                            lawsList3.add(fiveLaws);
                            laws = new Laws(drug_law, lawsList3);
                            created = true;
                            break;

                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            int size = directors.getDirectors().size();
                            int choose = random.nextInt(size);
                            System.out.println("getTransaction: ");
                            Director possion = directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString());
                            switch (choose){
                                case 1:
                                case 2:
                                    possion = directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString());
                                    break;

                                case 5:

                                    break;

                                case 7:

                                    break;

                                default:
                                    possion = directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString());

                            }
                            List<String > possions = new ArrayList<>();
                            possions.add(senderSim.getPublicKey());
                            laws = new Laws(possion.getName(), possions);
                            possions = new ArrayList<>();
                            created =true;
                            break;

                        default:
                            created = false;
                            break;

                    }
                }


                boolean vote = random.nextBoolean();
                VoteEnum voteEnum = VoteEnum.YES;
                if(vote == false)
                    voteEnum = VoteEnum.NO;

                double minerMoney = 0.0;
                minerMoney = random.nextDouble() * 2;

                DtoTransaction transactionGold = null;
                if(created == true){

                    transactionGold = new DtoTransaction(senderSim.getPublicKey(), laws.getHashLaw(), sendMoney, sendPower, laws, minerMoney, voteEnum);
                }else {
                    transactionGold = new DtoTransaction(senderSim.getPublicKey(), custmerSim.getPublicKey(), sendMoney, sendPower, laws, minerMoney, voteEnum);
                }

                PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderSim.getPrivateKey()));
                byte[] signGold = UtilsSecurity.sign(privateKey, transactionGold.toSign());
                transactionGold.setSign(signGold);
                transactions.add(transactionGold);

            }


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
