package com.aide.core;

/**
 * Created by Administrator on 2016/12/24.
 */
public class AideException extends RuntimeException {

    public AideException(){
    }
    public AideException(String message){
        super(message);
    }
    public AideException(String message,Throwable cause){
        super(message,cause);
    }
    public AideException(Throwable cause){
        super(cause);
    }
}
