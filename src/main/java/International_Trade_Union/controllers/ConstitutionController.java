package International_Trade_Union.controllers;

import International_Trade_Union.originalCorporateCharter.OriginalCHARTER;
import International_Trade_Union.originalCorporateCharter.OriginalCHARTER_ENG;
import International_Trade_Union.originalCorporateCharter.OriginalPreamble;
import International_Trade_Union.originalCorporateCharter.OriginalPreambleEng;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ConstitutionController {

    /**Отображает устав в браузере*/
    @GetMapping("corporate-charter")
    public String constutionRus(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        model.addAttribute("title", "corporate charter-корпоративный устав");
        List<String> eng = charterEngList();
        List<String> rus = charterRusList();







        model.addAttribute("rus", rus);
        model.addAttribute("eng", eng);
        return "corporate-charter";
    }

    public static String charterRus (){
        String string = "";
        for (String s : charterRusList()) {
            string = s + "\n";
        }
        return string;
    }

    public static String charterEng(){
        String string = "";
        for (String s : charterEngList()) {
            string = s + "\n";
        }
        return string;
    }


    public static List<String> charterRusList(){
        List<String> rus = new ArrayList<>();


        rus.add("OriginalPreamble.ARTICLE_0\n"+ OriginalPreamble.ARTICLE_0);
        rus.add("OriginalCHARTER.HOW_LAWS_ARE_CHOSEN_1:\n"+ OriginalCHARTER.HOW_LAWS_ARE_CHOSEN_1);
        rus.add("OriginalCHARTER.VOTE_STOCK_2:\n"+ OriginalCHARTER.VOTE_STOCK_2);
        rus.add("OriginalCHARTER.ONE_VOTE_3:\n"+ OriginalCHARTER.ONE_VOTE_3);
        rus.add("OriginalCHARTER.VOTE_FRACTION_4:\n"+ OriginalCHARTER.VOTE_FRACTION_4);
        rus.add("OriginalCHARTER.Penalty_mechanism_5:\n"+ OriginalCHARTER.Penalty_mechanism_5);
        rus.add("OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6:\n"+ OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6);
        rus.add("OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7:\n"+ OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7);
        rus.add("OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8:\n"+ OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8);
        rus.add("OriginalCHARTER.PROPERTY_OF_THE_CORPORATION_9:\n"+ OriginalCHARTER.PROPERTY_OF_THE_CORPORATION_9);
        rus.add("OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR_10:\n"+ OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR_10);
        rus.add("OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11:\n"+ OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11);
        rus.add("OriginalCHARTER.FREEDOM_OF_SPEECH_12:\n"+ OriginalCHARTER.FREEDOM_OF_SPEECH_12);
        rus.add("OriginalCHARTER.RIGHTS_13:\n"+ OriginalCHARTER.RIGHTS_13);
        rus.add("OriginalCHARTER.LEGISLATURE_14:\n"+ OriginalCHARTER.LEGISLATURE_14);
        rus.add("OriginalCHARTER.FRACTION_15:\n"+ OriginalCHARTER.FRACTION_15);

        return rus;
    }


    public static List<String> charterEngList(){
        List<String> eng = new ArrayList<>();


        eng.add("OriginalPreamble.ARTICLE_0\n"+ OriginalPreambleEng.ARTICLE_0);
        eng.add("OriginalCHARTER.HOW_LAWS_ARE_CHOSEN_1:\n"+ OriginalCHARTER_ENG.HOW_LAWS_ARE_CHOSEN_1);
        eng.add("OriginalCHARTER.VOTE_STOCK_2:\n"+ OriginalCHARTER_ENG.VOTE_STOCK_2);
        eng.add("OriginalCHARTER.ONE_VOTE_3:\n"+ OriginalCHARTER_ENG.ONE_VOTE_3);
        eng.add("OriginalCHARTER.VOTE_FRACTION_4:\n"+ OriginalCHARTER_ENG.VOTE_FRACTION_4);
        eng.add("OriginalCHARTER.Penalty_mechanism_5:\n"+ OriginalCHARTER_ENG.Penalty_mechanism_5);
        eng.add("OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6:\n"+ OriginalCHARTER_ENG.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6);
        eng.add("OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7:\n"+ OriginalCHARTER_ENG.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7);
        eng.add("OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8:\n"+ OriginalCHARTER_ENG.HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8);
        eng.add("OriginalCHARTER.PROPERTY_OF_THE_CORPORATION_9:\n"+ OriginalCHARTER_ENG.PROPERTY_OF_THE_CORPORATION_9);
        eng.add("OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR_10:\n"+ OriginalCHARTER_ENG.GENERAL_EXECUTIVE_DIRECTOR_10);
        eng.add("OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11:\n"+ OriginalCHARTER_ENG.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11);
        eng.add("OriginalCHARTER.FREEDOM_OF_SPEECH_12:\n"+ OriginalCHARTER_ENG.FREEDOM_OF_SPEECH_12);
        eng.add("OriginalCHARTER.RIGHTS_13:\n"+ OriginalCHARTER_ENG.RIGHTS_13);
        eng.add("OriginalCHARTER.LEGISLATURE_14:\n"+ OriginalCHARTER_ENG.LEGISLATURE_14);
        eng.add("OriginalCHARTER.FRACTION_15:\n"+ OriginalCHARTER_ENG.FRACTION_15);

        return eng;
    }
}
