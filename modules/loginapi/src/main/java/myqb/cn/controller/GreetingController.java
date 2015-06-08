package myqb.cn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value = "/greeting1")
    public String greeting1() {
        return "no login";
    }

    @RequestMapping(value = "/greeting2",produces =  "text/json;charset=UTF-8")
     public String greeting2(@RequestParam(value="name", defaultValue="World") String name) {
        return new String("haha:"+name);
    }


}
