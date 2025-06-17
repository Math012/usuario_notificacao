package com.math012.usuario.infra.execptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String msg){
        super(msg);
    }

    public ConflictException(String msg, Throwable throwable){
        super(msg);
    }
}
