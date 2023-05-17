package International_Trade_Union.controllers;

import International_Trade_Union.originalCorporateCharter.OriginalCHARTER;
import International_Trade_Union.originalCorporateCharter.OriginalPreamble;
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
        List<String> rus = charterRusList();
        List<String> eng = charterEngList();






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
        rus.add("OriginalCHARTER.POWERS_OF_THE_BOARD_OF_DIRECTORS:\n"+ OriginalCHARTER.POWERS_OF_THE_BOARD_OF_DIRECTORS);
        rus.add("OriginalCHARTER.HOW_LAWS_ARE_CHOSEN:\n"+ OriginalCHARTER.HOW_LAWS_ARE_CHOSEN);
        rus.add("OriginalCHARTER.HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED:\n"+ OriginalCHARTER.HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED);
        rus.add("OriginalCHARTER.POWERS_OF_THE_BOARD_OF_SHAREHOLDERS:\n"+ OriginalCHARTER.POWERS_OF_THE_BOARD_OF_SHAREHOLDERS);
        rus.add("OriginalCHARTER.HOW_SHAREHOLDERS_BOARD_IS_ELECTED:\n"+ OriginalCHARTER.HOW_SHAREHOLDERS_BOARD_IS_ELECTED);
        rus.add("OriginalCHARTER.VOTE_STOCK:\n"+ OriginalCHARTER.VOTE_STOCK);
        rus.add("OriginalCHARTER.CODE_VOTE_STOCK:\n"+ OriginalCHARTER.CODE_VOTE_STOCK);
        rus.add("OriginalCHARTER.POWERS_OF_DIRECTORS_IN_THE_OFFICE:\n"+ OriginalCHARTER.POWERS_OF_THE_CABINET_OF_DIRECTORS);
        rus.add("OriginalCHARTER.HOW_OFFICE_DIRECTORS_ARE_CHOSEN:\n"+ OriginalCHARTER.HOW_CABINET_DIRECTORS_ARE_CHOSEN);
        rus.add("OriginalCHARTER.ONE_VOTE:\n"+ OriginalCHARTER.ONE_VOTE);
        rus.add("OriginalCHARTER.CODE_VOTE_ONE:\n"+ OriginalCHARTER.CODE_VOTE_ONE);
        rus.add("OriginalCHARTER.MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES:\n"+ OriginalCHARTER.MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES);
        rus.add("OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS:\n"+ OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS);
        rus.add("OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES:\n"+ OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES);
        rus.add("OriginalCHARTER.HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED:\n"+ OriginalCHARTER.HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED);
        rus.add("OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN:\n"+ OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN);
        rus.add("OriginalCHARTER.POWERS_OF_THE_CHIEF_JUDGE:\n"+ OriginalCHARTER.POWERS_OF_THE_CHIEF_JUDGE);
        rus.add("OriginalCHARTER.HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER:\n"+ OriginalCHARTER.HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER);
        rus.add("OriginalCHARTER.HOW_THE_BUDGET_IS_APPROVED:\n"+ OriginalCHARTER.HOW_THE_BUDGET_IS_APPROVED);
        rus.add("OriginalCHARTER.HOW_IS_THE_STRATEGIC:\n"+ OriginalCHARTER.HOW_IS_THE_STRATEGIC);
        rus.add("OriginalCHARTER.HOW_NEW_POSITIONS_ARE_ADDED:\n"+ OriginalCHARTER.HOW_NEW_POSITIONS_ARE_ADDED);
        rus.add("OriginalCHARTER.PROPERTY_OF_THE_CORPORATION:\n"+ OriginalCHARTER.PROPERTY_OF_THE_CORPORATION);
        rus.add("OriginalCHARTER.INTERNET_STORE_DIRECTOR:\n"+ OriginalCHARTER.INTERNET_STORE_DIRECTOR);
        rus.add("OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR:\n"+ OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR);
        rus.add("OriginalCHARTER.DIRECTOR_OF_THE_DIGITAL_EXCHANGE:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_DIGITAL_EXCHANGE);
        rus.add("OriginalCHARTER.DIRECTOR_OF_DIGITAL_BANK:\n"+ OriginalCHARTER.DIRECTOR_OF_DIGITAL_BANK);
        rus.add("OriginalCHARTER.DIRECTOR_OF_THE_COMMERCIAL_COURT:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_COMMERCIAL_COURT);
        rus.add("OriginalCHARTER.MEDIA_DIRECTOR:\n"+ OriginalCHARTER.MEDIA_DIRECTOR);
        rus.add("OriginalCHARTER.DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION);
        rus.add("OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE:\n"+ OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE);
        rus.add("OriginalCHARTER.FREEDOM_OF_SPEECH:\n"+ OriginalCHARTER.FREEDOM_OF_SPEECH);
        rus.add("OriginalCHARTER.RIGHTS:\n"+ OriginalCHARTER.RIGHTS);
        return rus;
    }


    public static List<String> charterEngList(){
        List<String> eng = new ArrayList<>();
        eng.add("OriginalPreamble.ARTICLE_0\n"+ OriginalPreamble.ARTICLE_0);
        eng.add("OriginalCHARTER.POWERS_OF_THE_BOARD_OF_DIRECTORS:\n"+ OriginalCHARTER.POWERS_OF_THE_BOARD_OF_DIRECTORS);
        eng.add("OriginalCHARTER.HOW_LAWS_ARE_CHOSEN:\n"+ OriginalCHARTER.HOW_LAWS_ARE_CHOSEN);
        eng.add("OriginalCHARTER.HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED:\n"+ OriginalCHARTER.HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED);
        eng.add("OriginalCHARTER.POWERS_OF_THE_BOARD_OF_SHAREHOLDERS:\n"+ OriginalCHARTER.POWERS_OF_THE_BOARD_OF_SHAREHOLDERS);
        eng.add("OriginalCHARTER.HOW_SHAREHOLDERS_BOARD_IS_ELECTED:\n"+ OriginalCHARTER.HOW_SHAREHOLDERS_BOARD_IS_ELECTED);
        eng.add("OriginalCHARTER.VOTE_STOCK:\n"+ OriginalCHARTER.VOTE_STOCK);
        eng.add("OriginalCHARTER.CODE_VOTE_STOCK:\n"+ OriginalCHARTER.CODE_VOTE_STOCK);
        eng.add("OriginalCHARTER.POWERS_OF_DIRECTORS_IN_THE_OFFICE:\n"+ OriginalCHARTER.POWERS_OF_THE_CABINET_OF_DIRECTORS);
        eng.add("OriginalCHARTER.HOW_OFFICE_DIRECTORS_ARE_CHOSEN:\n"+ OriginalCHARTER.HOW_CABINET_DIRECTORS_ARE_CHOSEN);
        eng.add("OriginalCHARTER.ONE_VOTE:\n"+ OriginalCHARTER.ONE_VOTE);
        eng.add("OriginalCHARTER.CODE_VOTE_ONE:\n"+ OriginalCHARTER.CODE_VOTE_ONE);
        eng.add("OriginalCHARTER.MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES:\n"+ OriginalCHARTER.MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES);
        eng.add("OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS:\n"+ OriginalCHARTER.WHO_HAS_THE_RIGHT_TO_CREATE_LAWS);
        eng.add("OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES:\n"+ OriginalCHARTER.POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES);
        eng.add("OriginalCHARTER.HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED:\n"+ OriginalCHARTER.HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED);
        eng.add("OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN:\n"+ OriginalCHARTER.HOW_THE_CHIEF_JUDGE_IS_CHOSEN);
        eng.add("OriginalCHARTER.POWERS_OF_THE_CHIEF_JUDGE:\n"+ OriginalCHARTER.POWERS_OF_THE_CHIEF_JUDGE);
        eng.add("OriginalCHARTER.HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER:\n"+ OriginalCHARTER.HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER);
        eng.add("OriginalCHARTER.HOW_THE_BUDGET_IS_APPROVED:\n"+ OriginalCHARTER.HOW_THE_BUDGET_IS_APPROVED);
        eng.add("OriginalCHARTER.HOW_IS_THE_STRATEGIC:\n"+ OriginalCHARTER.HOW_IS_THE_STRATEGIC);
        eng.add("OriginalCHARTER.HOW_NEW_POSITIONS_ARE_ADDED:\n"+ OriginalCHARTER.HOW_NEW_POSITIONS_ARE_ADDED);
        eng.add("OriginalCHARTER.PROPERTY_OF_THE_CORPORATION:\n"+ OriginalCHARTER.PROPERTY_OF_THE_CORPORATION);
        eng.add("OriginalCHARTER.INTERNET_STORE_DIRECTOR:\n"+ OriginalCHARTER.INTERNET_STORE_DIRECTOR);
        eng.add("OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR:\n"+ OriginalCHARTER.GENERAL_EXECUTIVE_DIRECTOR);
        eng.add("OriginalCHARTER.DIRECTOR_OF_THE_DIGITAL_EXCHANGE:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_DIGITAL_EXCHANGE);
        eng.add("OriginalCHARTER.DIRECTOR_OF_DIGITAL_BANK:\n"+ OriginalCHARTER.DIRECTOR_OF_DIGITAL_BANK);
        eng.add("OriginalCHARTER.DIRECTOR_OF_THE_COMMERCIAL_COURT:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_COMMERCIAL_COURT);
        eng.add("OriginalCHARTER.MEDIA_DIRECTOR:\n"+ OriginalCHARTER.MEDIA_DIRECTOR);
        eng.add("OriginalCHARTER.DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION:\n"+ OriginalCHARTER.DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION);
        eng.add("OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE:\n"+ OriginalCHARTER.EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE);
        eng.add("OriginalCHARTER.FREEDOM_OF_SPEECH:\n"+ OriginalCHARTER.FREEDOM_OF_SPEECH);
        eng.add("OriginalCHARTER.RIGHTS:\n"+ OriginalCHARTER.RIGHTS);


        return eng;
    }
}
