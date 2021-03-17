package serveur.restservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public Response handle(Exception e) throws JSONException {
        return new ResponseError<>(e.getMessage());
    }
}
