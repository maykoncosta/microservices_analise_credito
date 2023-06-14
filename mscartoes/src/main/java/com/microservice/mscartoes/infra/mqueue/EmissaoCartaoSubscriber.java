package com.microservice.mscartoes.infra.mqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.mscartoes.domain.Cartao;
import com.microservice.mscartoes.domain.ClienteCartao;
import com.microservice.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.microservice.mscartoes.infra.repository.CartaoRepository;
import com.microservice.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmissaoCartaoSubscriber {

    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissaoCartao(@Payload String payload){
        try {
            var mapper = new ObjectMapper();
            var dadosEmissao = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = cartaoRepository.findById(dadosEmissao.getIdCartao()).orElseThrow();
            var clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dadosEmissao.getCpf());
            clienteCartao.setLimite(dadosEmissao.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCartao);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
