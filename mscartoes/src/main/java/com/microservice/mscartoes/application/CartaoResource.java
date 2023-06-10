package com.microservice.mscartoes.application;

import com.microservice.mscartoes.domain.Cartao;
import com.microservice.mscartoes.domain.CartaoSaveRequest;
import com.microservice.mscartoes.domain.CartoesPorClienteResponse;
import com.microservice.mscartoes.domain.ClienteCartao;
import com.microservice.mscartoes.service.CartaoService;
import com.microservice.mscartoes.service.ClienteCartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("cartoes")
public class CartaoResource {

    private final CartaoService service;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status(){
        return "OK";
    }

    @PostMapping
    public ResponseEntity cadastrarCartao(@RequestBody CartaoSaveRequest request){
        Cartao cartao = request.toModel();
        service.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda){
        List<Cartao> cartoesRendaMenorIgualList = service.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(cartoesRendaMenorIgualList);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(
            @RequestParam("cpf") String cpf){
        List<ClienteCartao> clienteCartaoList = clienteCartaoService.listCartoesByCpf(cpf);
        var resultList = clienteCartaoList.stream()
                .map(CartoesPorClienteResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultList);
    }
}
