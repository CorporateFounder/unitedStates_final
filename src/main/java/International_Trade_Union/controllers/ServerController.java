package International_Trade_Union.controllers;

import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class ServerController {

    @PostMapping("/server")

    public String server(@RequestParam String host) throws IOException {
        System.out.println("your host changed: " + host);
        UtilsFileSaveRead.save(host, Seting.YOUR_SERVER, false);
        return "redirect:/seting";
    }

    @GetMapping("/server")
    public String server(){
        return "redirect:/seting";
    }
}
