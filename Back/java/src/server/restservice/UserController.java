package server.restservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.sql.User;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/user")
    public User userGetByEmail(@RequestParam(value = "email") String email) throws Exception {
        return server.sql.User.getByEmail(email);
    }

}
