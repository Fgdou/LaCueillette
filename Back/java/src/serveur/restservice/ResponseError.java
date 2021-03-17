package serveur.restservice;

public class ResponseError<A> extends Response {
    private A error;

    public ResponseError(A log){
        this.error = log;
    }

    public A getError(){
        return error;
    }
}
