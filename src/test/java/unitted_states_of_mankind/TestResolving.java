package unitted_states_of_mankind;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.utils.UtilUrl;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static International_Trade_Union.controllers.BasisController.getNodes;

public class TestResolving {
    int THRESHOLD = 1;
    //методы @SuprTrupr
    public int resolveAndAddBlocks() {
        // Set updating flag to true
        // Устанавливаем флаг обновления в true
        BasisController.setUpdating(true);
        // Get the height of the local blockchain
        // Получаем высоту локального блокчейна
        int localBlockchainHeight = BasisController.getBlockchainSize();
        // Initialize a variable to store the highest external blockchain height
        // Инициализируем переменную для хранения наибольшей высоты внешнего блокчейна
        int highestExternalHeight = 0;

        try {
            // Print the height of the local blockchain
            System.out.println("Local blockchain height is: " + localBlockchainHeight);

            // Get the list of nodes
            Set<String> nodes = getNodes();
            // Iterate through all nodes
            for (String node : nodes) {
                try {
                    // Get the height of the blockchain from the node
                    // Получаем высоту блокчейна из узла
                    int externalHeight = getBlockchainHeightFromNode(node);
                    // Update the highest external blockchain height
                    // Обновляем максимальную высоту внешнего блокчейна
                    highestExternalHeight = Math.max(highestExternalHeight, externalHeight);
                } catch (IOException | JSONException e) {
                    // Print error message if fetching blockchain height from node fails
                    System.out.println("Unable to fetch blockchain height from node: " + node);
                }
            }

            // Compare blockchain heights
            // Сравниваем высоту блокчейна
            if (highestExternalHeight > localBlockchainHeight) {
                // If the height of the external blockchain is higher than the local one
                // Если высота внешнего блокчейна выше локального

                // Download blocks from nodes with higher heights
                // Загрузка блоков из узлов большей высоты
                List<International_Trade_Union.entity.blockchain.block.Block> downloadedBlocks = downloadBlocksFromNodes(nodes);

                // Add downloaded blocks to the local blockchain
                if (isSignificantDifference(highestExternalHeight, localBlockchainHeight)) {
                    // If the difference in blockchain heights is significant

                    // Perform a tournament to verify the validity of downloaded blocks
                    if (conductTournament(downloadedBlocks)) {
                        // If blocks pass the tournament
                        // Add downloaded blocks to the local blockchain
                        addBlocksToBlockchain(downloadedBlocks);
                    } else {
                        // Print error message if the tournament fails
                        System.out.println("Downloaded blocks failed to pass the tournament, won't be added to the blockchain.");
                    }
                } else {
                    // Add downloaded blocks to the local blockchain without conducting a tournament
                    addBlocksToBlockchain(downloadedBlocks);
                }
            } else {
                // Print a message indicating that the local blockchain is up to date
                System.out.println("Local blockchain is up to date, no need to download further blocks.");
            }

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                 NoSuchProviderException | InvalidKeyException e) {
            // Print error message in case of communication errors with nodes
            System.out.println("Error communicating with nodes: " + e.getMessage());
        } finally {
            // Set updating flag to false
            BasisController.setUpdating(false);
        }

        // Update metadata of the local blockchain
        int updatedLocalHeight = BasisController.getBlockchainSize();
        // Return value based on the success of blockchain update
        if (updatedLocalHeight > localBlockchainHeight) {
            return 1; // Blockchain was updated
        } else if (updatedLocalHeight < localBlockchainHeight) {
            return -1; // Error updating blockchain
        } else {
            return 0; // Local blockchain remains unchanged
        }
    }

    // Method to get the height of the blockchain from a given node
    private int getBlockchainHeightFromNode(String node) throws IOException, JSONException {
        // Query the node to get the blockchain height
        String response = UtilUrl.readJsonFromUrl(node + "/height");
        // Parse the response into a number (blockchain height)
        return Integer.parseInt(response);
    }

    // Method to download blocks from various nodes
    private List<International_Trade_Union.entity.blockchain.block.Block> downloadBlocksFromNodes(Set<String> nodes) throws IOException {
        // Initialize a list to store downloaded blocks
        List<International_Trade_Union.entity.blockchain.block.Block> downloadedBlocks = new ArrayList<>();
        // Iterate through all nodes
        for (String node : nodes) {
            // Download blocks from the node
            // Add downloaded blocks to the downloadedBlocks list
        }
        // Return the list of downloaded blocks
        return downloadedBlocks;
    }

    // Method to check if the difference in blockchain heights is significant
    private boolean isSignificantDifference(int externalHeight, int localHeight) {
        // Difference in heights exceeds a certain threshold
        return (externalHeight - localHeight) > THRESHOLD;
    }

    // Method to conduct a tournament to verify the validity of downloaded blocks
    private boolean conductTournament(List<International_Trade_Union.entity.blockchain.block.Block> blocks) {
        // Conduct a tournament
        return true; // If blocks pass the tournament
    }

    // Method to add downloaded blocks to the local blockchain
    private void addBlocksToBlockchain(List<Block> blocks) {
        // Add downloaded blocks to the local blockchain
    }
}
