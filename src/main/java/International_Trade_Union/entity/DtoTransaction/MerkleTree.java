package International_Trade_Union.entity.DtoTransaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private List<DtoTransaction> transactions;
    private String root;

    public MerkleTree(List<DtoTransaction> transactions) {
        this.transactions = transactions;
        this.root = buildMerkleRoot(transactions);
    }

    private String buildMerkleRoot(List<DtoTransaction> transactions) {
        List<String> transactionHashes = new ArrayList<>();
        for (DtoTransaction transaction : transactions) {
            transactionHashes.add(transaction.toSign());
        }
        return buildMerkleRootFromHashes(transactionHashes);
    }

    private String buildMerkleRootFromHashes(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes.get(0);
        }

        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < hashes.size(); i += 2) {
            if (i + 1 < hashes.size()) {
                newLevel.add(hash(hashes.get(i) + hashes.get(i + 1)));
            } else {
                newLevel.add(hash(hashes.get(i) + hashes.get(i)));
            }
        }
        return buildMerkleRootFromHashes(newLevel);
    }

    private String hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRoot() {
        return root;
    }
}
