package com.math012.usuario.business;

import com.math012.usuario.infra.client.ViaCepClient;
import com.math012.usuario.infra.client.dtoClient.ViaCepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepDTO buscarDadosDeEndereco(String cep){
        return viaCepClient.buscaEnderecoViaCep(processarCep(cep));

    }

    private String processarCep(String cep){
        String cepFormatado = cep
                .replace(" ", "")
                .replace("-","");

        if (!cepFormatado.matches("\\d+") || !Objects.equals(cepFormatado.length(), 8)){
            throw new IllegalArgumentException("O cep contém caracteres inválidos, por favor varificar");

        }
        return cepFormatado;
    }
}
