package serveur.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import serveur.DataBase;

@SpringBootApplication
public class RunWebService {
    public static void main(String[] args) throws Exception {
        DataBase.createInstance();
        SpringApplication.run(RunWebService.class, args);
    }
}