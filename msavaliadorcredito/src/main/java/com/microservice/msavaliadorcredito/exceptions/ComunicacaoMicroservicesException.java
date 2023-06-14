package com.microservice.msavaliadorcredito.exceptions;

import lombok.Getter;

public class ComunicacaoMicroservicesException extends RuntimeException{

    @Getter
    private Integer status;

    public ComunicacaoMicroservicesException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
}
