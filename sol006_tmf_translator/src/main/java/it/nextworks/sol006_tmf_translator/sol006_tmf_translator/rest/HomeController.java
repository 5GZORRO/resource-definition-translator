package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index() {
        return "redirect:swagger-ui/";
    }
}
