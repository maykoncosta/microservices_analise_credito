package com.microservice.mscartoes.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nome;

    @Column
    @Enumerated(EnumType.STRING)
    private BandeiraCartao bandeira;

    @Column
    private BigDecimal renda;

    @Column
    private BigDecimal limite;

    public Cartao(String nome,
                  BandeiraCartao bandeira,
                  BigDecimal renda,
                  BigDecimal limite) {
        this.nome = nome;
        this.bandeira = bandeira;
        this.renda = renda;
        this.limite = limite;
    }
}
