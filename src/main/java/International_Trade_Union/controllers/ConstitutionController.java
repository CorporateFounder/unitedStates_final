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
    @GetMapping("/charter")
    public String charter(Model model){
        model.addAttribute("title", "charter");
        return "charter";
    }
    @GetMapping("charter_rus")
    public String charter_rus(){
        return "charter_rus";
    }

    @GetMapping("charter_eng")
    public String charter_eng(){
        return "charter_eng";
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
