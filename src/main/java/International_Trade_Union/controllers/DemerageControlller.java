package International_Trade_Union.controllers;

import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsDemerage;
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
public class DemerageControlller {
    @GetMapping("/all_demerage")
    public String allDemerage(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();

        List<InfoDemerageMoney> infoDemerageMonies = new ArrayList<>();
        infoDemerageMonies = UtilsDemerage.readDemerage(Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        model.addAttribute("title", "demerage");
        model.addAttribute("infoDemerageMonies", infoDemerageMonies);
        return "all_demerage";
    }
}
