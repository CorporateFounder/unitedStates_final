package International_Trade_Union.controllers;

import International_Trade_Union.setings.originalCorporateCharter.OriginalCHARTER;
import International_Trade_Union.setings.originalCorporateCharter.OriginalCHARTER_ENG;
import International_Trade_Union.setings.originalCorporateCharter.OriginalPreamble;
import International_Trade_Union.setings.originalCorporateCharter.OriginalPreambleEng;
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


    /**Отображает устав в браузере.
     * Displays the charter in the browser.*/
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


        rus.add("УСТАВ\n"+ OriginalCHARTER.LAW_1);



        return rus;
    }


    public static List<String> charterEngList(){
        List<String> eng = new ArrayList<>();


        eng.add("CHARTER\n"+ OriginalCHARTER_ENG.LAW_1);




        return eng;
    }
}
