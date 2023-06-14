package com.microservice.msavaliadorcredito.exceptions;

public class DadosClientesNotFoundException extends RuntimeException{

    public DadosClientesNotFoundException() {
        super("Dados do cliente não encontrado para o CPF informado.");
    }
}
