package serveur.restservice;

public class ResponseError<A> extends Response {
    private A log;

    public ResponseError(A log){
        this.log = log;
    }

    public A getLog(){
        return log;
    }
}
