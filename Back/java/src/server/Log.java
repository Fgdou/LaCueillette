package server;

import java.util.Date;

public class Log {
    /*
     * Used to show information to the user, without using println.
     * Use this class in case output method change
     */

    public enum LOG_STATE {
        INFO,
        WARNING,
        ERROR
    }
    private static LOG_STATE _state = LOG_STATE.INFO;

    /**
     * Inform the user
     * @param msg
     */
    public static void info(String msg){
        if(_state == LOG_STATE.INFO)
            _message((new Date()).toString() + " [INFO] : " + msg);
    }

    /***
     * Warning
     * @param msg
     */
    public static void warn(String msg){
        if(_state == LOG_STATE.INFO || _state == LOG_STATE.WARNING)
            _message((new Date()).toString() + " [WARN] : " + msg);
    }

    /**
     * Error, displayed in red
     * @param msg
     */
    public static void error(String msg){
        _messageError((new Date()).toString() + " [ERROR] : " + msg);
    }

    /**
     * Fatal error, will quit the application
     * @param msg
     */
    public static void fatal(String msg){
        _messageError((new Date()).toString() + " [FATAL] : " + msg);
        _messageError("Quitting...");
        System.exit(1);
    }

    public static void changeState(LOG_STATE state){
        _state = state;
    }
    public static LOG_STATE getState(){
        return _state;
    }

    private static void _message(String msg){
        System.out.println(msg);
    }
    private static void _messageError(String msg){
        System.err.println(msg);
    }
}
