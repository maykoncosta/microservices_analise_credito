package com.microservice.msavaliadorcredito.service;

import com.microservice.msavaliadorcredito.domain.model.*;
import com.microservice.msavaliadorcredito.exceptions.ComunicacaoMicroservicesException;
import com.microservice.msavaliadorcredito.exceptions.DadosClientesNotFoundException;
import com.microservice.msavaliadorcredito.exceptions.SolicitacaoCartaoException;
import com.microservice.msavaliadorcredito.infra.clients.CartaoResourceClient;
import com.microservice.msavaliadorcredito.infra.clients.ClienteResourceClient;
import com.microservice.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartaoResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;


    public SituacaoCliente obterSituacaoCliente(String cpf){
        try{
            ResponseEntity<DadosCliente> dadosCliente = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesByCliente = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosCliente.getBody())
                    .cartoes(cartoesByCliente.getBody())
                    .build();

        }catch (FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClientesNotFoundException();
            }
            throw new ComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda){
        try{
            ResponseEntity<DadosCliente> dadosCliente = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> castoesResponse = cartoesClient.getCartoesRendaAteh(renda);
            List<Cartao> cartoes = castoesResponse.getBody();

            var cartoesAprovadosList = extrairCartoesAprovadosParaCliente(dadosCliente, cartoes);
            return new RetornoAvaliacaoCliente(cartoesAprovadosList);

        }catch (FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClientesNotFoundException();
            }
            throw new ComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    private static List<CartaoAprovado> extrairCartoesAprovadosParaCliente(ResponseEntity<DadosCliente> dadosCliente, List<Cartao> cartoes) {
        return cartoes.stream().map(cartao -> {
            DadosCliente dados = dadosCliente.getBody();
            var limiteBasico = cartao.getLimite();
            var idadeBD = BigDecimal.valueOf(dados.getIdade());
            var fator = idadeBD.divide(BigDecimal.valueOf(10));
            var limiteAprovado = fator.multiply(limiteBasico);

            var cartaoAprovado = new CartaoAprovado();
            cartaoAprovado.setCartao(cartao.getNome());
            cartaoAprovado.setLimiteAprovado(limiteAprovado);
            cartaoAprovado.setBandeira(cartao.getBandeira());

            return cartaoAprovado;
        }).collect(Collectors.toList());
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new SolicitacaoCartaoException(e.getMessage());
        }
    }

}
