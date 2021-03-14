package serveur.restservice;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.sql.Store;

import java.util.Map;

@RestController
public class StoreController {

    @GetMapping("/store/getInfos")
    public Store getInformations(@RequestParam(value = "id") int id) throws Exception{
        return Store.getById(id);
    }


}
