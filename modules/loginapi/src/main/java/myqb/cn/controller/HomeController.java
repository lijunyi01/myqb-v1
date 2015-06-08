package myqb.cn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ljy on 15/6/8.
 * ok
 */
//@RestController
//public class HomeController {
//
//    @RequestMapping(value = "/")
//    public String greeting1() {
//        return "home";
//    }
//}

@RestController
public class HomeController {

    @RequestMapping(value = "/")
    public String greeting1() {
        return "home page";
    }
}
