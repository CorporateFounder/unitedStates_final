package International_Trade_Union.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import International_Trade_Union.model.CreateAccount;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Controller
public class CreateAccountController {


    /**Позвалояет создавать счет, отображается в браузере*/
    @GetMapping("create-account")
    public String createAccount(Model model) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }

        model.addAttribute("title", "create minerAccount");
        Map<String, String> newAccount = CreateAccount.create();

        model.addAttribute("login", newAccount.get("pubKey"));
        model.addAttribute("password", newAccount.get("privKey"));
        return "create-account";
    }


    @PostMapping("create-account")
    public String createNewAccount(RedirectAttributes redirectAttrs) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        redirectAttrs.addFlashAttribute("title", "create minerAccount");
        Map<String, String> newAccount = CreateAccount.create();

        redirectAttrs.addFlashAttribute("login", newAccount.get("pubKey"));
        redirectAttrs.addFlashAttribute("password", newAccount.get("privKey"));

        return "redirect:/create-account";
    }

}
