package serveur.restservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AliveController {

    /**
     * Tells if the API is alive
     *
     * @return Log if API is alive
     */
    @PostMapping
    public Response isApiAlive() {
        return new ResponseLog<>("alive");
    }
}
