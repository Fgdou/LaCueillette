package serveur.restservice;

public class ResponseLog<A> extends Response{
    private A log;

    public ResponseLog(A log){
        this.log = log;
    }

    public A getLog(){
        return log;
    }
}
