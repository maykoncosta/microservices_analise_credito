package com.microservice.msavaliadorcredito.application;

import com.microservice.msavaliadorcredito.domain.model.DadosAvaliacao;
import com.microservice.msavaliadorcredito.domain.model.DadosSolicitacaoEmissaoCartao;
import com.microservice.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import com.microservice.msavaliadorcredito.domain.model.SituacaoCliente;
import com.microservice.msavaliadorcredito.exceptions.ComunicacaoMicroservicesException;
import com.microservice.msavaliadorcredito.exceptions.DadosClientesNotFoundException;
import com.microservice.msavaliadorcredito.exceptions.SolicitacaoCartaoException;
import com.microservice.msavaliadorcredito.service.AvaliadorCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService service;

    @GetMapping
    public String status(){
        return "OK";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity getConsultarSituacaoCliente(@RequestParam("cpf") String cpf){
        try{
            SituacaoCliente situacaoCliente = service.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        }catch (DadosClientesNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (ComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados){
        try{
            RetornoAvaliacaoCliente retornoAvaliacaoCliente
                    = service.realizarAvaliacao(dados.getCpf(), dados.getRenda());
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        }catch (DadosClientesNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (ComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados){
        try{
            var protocoloSolicitacao = service.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocoloSolicitacao);
        }catch (SolicitacaoCartaoException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
