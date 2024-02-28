package International_Trade_Union.model.comparator;

import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.model.HostEndDataShortB;

import java.util.Comparator;

public class HostEndDataShortBComparator implements Comparator<HostEndDataShortB> {

    @Override
    public int compare(HostEndDataShortB h1, HostEndDataShortB h2) {
        DataShortBlockchainInformation d1 = h1.getDataShortBlockchainInformation();
        DataShortBlockchainInformation d2 = h2.getDataShortBlockchainInformation();


        // Сравнение bigRandomNumber
        if (d1.getBigRandomNumber() + d1.getSize() != d2.getBigRandomNumber() + d2.getSize()) {
            return Integer.compare(d2.getBigRandomNumber(), d1.getBigRandomNumber());
        }

        // Сравнение hashCount
        if (d1.getHashCount() != d2.getHashCount()) {
            return Long.compare(d2.getHashCount(), d1.getHashCount());
        }

        // Сравнение staking
        if (d1.getStaking() != d2.getStaking()) {
            return Double.compare(d2.getStaking(), d1.getStaking());
        }


        // Сравнение transactions
        return Long.compare(d2.getTransactions(), d1.getTransactions());
    }
}