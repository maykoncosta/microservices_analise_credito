package com.microservice.mscartoes.infra.mqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.mscartoes.domain.Cartao;
import com.microservice.mscartoes.domain.ClienteCartao;
import com.microservice.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.microservice.mscartoes.infra.repository.CartaoRepository;
import com.microservice.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmissaoCartaoSubscriber {

    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissaoCartao(@Payload String payload){
        try {
            log.info("Inicio - Receber dados para emitir cartao");
            var mapper = new ObjectMapper();
            var dadosEmissao = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = cartaoRepository.findById(dadosEmissao.getIdCartao()).orElseThrow();
            var clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dadosEmissao.getCpf());
            clienteCartao.setLimite(dadosEmissao.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCartao);
            log.info("Fim - Cartao Emitido");
        } catch (Exception e) {
            log.error("Erro ao receber solicitacao de emissao do cartao: " + e.getMessage());
        }

    }

}
