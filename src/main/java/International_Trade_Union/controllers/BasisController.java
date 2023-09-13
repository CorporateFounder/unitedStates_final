package International_Trade_Union.controllers;

import International_Trade_Union.entity.*;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.json.JSONException;

import org.springframework.http.MediaType;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.User;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.utils.UtilsBalance.calculateBalance;

@Controller
public class BasisController {

    private static double minDollarRewards = 0;
    private static Block prevBlock = null;
    private static Account minerShow = null;
    private static int difficultExpected = 0;
    private static boolean mining = false;
    private static boolean updating = false;
    private static DataShortBlockchainInformation shortDataBlockchain = null;
    private static Blockchain tempBlockchain;
    private static Blockchain blockchain;
    private static int blockchainSize = 0;
    private static boolean blockchainValid = false;
    private static Set<String> excludedAddresses = new HashSet<>();


    public static int getDifficultExpected() {
        return difficultExpected;
    }

    public static void setDifficultExpected(int difficultExpected) {
        BasisController.difficultExpected = difficultExpected;
    }

    public static Account getMinerShow() {
        return minerShow;
    }

    public static double getMinDollarRewards() {
        return minDollarRewards;
    }

    public static void setMinDollarRewards(double minDollarRewards) {
        BasisController.minDollarRewards = minDollarRewards;
    }

    public static void setMinerShow(Account minerShow) {
        BasisController.minerShow = minerShow;
    }

    public static int getBlockchainSize() {
        return blockchainSize;
    }

    /**
     * informs if mining is happening now. информирует, происходит ли сейчас майнинг.
     */
    public static boolean isMining() {
        return mining;
    }

    /**
     * Informs whether the files are currently being updated. информирует, происходит ли сейчас обновление файлов.
     */
    public static boolean isUpdating() {
        return updating;
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null)
            return null;
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }

    public static Set<String> getExcludedAddresses() {

        HttpServletRequest request = getCurrentRequest();
        if (request == null)
            return excludedAddresses;

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();  // includes leading forward slash

        String localaddress = scheme + "://" + serverName + ":" + serverPort;

        excludedAddresses.add(localaddress);
        return excludedAddresses;
    }

    public static void setExcludedAddresses(Set<String> excludedAddresses) {
        BasisController.excludedAddresses = excludedAddresses;
    }

    private static Set<String> nodes = new HashSet<>();
//    private static Nodes nodes = new Nodes();

    public static void setNodes(Set<String> nodes) {
        BasisController.nodes = nodes;
    }

    /**
     * returns a list of hosts to which you can connect automatically.
     * возвращает список хостов которому можно подключиться автоматически.
     */
    public static Set<String> getNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        nodes = new HashSet<>();

        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);


        nodes.addAll(temporary);


        nodes = nodes.stream()
                .filter(t -> !t.isBlank())
                .filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        nodes = nodes.stream().map(t -> t.replaceAll("\"", "")).collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        //removes blocked hosts. удаляет заблокированные хосты
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);

        //adds preset hosts. добавляет предустановные хосты
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        return nodes;
    }

    /**
     * similar to getNodes. аналогичен getNodes
     */
    @GetMapping("/getNodes")
    public Set<String> getAllNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.addAll(temporary);
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        nodes = nodes.stream().filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);
        return nodes;
    }

    /**
     * Возвращает действующий блокчейн. Returns a valid blockchain
     */
    public static Blockchain getBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain1  = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain1;
    }

//    public static synchronized void setBlockchain(Blockchain blockchain) {
//        BasisController.blockchain = blockchain;
//    }
    public static Map<String, Integer> cheaters = new HashMap<>();
    static {
        try {
            //creates all resource folders to work with
            //создает все папки ресурсов для работы
            UtilsCreatedDirectory.createPackages();

            //downloads from a blockchain file
            //загрузки из файла блокчейна
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);

            //a shorthand object that stores information about the blockchain
            //сокращенный объект, который хранит информацию о блокчейне
            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();
            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);
            String json = UtilsJson.objToStringJson(shortDataBlockchain);
            UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
            if(cheaters.isEmpty()){
                cheaters.put("23gGdRnzpGCzeoFhRbTh6qN4iUHx4yN7XHtiyu9dLNHp5", 20);
                cheaters.put("yds19rd1F1DaUw2raZjEtVcXJS1w52PDNWCnqmJU2Vm2", 460);
                cheaters.put("24ZK18ixYsLVFtRkofVaoAPdbwR8KsavRtGLNHDiMyzy3", 38);
                cheaters.put("br5AWJ1kYALi8aj6riQmadbVB3N5EQEww4i9NU6fa8rW", 460);
                cheaters.put("tjghGks15LdppYYvZKwb79w6wU2NwgpEeq5Rktj7smHH", 48);
                cheaters.put("tsUbUnrNhKbRD5aXaTRUVwmqX1nE6QwyFrvj9E9fvC9i", 12);
                cheaters.put("xR8M7uGWbXqJZ91agqsHYPGqtgxDdGyMtcN3ub9HrRP4", 270);
                cheaters.put("ss8xWxs5h6PxcGBLsbqxYYZpMFow9xwPq8WXZcCp9T8W", 840);
                cheaters.put("wdQkJH2ojoAwiuBszkj4M2PF3gtwP32VcdRvivoACbcq", 10);
                cheaters.put("2BgqL9dBcZzxRtFToidJe8GoThMbEzwRB2mFCUeLQjY5E", 132);
                cheaters.put("rkYZHogbkJy9iFXPxuojBRGX4ULQmNi2h96dGRxXhjzB", 336);
                cheaters.put("26GKW4gTxZ9CfFmeUYa4tf72fCHKVX1Ra1vaV5yhEzPk8", 28);
                cheaters.put("2789oTQwPH1VELR3rcPiURptx17RG1wmx5taZ71URKEK1", 402);
                cheaters.put("xUaV5cMdTwMFvtU5mFAiGjgoV6AKXA5izD6fXqQeAnop", 262);
                cheaters.put("vEoTZj1WDNSZdK7mtJ9g98Yp39zEKFNbnAmeMkkALvQ1", 28);
                cheaters.put("oGooDwbBWz1GoQ4xHkquPhVxJWFgs5AY8Z5BRiKaZVGQ", 56);
                cheaters.put("hNSz6M75uLKMCeW17AR4sDcoPZjXJxNbVZuFjnZpgS61", 6);
                cheaters.put("fbCBE5Et4tmtGPYB74AumnhmTKVtssrWF8d6gfcgssZD", 4);
                cheaters.put("ruo7jAT29PJVZR3W5wjBgbJpRnoAfbtAJ2BJnP4UqygR", 130);
                cheaters.put("eEQK24hv8DKkJNter1PgZLJJhWEfSXigXHLYNWwrm2eY", 124);
                cheaters.put("28yWK3qM546xyQYrG2Kcyqzz37FBPYJKiybLxWDKo5Ppz", 26);
                cheaters.put("wDzvvr76G6c5vojqMVqZExCaa9GnrSCNVRQMY9RzzKDm", 42);
                cheaters.put("277sR4HdHx6XijaBu48y2cBNKhBGjaaSt73oBQ2RHLyCp", 46);
                cheaters.put("iVitpjskNQykCH5R6G1Vy9mvJaWDzEz2akGXU1M1kPmL", 12);
                cheaters.put("2BkmG61VpEpNLnkHihjw4X72q971vA4w5pHYEcb7ef8W5", 150);
                cheaters.put("n4Y74AdPQnjrKFzkWTSkN5x57wKZpwfxZsccgBu6dgXC", 160);
                cheaters.put("ifN9wScZr4CuWVELQo1J524xUv78X8dUrCnqkMDUHKGx", 22);
                cheaters.put("24uzecEAGzrS8EcNZHEVkJD3R42pBn8927fdXqFvJQfRR", 32);
                cheaters.put("283yA6LzTYNZLNYF6ktcJWKeR1zogu6oZSKDNqU6FJDMB", 242);
                cheaters.put("yYjcKwDptRFMDNPZuXZKd99G2tCuryz1GuTh4AzK4JjB", 8);
                cheaters.put("28Rg1NQpSPv5mwhowW3q71CtALUr9nP1TdeVcWGvWsZxC", 212);
                cheaters.put("orx1fcthjcAAaMxjuvaGEVPDMFY2Tpg9pYRnkBMQMngC", 342);
                cheaters.put("296U3K2cCexBm94VLeqF2d81qHU76HFjBbf99MChLwtSj", 22);
                cheaters.put("unETHwaQSM9PmC61d79hk1wNGQoRAADMXp2caUmxCYDM", 24);
                cheaters.put("togZkNxViksGGwHQkeb6Wfftc65oJvmniFsCgcLM3JMR", 98);
                cheaters.put("pzEyaZZqY8N3auYsQL8bQ5Nxw2bjxyAGXKHvvv4BUG7h", 40);
                cheaters.put("q7ZKrcchAXgSVicHRi8MbY8vXtJ5jFgMz6GjsFKCPGE2", 172);
                cheaters.put("v3cyYpMcAQyYYNxnghUsKjxqDKQW9JWssSfB5YgnDRkZ", 258);
                cheaters.put("rustMYAYYhZqQbGcDFAjLvFGF8PpQUVxTbJ82y9JosHy", 364);
                cheaters.put("224ybxXE5yteZxNDjs39ndpXVWhPbkNoqqtaB3EU1TvA1", 4);
                cheaters.put("jZ2L4dKFh15cftZdGhyz4aN2RC8wS9oGzMuTZBEezA8c", 22);
                cheaters.put("teEMAYvdxQKVejutYwhzC4rbiFBWrb6ML1CCa4EscYkW", 684);
                cheaters.put("25zNyykmQjnj4H7meMzGQvwyPNeLsFeef9Pjw1ozvpq5P", 120);
                cheaters.put("pMXXqFVXM6PYQfwAnxL788bkToecn7GQcHBuXawhtUC3", 702);
                cheaters.put("e9yxWtbbCiv6FpSNDqfaZmQYn1C7fsWDmzVyWdhwGbRa", 8);
                cheaters.put("nBE3Zf1KjVkvA9bnRFjuKtKqZsU9gxah9KbeuUdrGzNm", 24);
                cheaters.put("cwUQne7HqKb9z5imxQsWG8QJMf32DEtXRxpoXeFbCwD5", 166);
                cheaters.put("kBEqKkTzygDdErhzcZLmAMcJMuUogfVkBbye7sVnTH6r", 124);
                cheaters.put("v8BEUMZk64k5J9HcfPeJJMCDRXJRv2HKwDKQm6YksMxx", 288);
                cheaters.put("zJmfZzJ8xpHcdq4QtLHD2rf42gz9Vm2Wat4a6R6FsPso", 214);
                cheaters.put("ysMDsZe9EsfnfSfLhtGiizqfZGf1dkWE1X8v15VwBXsQ", 252);
                cheaters.put("quMX9Vq5xU4kNA9U8H9gA4ALXq4JAvLkaMdFT2NpB3fW", 18);
                cheaters.put("hqhbCcimB8hUcn2Gkzi7Ak9G8MPXLdvY1rr9qXKzbAga", 32);
                cheaters.put("osgahwsZCW3PkYpMqSxnLBR5KN6NgQLbxvAuGeC7RxRk", 652);
                cheaters.put("e9BHsZzg5JMf1LsxtR1fkyMXZ3RDWKw6nj99HSQi8tWv", 8);
                cheaters.put("292YK2bMfneupTvSRJ46APfmw53jF5P21eQgXt7D98x1v", 672);
                cheaters.put("z1KzWiZjFnqSQTwHph6jV4KydaXmGQt6SeCNSK8aKRUB", 8);
                cheaters.put("mtYyHFA2hw9Tv9JvGwwitGwTq42d65DP7iCegoP8YuEQ", 34);
                cheaters.put("oAcadYwHTgBUtNpStjfahjRwYYVLu9hukyabwxjT32og", 376);
                cheaters.put("oFWXNKMEug8EjND88S8t1yCxn64PbWUu3Y6iagwqFqMB", 180);
                cheaters.put("28RWx6G6A4NoB7CwC6kKnhJ5epUvYEzTpcuN5Emmeujzj", 110);
                cheaters.put("oMqgPS4HZwd7oQVQjsfqUVR1Vkrmw9oUiiJZRizsvvWF", 16);
                cheaters.put("k2KBBp9B5ve7C42fdBnAMF1LxxQkiacEtk36Z71QWD1N", 10);
                cheaters.put("beACedNewaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC", 398);
                cheaters.put("215RUpCcGTk9RChPrjKbM8KbsXLPoknLwxWzW5CfAxJPB", 516);
                cheaters.put("jzGTKkkvJQRLo1dvJPdAyqCL9Ro3mNNipKmUYBNuCkpr", 506);
                cheaters.put("24diH6Kq61KaYCXRu5uiDGrS8izbqeJaDwVCdaL2oh6Ua", 280);
                cheaters.put("wFu9mvZqKMKWhaMNRZoczpEXZzoaDYQjBnp5iaM6Pz39", 228);
                cheaters.put("fmdBFQzg2Ur4MFey8GyRSNBdFy6EJTbcwqgFcnUVgvQK", 152);
                cheaters.put("jwsCieirRQGttwfoDUuFNfK7dwycPXe9jrbV31k3rCpH", 300);
                cheaters.put("whFXwPDBEH1wGCdjxqsz3qBHHkLHcsKoAw5PRN5dKdBX", 212);
                cheaters.put("mvNUJKFMoWR5sTPge3ULS9tackuMGQKS7wsh2XcVa2Rt", 34);
                cheaters.put("2518F6Ywb1Rr4p7UgxbdT8Mv83F7CLFhnxFMuCPKpw5Yh", 690);
                cheaters.put("yfWzeXyM63iaf5dMmFQ5sNnZWrhavQhKSWTG8Fwm6tJE", 332);
                cheaters.put("2AE3iXvSt5S9GxcRK9JK6sdt2WaWgAGojRq8wYvKHnPgQ", 136);
                cheaters.put("zuturEey12vXvT32P1MMZxD7idAzakRZnohCVy6F34Hn", 26);
                cheaters.put("2BKfoo2qfzBT27rXRhb9oackZEtcQdRr4P5cUrGKVCmfj", 262);
                cheaters.put("kYCZTyigxAQhVMpqBfat63rDfTbHQ3bpMNqjJyXUxE75", 404);
                cheaters.put("w3X85ceDfyEq1SEFFDQBmbdBJP8f2girpW6D1eMzRYLz", 34);
                cheaters.put("snNSobvq1STcYof8hzkqGF7FfeEQ3gruv2eN9hbpyMXJ", 30);
                cheaters.put("gPqT5QAv4sYHHcMRcUKAz9eaceHx32ePRZeMeEQkwVQh", 2);
                cheaters.put("maPQo4KRv3bixgVp5PRNRGu35KnPJrhRoeu7zw8mRhTX", 4);
                cheaters.put("cRur6s9pZqXJjMtdL1MBKttKWuuWC9246itspnHrDYAQ", 22);
                cheaters.put("272MnDK5ZCZA4E64x3w5xFxWBLZkeBvaDFy1eeYTVv5ni", 420);
                cheaters.put("27iftF24CpdZtfT5SoouJ9ViKRWsZ6WUj9mx7pupQhLwz", 4);
                cheaters.put("yR8ZEK9xcFkxTpT5V7QJY3zsmP3DfC1TcWmG26v36E7T", 22);
                cheaters.put("pAyuDDZ7sXUQXojWaDUfSRRFn65PvodFoyd2oQdoco4J", 170);
                cheaters.put("23fxY3QspMdpiqSGCM7xP1HmRowbeUKWEuqXNNDqPBhp9", 52);
                cheaters.put("jusku5PfFtdrUL2PrwDWWRj845hWaWepw4Zeba1Y6Up6", 6);
                cheaters.put("vtNLydTEDqbTmFq8Ew28gBu3tXM294KuB2aaDRSVDwBv", 66);
                cheaters.put("gye7VmCjzfinYd6eqN2UH1HhkwxcCt4CX9bkgWUraLjy", 590);
                cheaters.put("ugpmBdEsXjvFR3dYT2q2irv15iKiTNKSKUbYdTgPfsz1", 26);
                cheaters.put("24d7Ay3mNLETD4QcvRRmeLUspkgvxXqp7iDamcE5DLJaL", 6);
                cheaters.put("dGNNYTqKUq2ur87axvmhoAYNhZsefNRD2uSfDqvru5qA", 36);
                cheaters.put("s3mKzCgdxdk62F6q5LyWx5N7dEJgHn6STLpPqBv2dcsy", 130);
                cheaters.put("2AREzDBWHnA4L5YJXBDURGqdA1ZTY5qtsVZw7g1bdiLim", 30);
                cheaters.put("gX6bLhsMEkwd4cjEtLmiDWddVCzkENGGaUNaSNngiYBx", 108);
                cheaters.put("zKWF4WxMY5uVbPmuV5E8SYo331FCWWKdjQucEZuHjU4L", 48);
                cheaters.put("tWjvEda9ux64h4M4bggRYax18k955V8fefLd7WkYukcu", 26);
                cheaters.put("f89RukbCknDEbAcWTJ4kdyU4wr3hG2QgggWwAHehP8mu", 70);
                cheaters.put("23mHgUJwzn5ufRrmwcjJ9pChMHoMCF6iXTKpHuBpUmK6B", 242);
                cheaters.put("wdAiMNvQHjGxTR2SoPC1f5CtMEBkAKqtr3g9zhoNwJcb", 40);
                cheaters.put("g3PW13FnuqwujejCs2KsRnEZi26v2focEj26tXEN121t", 56);
                cheaters.put("dY73Ujg6SdUfTU2uNL3M4xp9YPS49mfKFf8UY1pNhhNP", 32);
                cheaters.put("p3mDRXgDNxJ16NcBoFqFGQHSQwaBMhDe1zNGP4e5mJaT", 40);
                cheaters.put("2ASpf7w2FNo62yFPk9m4dzZkdu91VUukdcJ1MBodGsS99", 74);
                cheaters.put("zwsc3EXk547XRjeke5DadHEs95RpsjbVEd9Kt9rkDnLv", 4);
                cheaters.put("beACedaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC", 2);
                cheaters.put("28xPLz7Ewk5vZP8EUrBzZr6yoGPXrWnKTJ4jpDNVj3TwE", 40);
                cheaters.put("25eQtccfAhnov3NZ8vZKYSS9KGbVN2WKUYsj46s6HvKz1", 26);
                cheaters.put("iyeHYJnsFV8ZnuC4yyVmDiFe354T2GZHBVUqA64XMtZU", 148);
                cheaters.put("23UdcdQosh8NdFPX6MJ5n4Qxtbu4pmkstWgRjnJMhxXvR", 32);
                cheaters.put("dcnpsAvwT2NVfkS4dtBdd6NpbY4bQ5PABc3kiPsXavsj", 126);
                cheaters.put("mNe6iUtdQSARbprgWn7SGXdAhuDDdKeKniB5LYWPzWa3", 610);
                cheaters.put("25eHdsX3t4NgA1MsDSJCcpCENvSjiw2qw9yCKupcWTgfP", 28);
                cheaters.put("sLDTrgToh4tXKUk1TcVtThZFgMvZsFdi1YRhJCJcJwzP", 6);
                cheaters.put("cDR6QfaAgScEi7NZLHxGKiBEC5v8cgdqGUcXPdQexS21", 146);
                cheaters.put("bh427oRfPaaihsemmqt3DD7AfyugKZxjz4RWYs8LurbE", 222);
                cheaters.put("h1H1E38aWVhycSBKn2SjL3hRwPg2xZf6H1diKu1m4Mwn", 424);
                cheaters.put("df5yzLBqmqBK6Ax9t7W93NLUu138s38rYN7RpsRxRTCN", 24);
                cheaters.put("23SwqJRMvj6eZX1XbXCAGPJBcyP6xQqTKdfMkjF4dQncg", 304);
                cheaters.put("2A6kQQ2uAN8zt5B5pfjXydz6P5o6yuyDv5xFyv9fZiFZQ", 230);
                cheaters.put("2A8S3QkzMqg2FktSMWjEHR6uSUk7am9zac1hfS2UhcjeA", 238);
                cheaters.put("xrmVPZmpZ3Ttctip9CiwiwmPdb8bBMJoZo9UerCBPjoq", 260);
                cheaters.put("25T7yijyjktRAS2czwWEJvURxrmhrBBjGRST51DPVmQai", 248);
                cheaters.put("279w1BHeizFj94sbxLi8pBqGrV4kvao3GSBPBJTkumygo", 142);
                cheaters.put("ux2xJHME9P7RdZ9NRbCSuFKEcir55feLmSfZFWSSAPxS", 24);
                cheaters.put("23zGb1bpho2hqpMCYWKVUZSQ8nvvGDgcG1jrWYakPFnDG", 94);
                cheaters.put("csAF2XdC1EE3mCnSpAiGx1Swe2nvgaiSevF1tY31H4V5", 34);
                cheaters.put("24GHqPQpdqLwoYKdwWpZcaVx5wRMhmt5qJvdT495VdwXh", 82);
                cheaters.put("kHiKPVZij6icNs6jELjty2aJXQ4Sw5TEkae7AhjkUZJG", 6);
                cheaters.put("26vVvoDk5rEMscD2MpQHt3FhpsWnFJcd2AKRDNsrgcbH9", 632);
                cheaters.put("wBX6762JpbJWHDq7zSSmnyH4EguR4WHPSqZaJiXXVK4T", 8);
                cheaters.put("2BR6WDEMyt9awJXfo6wsH6JuVykh7iyY4CdxueDBAi1z3", 16);
                cheaters.put("p91SrgDws5n8GSf4rKCn2gZk27b4HBi48LohLspV3mmL", 464);
                cheaters.put("jtw7SKU3HRju5A2EFBX96Q2FdG18GXfrwKmGusY7Zqyb", 100);
                cheaters.put("hVmU5GkJ3pu1vSWzA1vAatYrt4BYY7B8P8HqzsMbmJGM", 70);
                cheaters.put("riMPjBFzmFpmvo7vnQQzCkhBzRM6ubY32TJLCnsyhwS8", 46);
                cheaters.put("hjBFyuBcqi2s6QwSLqWficZEmSeaY6jpyyfKeWzd2Vyk", 254);
                cheaters.put("26nec5NMvVWttPpnHFzrYyt9Z1i74pxfJmx4DFYeYwK73", 390);
                cheaters.put("hpt8iZNx1haf3jcGVdrCTj59MSQdgUbsHBq8qY9NHuBC", 16);
                cheaters.put("qLM45ntQypxRHUepugdN6hbbceYohy8R3FueRXDb7Fns", 56);
                cheaters.put("26BMHMizDLwSKLCGwPqM8TFqb8PW74BiUmKtmdo1GacLn", 242);
                cheaters.put("twvJvVkkm6dAuqSEs4pHLnUWCYSn8YoxFvRu5q4qo1n4", 80);
                cheaters.put("of7r6Eoqp86CvUFyDMa9pm36mMtYCVUTz47A2jPU15Kk", 166);
                cheaters.put("23sq3TT5EvGGUMFZh6k4STAC3hrRnPirpNmQ7zcYRoJ7m", 556);
                cheaters.put("2A9HWooA3Y9EEnBhGLz1Bbo8kRupR5kJ6xjjab5ZjcTmk", 142);
                cheaters.put("268wySmYmMdDhA6FBUu7XupVMMTeGzcZ7JsMVUuDiikys", 24);
                cheaters.put("xWe6F7kriDSprcovdt6muDypDeWih4EJ5q179Z3caGXb", 22);
                cheaters.put("deXgm6J9LNgeD8N8axfQAL8rGrs3vWFAYnfoNhW5GCDf", 480);
                cheaters.put("pFno2wqqEAzmAj2tQ4ow4Af3BjSyijyS4JbTb9ESfZiS", 1648);
                cheaters.put("fL3AaTUMvUpLEhXZKHLwHSzpqAAmbHmvN6EXtothEiie", 224);
                cheaters.put("wPr3hTFJL6dXJpD3srcx69wCekCncc5GLvko4oBfrEVU", 126);
                cheaters.put("dQr71RUczgJ7SH6Uepyy7hpdAP2JAPfz7DvzYLnsvefD", 52);
                cheaters.put("26x5aDK78k8TTdkdPPjWzf6o1jkwbNT9ZB5FzZ5jYGYR7", 12);
                cheaters.put("sSVE6PhRBrLJSjiBCokhhrUbaQKqsWbR2ipb2jem5viK", 34);
                cheaters.put("27HDzRat6kFZw1PTdGnG3KVdPcRqNRVYobaNYntmj4MqQ", 50);
                cheaters.put("ezktCpiUw1DFLneDm1mbvwzkQDNcdtuxj9f15MKytntS", 36);
                cheaters.put("2733E2eSNwPL4sccL42q43CZ995AThvbx4ZENFdhBY7uJ", 12);
                cheaters.put("25TjMCZzaQsRoGkJ61Nb8JFTVPiWN4GhSPrKFA3Y4g3WN", 184);
                cheaters.put("eocwNsJ6i5pyBCKjyva4eXUStS7i7QZJBtqDtNZtwVrC", 36);
                cheaters.put("2336trATzYoMsmFB6qbTPHwUFLsk1mZSxXysbjywxjcKF", 24);
                cheaters.put("hzhq1LUk3qCcNyrTGE5pSRrRsYf3HkdSmeu5jap1JUnx", 242);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }



    public BasisController() {
    }

    //TODO если вы прервали mine, то перед следующим вызовом перезапустите сервер и вызовите /addBlock перед mine
    //TODO if you interrupted mine, restart the server before next call and call /addBlock before mine
    //TODO иначе будет расождение в файле балансов
    //TODO otherwise there will be a discrepancy in the balance file

    /**
     * Returns an EntityChain which stores the size of the blockchain and the list of blocks
     * Возвращает EntityChain который, хранит в себе размер блокчейна и список блоков
     */
    @GetMapping("/chain")
    @ResponseBody
    public EntityChain full_chain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return new EntityChain(blockchain1.sizeBlockhain(), blockchain1.getBlockchainList());
    }

    /**
     * returns the size of the local blockchain
     * возвращает размер локального блокчейна
     */
    @GetMapping("/size")
    @ResponseBody
    public Integer sizeBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();


        System.out.println(":sizeBlockchain: " + shortDataBlockchain);

        return blockchainSize;
    }

    /**
     * Returns a list of blocks from to to,
     * Возвращает список блоков ОТ до ДО,
     */
    @PostMapping("/sub-blocks")
    @ResponseBody
    public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();

        return Blockchain.subFromFile(entity.getStart(), entity.getFinish(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * Returns a block by index
     * Возвращает блок по индексу
     */
    @PostMapping("/block")
    @ResponseBody
    public Block getBlock(@RequestBody Integer index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();

//        return blockchain.getBlock(index);
        return Blockchain.indexFromFile(index, Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * connects to the storage and updates its internal blockchain.
     * подключается к хранилищу и обновляет свой внутренний блокчейн
     */
    @GetMapping("/nodes/resolve")
    public static synchronized int resolve_conflicts() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        System.out.println(":resolve_conflicts");

        int result = resovle2();
        System.out.println("resovle2: " + result);
        if (result != 0) {
            result = resolve();
            System.out.println("resovle: " + result);
        }
        System.out.println("resolve" + result);

        return result;
    }


    //данный метод более оптимизированный так как мы скачиваем только с доверенного хоста и не нужна дополнительная проверка
    public static int resovle2() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        updating = true;
        int bigSize = 0;

        try {
            System.out.println(" :start resolve");

            utilsMethod();

            //size of the most recent long blockchain downloaded from hosts (storage)
            //размер самого актуального длинного блокчейна, скачанного из хостов (хранилище)


            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = blockchainSize;


            EntityChain entityChain = null;
            System.out.println(" :resolve_conflicts: blocks_current_size: " + blocks_current_size);



            Set<String> nodesAll = getNodes();

            System.out.println(":BasisController: resolve_conflicts: size nodes: " + getNodes().size());

            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна
            for (String s : nodesAll) {
                //if the local address matches the host address, it skips
                //если локальный адрес совпадает с адресом хоста, он пропускает
                if (BasisController.getExcludedAddresses().contains(s)) {
                    System.out.println(":its your address or excluded address: " + s);
                    continue;
                }
                try {
                    //if the address is localhost, it skips
                    //если адрес локального хоста, он пропускает
                    if (s.contains("localhost") || s.contains("127.0.0.1"))
                        continue;



                    String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                    Integer size = Integer.valueOf(sizeStr);

                    if(size > bigSize){
                        bigSize = size;
                    }
//

                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    System.out.println("resolve2 size: " + size + " blocks_current_size: " + blocks_current_size);
                    if (size > blocks_current_size) {

                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm

                        List<Block> lastDiff = new ArrayList<>();
                        if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                            lastDiff = Blockchain.subFromFile(blockchainSize - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                    blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                        }
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;
                        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD, then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD, то нужно скачивать порциями из хранилища
                        if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                            int start = blocks_current_size;
                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продожаться скачивать порциями, чтобы скачать весь блокчейн
                            while (downloadPortion) {

                                subBlockchainEntity = new SubBlockchainEntity(start, finish);

                                System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
                                subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                System.out.println("1:sublockchainJson: " + subBlockchainJson);
                                List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                System.out.println("1:download sub block: " + subBlocks.size());
                                finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD;
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1;


                                balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

                                if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                    lastDiff = Blockchain.subFromFile(blockchainSize - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                            blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                }
                                DataShortBlockchainInformation temp = new DataShortBlockchainInformation();

                                if(blockchainSize > 1){
                                    temp =  Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff);
                                }


                                System.out.println("temp: " + temp);
                                System.out.println("blockchainsize: " + blockchainSize);
                                System.out.println("sublocks: " + subBlocks.size());

                                if (blockchainSize > 1 && !temp.isValidation()) {
                                    return -10;
                                }
                                addBlock3(subBlocks, balances);
                                if(!temp.isValidation()){
                                    temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                }
                                shortDataBlockchain = temp;
                                blockchainSize = (int) shortDataBlockchain.getSize();
                                blockchainValid = shortDataBlockchain.isValidation();
                                prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

                                String json = UtilsJson.objToStringJson(shortDataBlockchain);
                                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                                if (size - prevBlock.getIndex() < Seting.PORTION_DOWNLOAD) {
                                    downloadPortion = false;
                                    finish = size;
                                    subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                    System.out.println("2:sublockchainEntity: " + subBlockchainEntity);
                                    subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                    System.out.println("2:sublockchainJson: " + subBlockchainJson);
                                    subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                    System.out.println("2:download sub block: " + subBlocks.size());

                                    balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                    if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                        lastDiff = Blockchain.subFromFile(blockchainSize - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    }

                                    if(blockchainSize > 1){
                                        temp =  Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff);
                                    }


                                    System.out.println("temp: " + temp);
                                    System.out.println("blockchainsize: " + blockchainSize);
                                    System.out.println("sublocks: " + subBlocks.size());

                                    if (blockchainSize > 1 && !temp.isValidation()) {
                                        return -10;
                                    }
                                    addBlock3(subBlocks, balances);
                                    if(!temp.isValidation()){
                                        temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    }
                                    shortDataBlockchain = temp;
                                    blockchainSize = (int) shortDataBlockchain.getSize();
                                    blockchainValid = shortDataBlockchain.isValidation();
                                    prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

                                    json = UtilsJson.objToStringJson(shortDataBlockchain);
                                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                                }
                            }
                        } else {
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);

                            System.out.println("3:sublockchainEntity: " + subBlockchainEntity);
                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);


                            System.out.println("3:sublockchainJson: " + subBlockchainJson);
                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            System.out.println("3:download sub block: " + subBlocks.size());
                            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                            if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                lastDiff = Blockchain.subFromFile(blockchainSize - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                        blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            }

                            DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
                            if(blockchainSize > 1){
                                temp = Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff);
                            }

                            System.out.println("temp: " + temp);
                            System.out.println("blockchainsize: " + blockchainSize);
                            System.out.println("sublocks: " + subBlocks.size());

                            if (temp.getSize() > 1 && !temp.isValidation()) {
                                return -10;
                            }
                            addBlock3(subBlocks, balances);
                            if(!temp.isValidation()){
                                temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            }
                            shortDataBlockchain = temp;
                            blockchainSize = (int) shortDataBlockchain.getSize();
                            blockchainValid = shortDataBlockchain.isValidation();
                            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

                            String json = UtilsJson.objToStringJson(shortDataBlockchain);
                            UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                        }

                        //if the local blockchain was originally greater than 0, then add part of the missing list of blocks to the list.
                        //если локальный блокчейн изначально был больше 0, то добавить в список часть недостающего списка блоков.


                        System.out.println("size temporaryBlockchain: ");
                        System.out.println("resolve: temporaryBlockchain: ");


                    } else {
                        System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                        continue;
                    }
                } catch (IOException e) {

//                    e.printStackTrace();
                    System.out.println("BasisController: resove2: " + e.getMessage());
                    continue;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }


            }


        } finally {
            updating = false;
            if (blockchainSize > bigSize) {
                return 1;
            } else if (blockchainSize < bigSize) {
                return -1;
            } else {
                return 0;
            }

        }


    }

    //TODO need to improve the code, at the moment even if you download one block,
    //TODO have to recalculate the entire blockchain again to get the current balance
    //TODO нужно улучшить код, на данный момент даже если вы скачиваете один блок,
    //TODO приходиться пересчитывать весь блокчейн заново, чтобы получить актуальный баланс

    /**
     * Updates the blockchain. Connects to the storage host and downloads if the host has a more up-to-date blockchain.
     * Обновляет блокчейн. Подключается к хосту хранилища и скачивает, если в на хосте более актуальный блокчейн.
     */


    public static int resolve() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        updating = true;
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        boolean isPortion = false;
        boolean isBigPortion = false;

        try {
            System.out.println(" :start resolve");
            Blockchain temporaryBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            Blockchain bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            utilsMethod();

            //size of the most recent long blockchain downloaded from hosts (storage)
            //размер самого актуального длинного блокчейна, скачанного из хостов (хранилище)
            int bigSize = 0;

            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = blockchainSize;

            //the sum of the complexity (all zeros) of the temporary blockchain, needed to select the most complex blockchain
            //сумма сложности (всех нулей) временного блокчейна, нужна чтобы отобрать самый сложный блокчейн
            long hashCountZeroTemporary = 0;

            //the sum of the complexity (all zeros) of the longest downloaded blockchain is needed to select the most complex blockchain
            //сумма сложности (всех нулей) самого длинного блокчейна из скачанных, нужна чтобы отобрать самый сложный блокчейн
            long hashCountZeroBigBlockchain = 0;

            EntityChain entityChain = null;
            System.out.println(" :resolve_conflicts: blocks_current_size: " + blocks_current_size);

            //the sum of the complexity (all zeros) of the local blockchain
            //сумма сложности (всех нулей) локального блокчейна
            long hashCountZeroAll = 0;

            //get the total complexity of the local blockchain
            //получить общую сложность локального блокчейна
            hashCountZeroAll = shortDataBlockchain.getHashCount();

            Set<String> nodesAll = getNodes();

            System.out.println(":BasisController: resolve_conflicts: size nodes: " + getNodes().size());

            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна
            for (String s : nodesAll) {
                //if the local address matches the host address, it skips
                //если локальный адрес совпадает с адресом хоста, он пропускает
                if (BasisController.getExcludedAddresses().contains(s)) {
                    System.out.println(":its your address or excluded address: " + s);
                    continue;
                }
                try {
                    //if the address is localhost, it skips
                    //если адрес локального хоста, он пропускает
                    if (s.contains("localhost") || s.contains("127.0.0.1"))
                        continue;

                    System.out.println("start:BasisController:resolve conflicts: address: " + s + "/size");

                    String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                    Integer size = Integer.valueOf(sizeStr);
//                    MainController.setGlobalSize(size);
                    System.out.println(" :resolve_conflicts: finish /size: " + size);
                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    if (size > blocks_current_size) {

                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm
                        List<Block> emptyList = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;

                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD, then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD, то нужно скачивать порциями из хранилища
                        if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                            int start = blocks_current_size;
                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продожаться скачивать порциями, чтобы скачать весь блокчейн
                            while (downloadPortion) {

                                subBlockchainEntity = new SubBlockchainEntity(start, finish);

                                System.out.println("downloadPortion: " + subBlockchainEntity.getStart() +
                                        ": " + subBlockchainEntity.getFinish());
                                subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                                List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD;
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1;

                                emptyList.addAll(subBlocks);
                                System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                        + subBlocks.get(subBlocks.size() - 1).getIndex());

                                if (size - emptyList.get(emptyList.size() - 1).getIndex() < Seting.PORTION_DOWNLOAD) {
                                    downloadPortion = false;
                                    finish = size;
                                    subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                    subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                    subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                    System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                            + subBlocks.get(subBlocks.size() - 1).getIndex());
                                    emptyList.addAll(subBlocks);
                                }
                            }
                        } else {
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);
                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                            System.out.println(":download sub block: " + subBlockchainJson);

                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            emptyList.addAll(subBlocks);

                            System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                    + subBlocks.get(subBlocks.size() - 1).getIndex());
                            System.out.println("blocks_current_size: " + blocks_current_size);
                            System.out.println("sub: " + subBlocks.get(0).getIndex() + ":" + subBlocks.get(0).getHashBlock() + ":"
                                    + "prevHash: " + subBlocks.get(0).getPreviousHash());
                        }

                        //if the local blockchain was originally greater than 0, then add part of the missing list of blocks to the list.
                        //если локальный блокчейн изначально был больше 0, то добавить в список часть недостающего списка блоков.
                        if (blocks_current_size > 0) {
                            System.out.println("sub: from 0 " + ":" + blocks_current_size);
                            List<Block> temp = blockchain1.subBlock(0, blocks_current_size);

                            emptyList.addAll(temp);
                        }

                        emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                        temporaryBlockchain.setBlockchainList(emptyList);


                        System.out.println("size temporaryBlockchain: " + temporaryBlockchain.sizeBlockhain());
                        System.out.println("resolve: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());

                        //if the global blockchain is larger but there is a branching in the blockchain, for example, the global size is 25,
                        // the local size is 20,
                        //but from block 15 they differ, then you need to remove all blocks from the local block from block 15
                        // and add 15-25 blocks from the global blockchain there
                        //если глобальный блокчейн больше но есть развлетление в блокчейне, к примеру глобальный размер 25,
                        // локальный 20,
                        //но с 15 блока они отличаются, то нужно удалить из локального с
                        // 15 все блоки и добавить туда 15-25 с глобального блокчейна

                        if (temporaryBlockchain.validatedBlockchain() && blockchainSize > 1) {
                            isPortion = true;
                        } else {
                            isPortion = false;
                        }
                        if (!temporaryBlockchain.validatedBlockchain()) {
                            System.out.println(":download blocks");
                            emptyList = new ArrayList<>();

                            for (int i = size - 1; i > 0; i--) {

                                Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));

                                System.out.println("block index: " + block.getIndex());
                                if (i > blocks_current_size - 1) {
                                    System.out.println(":download blocks: " + block.getIndex() +
                                            " your block : " + (blocks_current_size) + ":wating need downoad blocks: " + (block.getIndex() - blocks_current_size));
                                    emptyList.add(block);
                                } else if (!blockchain1.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                    emptyList.add(block);
                                    System.out.println("********************************");
                                    System.out.println(":dowdnload block index: " + i);
                                    System.out.println(":block original index: " + blockchain1.getBlock(i).getIndex());
                                    System.out.println(":block from index: " + block.getIndex());
                                    System.out.println("---------------------------------");
                                } else {
                                    emptyList.add(block);

                                    if (i != 0) {
                                        System.out.println("portion:sub: " + 0 + " : " + i + " block index: " + block.getIndex());
                                        emptyList.addAll(blockchain1.subBlock(0, i));
                                    }

                                    emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                    temporaryBlockchain.setBlockchainList(emptyList);
                                    System.out.println("<><><<><><><>><><><><><><><<>><><><><>");
                                    System.out.println(":resolve_conflicts: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());
                                    System.out.println(":dowdnload block index: " + i);
                                    System.out.println(":block original index: " + blockchain.getBlock(i).getIndex());
                                    System.out.println(":block from index: " + block.getIndex());
                                    System.out.println("<><><<><><><>><><><><><><><<>><><><><>");
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                        continue;
                    }
                } catch (IOException e) {

                    System.out.println(":BasisController: resolve_conflicts: connect refused Error: " + s);
                    continue;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }

                //if the global blockchain is correct and it is larger than the longest previous temporary blockchain, then make it a contender as a future local blockchain
                //если глобальный блокчейн верный и он больше самого длиного предыдущего временного блокчейна, то сделать его претендентом в качестве будущего локального блокчейна
                if (temporaryBlockchain.validatedBlockchain()) {
                    if (bigSize < temporaryBlockchain.sizeBlockhain()) {
                        isBigPortion = isPortion;
                        bigSize = temporaryBlockchain.sizeBlockhain();
                    }
                    for (Block block : temporaryBlockchain.getBlockchainList()) {
                        hashCountZeroTemporary += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());
                    }

                    if (blocks_current_size < temporaryBlockchain.sizeBlockhain() && hashCountZeroAll < hashCountZeroTemporary) {
                        blocks_current_size = temporaryBlockchain.sizeBlockhain();
                        bigBlockchain = temporaryBlockchain;
                        hashCountZeroBigBlockchain = hashCountZeroTemporary;
                    }
                    hashCountZeroTemporary = 0;
                }

            }

            System.out.println("bigBlockchain: " + bigBlockchain.validatedBlockchain() + " : " + bigBlockchain.sizeBlockhain());
            //Only the blockchain that is not only the longest but also the most complex will be accepted.
            //Будет принять только тот блокчейн который является не только самым длинным, но и самым сложным.
            if (bigBlockchain.validatedBlockchain() && bigBlockchain.sizeBlockhain() > blockchainSize && hashCountZeroBigBlockchain > hashCountZeroAll) {
                System.out.println("resolve start addBlock start: ");
                blockchain1 = bigBlockchain;
                if (isBigPortion) {
                    List<Block> temp = bigBlockchain.subBlock(blockchainSize, bigBlockchain.sizeBlockhain());
                    Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                    addBlock2(temp,
                            balances);
                    System.out.println("temp size: " + temp.size());

                } else {


                    UtilsBlock.deleteFiles();
                    addBlock(bigBlockchain.getBlockchainList());
                }
                List<Block> temp = bigBlockchain.subBlock(blockchainSize, bigBlockchain.sizeBlockhain());

                System.out.println("size: " + blockchainSize);
                System.out.println(":BasisController: resolve: bigblockchain size: " + bigBlockchain.sizeBlockhain());
                System.out.println(":BasisController: resolve: validation bigblochain: " + bigBlockchain.validatedBlockchain());

                System.out.println("isPortion: " + isPortion + ":isBigPortion: " + isBigPortion + " size: " + temp.size());
                if (blockchainSize > bigSize) {
                    return 1;
                } else if (blockchainSize < bigSize) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } finally {
            updating = false;
        }
        return -4;

    }

    /**
     * rewrites the blockchain to files
     * производит перезапись блокчейна в файлы
     */

    public static void addBlock3(List<Block> originalBlocks, Map<String, Account> balances) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock3: blockchain is being updated: ");
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }

        //recalculation of the balance
        //перерасчет баланса
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();
        for (Block block : originalBlocks) {
            calculateBalance(balances, block, signs);
            balances = UtilsBalance.calculateBalanceFromLaw(balances, block, allLaws, allLawsWithBalance);
        }

        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

        //removal of obsolete laws
        //удаление устаревших законов
//        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //rewriting all existing laws
        //перезапись всех действующих законов
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        System.out.println(":BasisController: addBlock3: finish: " + originalBlocks.size());

    }

    public static void addBlock2(List<Block> originalBlocks, Map<String, Account> balances) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        //delete all files from resources folder
        //удалить все файлы из папки resources

        System.out.println(" addBlock2 start: ");

        //write a new blockchain from scratch to the resources folder
        //записать с нуля новый блокчейн в папку resources
        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock2: blockchain is being updated: ");
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }

        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();
        prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        //recalculation of the balance
        //перерасчет баланса
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();
        for (Block block : originalBlocks) {
            calculateBalance(balances, block, signs);
            balances = UtilsBalance.calculateBalanceFromLaw(balances, block, allLaws, allLawsWithBalance);
        }

        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

        //removal of obsolete laws
        //удаление устаревших законов
//        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //rewriting all existing laws
        //перезапись всех действующих законов
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        System.out.println(":BasisController: addBlock2: finish: " + originalBlocks.size());
    }

    public static void addBlock(List<Block> orignalBlocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain1 = null;
        Map<String, Account> balances = new HashMap<>();
        Blockchain temporaryForValidation = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        temporaryForValidation.setBlockchainList(orignalBlocks);
        //delete all files from resources folder
        //удалить все файлы из папки resources
        UtilsBlock.deleteFiles();
        System.out.println(" addBlock start: ");

        //write a new blockchain from scratch to the resources folder
        //записать с нуля новый блокчейн в папку resources
        for (Block block : orignalBlocks) {
            System.out.println(" :BasisController: addBlock: blockchain is being updated: ");
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }

        blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();
        prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        String json = UtilsJson.objToStringJson(shortDataBlockchain);
        UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
        //recalculation of the balance
        //перерасчет баланса
        balances = UtilsBalance.calculateBalances(blockchain1.getBlockchainList());
        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);


        //получение и отображение законов, а также сохранение новых законов
        //и изменение действующих законов
        Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain1.getBlockchainList(), Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);

        //возвращает все законы с балансом
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances,
                Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //removal of obsolete laws
        //удаление устаревших законов
        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //rewriting all existing laws
        //перезапись всех действующих законов
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        System.out.println(":BasisController: addBlock: finish");
    }

    /**
     * overwrites the current blockchain in the resources folder.
     * производит перезапись текущего блокчейна в папку ресурсы
     */
    @GetMapping("/addBlock")
    public ResponseEntity getBLock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        UtilsBlock.deleteFiles();
        addBlock(blockchain.getBlockchainList());
        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * get a block. добыть блок
     */
    @GetMapping("/miningblock")
    public synchronized ResponseEntity minings() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    /**
     * get a block. добыть блок
     */
    @GetMapping("/process-mining")
    public synchronized String proccessMining(Model model, Integer number) throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        model.addAttribute("title", "mining proccess");
        return "redirect:/process-mining";
    }


    /**
     * blockchain update. обновление блокчейна
     */
    @RequestMapping("/resolving")
    public String resolving() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        resolve_conflicts();
        return "redirect:/";
    }




    /**
     * Registers a new external host. Регистрирует новый внешний хост
     */
    @RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static synchronized void register_node(@RequestBody AddressUrl urlAddrress) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        for (String s : BasisController.getNodes()) {
            String original = s;
            String url = s + "/nodes/register";

//            try {
//                UtilUrl.sendPost(urlAddrress.getAddress(), url);
//                sendAddress();
//
//
//            } catch (Exception e) {
//                System.out.println(":BasisController: register node: wrong node: " + original);
//                BasisController.getNodes().remove(original);
//                continue;
//            }
        }

        Set<String> nodes = BasisController.getNodes();
        nodes = nodes.stream()
                .map(t -> t.replaceAll("\"", ""))
                .map(t -> t.replaceAll("\\\\", ""))
                .collect(Collectors.toSet());
        nodes.add(urlAddrress.getAddress());
        BasisController.setNodes(nodes);

        Mining.deleteFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.stream().forEach(t -> {
            try {
                UtilsAllAddresses.saveAllAddresses(t, Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            } catch (NoSuchProviderException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /**
     * connects to other nodes and takes their lists of hosts that are stored by them,
     * and keeps these lists at home. (currently partially disabled).
     * подключается к другим узлам и у них берет их списки хостов, которые хранятся у них,
     * и сохраняет эти списки у себя. (на данный момент частично отключен).
     */

    @GetMapping("/findAddresses")
    public static void findAddresses() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            Set<String> addressesSet = new HashSet<>();
            try {
                register_node(new AddressUrl(s));
                System.out.println(":start download addressses");
                System.out.println("trying to connect to the server:" + s + " timeout 45 seconds");
//                String addresses = UtilUrl.readJsonFromUrl(s + "/getDiscoveryAddresses");
//                addressesSet = UtilsJson.jsonToSetAddresses(addresses);
                System.out.println(":finish download addreses");
            } catch (IOException e) {
                System.out.println(":BasisController: findAddress: error");
                continue;
            }

        }

    }

    /**
     * Starts an automatic mining cycle, the cycle will go 2000 steps
     * Запускает автоматический цикл майнинга, цикл будет идти 2000 шагов
     */
    @GetMapping("/moreMining")
    public void moreMining() throws JSONException, IOException {
        for (int i = 1; i < 2000; i++) {
            System.out.println("block generate i: " + i);
            UtilUrl.readJsonFromUrl("http://localhost:8082/mine");


        }
    }


    /**
     * Sends its list of hosts to other nodes and tries to automatically register with them
     * Отправляет свой список хостов, другим узлам, и пытается автоматически регистрировать у них
     */
    public static void sendAddress() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //лист временный для отправки аддресов

        for (String s : Seting.ORIGINAL_ADDRESSES) {
            System.out.println(new Date() + ":start send addreses");
            String original = s;
            String url = s + "/nodes/register";

            if (BasisController.getExcludedAddresses().contains(url)) {
                System.out.println(":MainController: its your address or excluded address: " + url);
                continue;
            }
            try {
                for (String s1 : BasisController.getNodes()) {

                    System.out.println(":trying to connect to the server: send addreses: " + s + ": timeout 45 seconds");
                    AddressUrl addressUrl = new AddressUrl(s1);
                    String json = UtilsJson.objToStringJson(addressUrl);
                    UtilUrl.sendPost(json, url);
                }
            } catch (Exception e) {
                System.out.println(":BasisController: sendAddress: wronge node: " + original);

                continue;
            }


        }
        System.out.println(":finish send addressess");
    }


    /**
     * Sends a list of blocks to central stores (example: http://194.87.236.238:80)
     * Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:80)
     */
    public static int sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println(new Date() + ":BasisController: sendAllBlocksToStorage: start: ");
        int bigsize = 0;
        int blocks_current_size = (int) blocks.get(blocks.size()-1).getIndex() +1;
        //отправка блокчейна на хранилище блокчейна
        System.out.println(":BasisController: sendAllBlocksToStorage: ");
        getNodes().stream().forEach(System.out::println);
        for (String s : getNodes()) {

            System.out.println(":trying to connect to the server send block: " + s + ": timeout 45 seconds");

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println(":its your address or excluded address: " + s);
                continue;
            }

            try {
                System.out.println(":BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size = 0;
                if (Integer.valueOf(sizeStr) > 0)
                    size = Integer.valueOf(sizeStr);
                System.out.println(":BasisController: send: local size: " + blocks_current_size + " global size: " + size);
                if (size > blocks_current_size) {
                    System.out.println(":your local chain less: current: " + blocks_current_size + " global: " + size);
                    return -1;
                }
//                List<Block> fromToTempBlock = blocks.subList(size, blocks_current_size);
                List<Block> fromToTempBlock = new ArrayList<>();
                fromToTempBlock.addAll(blocks);
                SendBlocksEndInfo infoBlocks = new SendBlocksEndInfo(Seting.VERSION, fromToTempBlock);
                String jsonFromTo = UtilsJson.objToStringJson(infoBlocks);
                //if the current blockchain is larger than the storage, then
                //send current blockchain send to storage
                //если блокчейн текущей больше чем в хранилище, то
                //отправить текущий блокчейн отправить в хранилище
                if (size < blocks_current_size) {
                    if (bigsize < size) {
                        bigsize = size;
                    }
                    int response = -1;
                    //Test start algorithm
                    String originalF = s;
                    System.out.println(":send resolve_from_to_block");
                    String urlFrom = s + "/nodes/resolve_from_to_block";
                    try {
                        response = UtilUrl.sendPost(jsonFromTo, urlFrom);
                        System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE " + HttpStatus.CONFLICT.value());
                        System.out.println(":GOOD: SUCCESS  " + HttpStatus.OK.value());
                        System.out.println(":FAIL BAD BLOCKCHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                        System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                        System.out.println(":response: " + response);
                    } catch (Exception e) {
                        System.out.println(":exception resolve_from_to_block: " + originalF);

                    }
                    System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE: " + HttpStatus.CONFLICT.value());
                    System.out.println(":GOOD SUCCESS: " + HttpStatus.OK.value());
                    System.out.println(":FAIL BAD BLOCKHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                    System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                    System.out.println(":NAME CONFLICT: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("two miner addresses cannot be consecutive: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("PARITY ERROR" + HttpStatus.LOCKED);
                    System.out.println("Test version: If the index is even, then the stock balance must also be even; if the index is not even, all can mining"
                            + HttpStatus.LOCKED.value());
                    System.out.println("BLOCK HAS CHEATER ADDRESS: " + HttpStatus.SEE_OTHER);
                    System.out.println(":response: " + response );

                    System.out.println(":BasisController: sendAllBlocksStorage: response: " + response );
                    System.out.println("blocks: " + blocks);

                    //there is an up-to-date branch on the global server, download it and delete the obsolete branch.
                    //на глобальном сервере есть актуальная ветка, скачать ее и удалить устревшую ветку.
                    if (response == HttpStatus.CONFLICT.value()) {
                        System.out.println(":BasisController: sendAllBlocksStorage: start deleted 50 blocks:");
                        System.out.println(":size before delete: " + blockchainSize);
                       Blockchain blockchain1 = Mining.getBlockchain(
                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                                BlockchainFactoryEnum.ORIGINAL);
                        List<Block> temporary = blockchain1.subBlock(0, blockchainSize - Seting.DELETED_PORTION);
                        UtilsBlock.deleteFiles();
                        blockchain1.setBlockchainList(temporary);
                        UtilsBlock.saveBlocks(blockchain1.getBlockchainList(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                        blockchain1 = Mining.getBlockchain(
                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                                BlockchainFactoryEnum.ORIGINAL);

                        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                        blockchainSize = (int) shortDataBlockchain.getSize();
                        blockchainValid = shortDataBlockchain.isValidation();

                        UtilsBlock.deleteFiles();
                        addBlock(blockchain1.getBlockchainList());
                        System.out.println(":size after delete: " + blockchainSize);


                        int result = resolve();
                        System.out.println(":resolve: updated: " + result);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
                continue;

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
        if (blockchainSize > bigsize) {
            return 1;
        } else if (blockchainSize < bigsize) {
            return -1;
        } else if (blockchainSize == bigsize) {
            return 0;
        } else {
            return -4;
        }
    }

    /**
     * mine every 576 blocks. добывать каждые 576 блоков
     */
    @GetMapping("/constantMining")
    public String alwaysMining() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {

        while (true) {
            try {
                mining();
                if (Mining.isIsMiningStop()) {
                    System.out.println("production stopped");
                    Mining.setIsMiningStop(false);
                    break;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("BasisisController: constantMining find error:");
                continue;
            } catch (IOException e) {
                System.out.println("BasisisController: constantMining find error: ");
                continue;
            }
        }
        return "redirect:/mining";
    }


    public static List<DtoTransaction> deletedCheaters (List<DtoTransaction> dtoTransactions){
        List<DtoTransaction> withoutCheaters = new ArrayList<>();
        List<String> cheating = cheaters.entrySet().stream().filter(t->t.getValue() > Seting.LIMIT_CHEATING)
                .map(t->t.getKey()).collect(Collectors.toList());
        for (DtoTransaction dtoTransaction : dtoTransactions) {
            if(cheating.contains(dtoTransaction.getSender()))
                continue;

            withoutCheaters.add(dtoTransaction);
        }
        return withoutCheaters;
    }


    public static synchronized String mining() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining = true;
        try {

            findAddresses();

            resolve_conflicts();


            List<Block> tempBlockchain = Blockchain.subFromFile(
                    blockchainSize - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                    blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE
            );
            Block prevBlock = tempBlockchain.get(tempBlockchain.size()-1);
            long index = prevBlock.getIndex()+1;
            Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            Account miner = balances.get(User.getUserAddress());
            minerShow = miner;


            String sizeStr = "-1";
            try {
                sizeStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/size");
            } catch (NoRouteToHostException e) {
                System.out.println("home page you cannot connect to global server," +
                        "you can't give size global server");
                sizeStr = "-1";
            } catch (SocketException e) {
                System.out.println("home page you cannot connect to global server," +
                        "you can't give size global server");
                sizeStr = "-1";
            }
            Integer sizeG = Integer.valueOf(sizeStr);
            String text = "";
            //нахождение адрессов




            if (blockchainSize % 288 == 0) {
                System.out.println("clear storage transaction because is old");
                AllTransactions.clearAllTransaction();
            }
            //собирает класс список балансов из файла расположенного по пути Seting.ORIGINAL_BALANCE_FILE
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            //собирает объект блокчейн из файла

            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();





            if (blockchainSize <= 1) {
                System.out.println("save genesis block");
                Blockchain blockchain1 = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
                //сохранение генезис блока
                if (blockchainSize == 1) {
                    UtilsBlock.saveBLock(blockchain1.genesisBlock(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                }

                //получить список балансов из файла
                List<String> signs = new ArrayList<>();
                balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain1, balances, signs);
                //удалить старые файлы баланса
                Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);

                //сохранить балансы
                SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);
//

            }
            //скачать список балансов из файла
            System.out.println("BasisController: minining: read list balance");
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

            //получить счет майнера

            miner = balances.get(User.getUserAddress());
            minerShow = miner;
            System.out.println("BasisController: mining: account miner: " + miner);
            if (miner == null) {
                //если в блокчейне не было баланса, то баланс равен нулю
                miner = new Account(User.getUserAddress(), 0, 0);
            }

            //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions
            List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();
            //отказ от дублирующих транзакций
            List<Block> temp = Blockchain.subFromFile(blockchainSize - Seting.CHECK_DTO, blockchainSize, Seting.ORIGINAL_BLOCKCHAIN_FILE);
            temporaryDtoList = UtilsBlock.validDto(temp, temporaryDtoList);
            //блокировка читеров
            temporaryDtoList = deletedCheaters(temporaryDtoList);
            //отказ от транзакций которые меньше данного вознаграждения
            temporaryDtoList = UtilsTransaction.reward(temporaryDtoList, minDollarRewards);

            //раз в три для очищяет файлы в папке resources/sendedTransaction данная папка
            //хранит уже добавленые в блокчейн транзации, чтобы повторно не добавлять в
            //в блок уже добавленные транзакции

            AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
            System.out.println("BasisController: start mine:");

            //Сам процесс Майнинга
            //DIFFICULTY_ADJUSTMENT_INTERVAL как часто происходит коррекция
            //BLOCK_GENERATION_INTERVAL как часто должен находить блок
            //temporaryDtoList добавляет транзакции в блок
            Block block = Mining.miningDay(
                    miner,
                    tempBlockchain,
                    Seting.BLOCK_GENERATION_INTERVAL,
                    Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                    temporaryDtoList,
                    balances,
                    index
            );

            //синхронизируется с глобальным сервером и если в глобальном сервере,
            //появился более актуальный блок, то прекращает добывать.
            if (Mining.miningIsObsolete) {
                Mining.miningIsObsolete = false;
                System.out.println("This block has already been mined, we start a new cycle");
                return "ok";

            }
            if (Mining.isIsMiningStop()) {
                System.out.println("mining will be stopped");
                return "ok";
            }
            System.out.println("BasisController: finish mine:" + block.getIndex());

            //нужна для корректировки сложности
            int diff = Seting.DIFFICULTY_ADJUSTMENT_INTERVAL;
            //Тестирование блока
            List<Block> testingValidationsBlock = null;

            if (tempBlockchain.size() > diff) {
                testingValidationsBlock = Blockchain.clone(
                        tempBlockchain.size() - diff,
                        temp.size(), tempBlockchain
                );

            } else {
                testingValidationsBlock = Blockchain.clone(
                        0, tempBlockchain.size(), tempBlockchain
                );
            }

            String addresFounder = Blockchain.indexFromFile(0, Seting.ORIGINAL_BLOCKCHAIN_FILE).getFounderAddress();
            if (!block.getFounderAddress().equals(addresFounder)){
                System.out.println("wrong address founder: ");
            }
            //проверяет последние 288 блоков на валидность.
            if (testingValidationsBlock.size() > 1) {
                boolean validationTesting = UtilsBlock.validationOneBlock(
                        addresFounder,
                        testingValidationsBlock.get(testingValidationsBlock.size() - 1),
                        block,
                        Seting.BLOCK_GENERATION_INTERVAL,
                        diff,
                        testingValidationsBlock);

                if (validationTesting == false) {
                    System.out.println("wrong validation block: " + validationTesting);
                    System.out.println("index block: " + block.getIndex());
                    text = "wrong validation";

                }
                testingValidationsBlock.add(block.clone());
            }


            System.out.println("block to send: " + block.getIndex());
            List<Block> sends = new ArrayList<>();
            sends.add(block);
            sendAllBlocksToStorage(sends);



            //отправить адресса
//        sendAddress();
            text = "success: блок успешно добыт";
//        model.addAttribute("text", text);
        } finally {
            mining = false;
            Mining.miningIsObsolete = false;
        }
        return "ok";
    }

    /**
     * Стартует добычу, начинает майнинг
     */
    @GetMapping("/mine")
    public synchronized String mine(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining();

        return "redirect:/mining";

    }


    @GetMapping("/processUpdating")
    public String processUpdating(Model model) {
        model.addAttribute("isMining", isMining());
        model.addAttribute("isUpdating", isUpdating());
        if (minerShow == null) {
            model.addAttribute("address", "balance has not loaded yet");
            model.addAttribute("dollar", "balance has not loaded yet");
            model.addAttribute("stock", "balance has not loaded yet");
            model.addAttribute("stop", Mining.isIsMiningStop());

        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.################");
            String dollar = decimalFormat.format(minerShow.getDigitalDollarBalance());
            String stock = decimalFormat.format(minerShow.getDigitalStockBalance());
            model.addAttribute("address", minerShow.getAccount());
            model.addAttribute("dollar", dollar);
            model.addAttribute("stock", stock);
            model.addAttribute("stop", Mining.isIsMiningStop());
            model.addAttribute("expectedDifficult", difficultExpected);

        }

        if (difficultExpected == 0) {
            model.addAttribute("expectedDifficult", "");
        } else {
            model.addAttribute("expectedDifficult", difficultExpected);
        }


        return "processUpdating";
    }

    @GetMapping("/stopMining")
    public String stopMining(RedirectAttributes model) {
        Mining.setIsMiningStop(true);
        model.addAttribute("isMining", isMining());
        model.addAttribute("isUpdating", isUpdating());
        if (minerShow == null) {
            model.addAttribute("address", "balance has not loaded yet");
            model.addAttribute("dollar", "balance has not loaded yet");
            model.addAttribute("stock", "balance has not loaded yet");

            model.addAttribute("stop", Mining.isIsMiningStop() + " mining will be stopped, as file updates will stop. You can click on the main page, if mining is disabled then you will see it.");
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.################");
            String dollar = decimalFormat.format(minerShow.getDigitalDollarBalance());
            String stock = decimalFormat.format(minerShow.getDigitalStockBalance());
            model.addAttribute("address", minerShow.getAccount());
            model.addAttribute("dollar", dollar);
            model.addAttribute("stock", stock);

            model.addAttribute("stop", Mining.isIsMiningStop() + " mining will be stopped, as file updates will stop. You can click on the main page, if mining is disabled then you will see it.");

        }

        if (difficultExpected == 0) {
            model.addAttribute("expectedDifficult", "");
        } else {
            model.addAttribute("expectedDifficult", difficultExpected);
        }
        return "redirect:/processUpdating";
    }


    /**
     * Инициализирует блокчейн из файла и ShortDataBlockchain, а также предыдущий блок
     */
    public static boolean utilsMethod() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        boolean result = false;
        if (shortDataBlockchain.getSize() == 0
                || !shortDataBlockchain.isValidation()
                || shortDataBlockchain.getHashCount() == 0
                || prevBlock == null) {

            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);


            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();

            result = true;
        }
        return result;
    }


}


