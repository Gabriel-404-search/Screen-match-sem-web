package br.com.alura.Screen.Match.services;

import br.com.alura.Screen.Match.modelo.DadosSeries;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterDados implements IConverteDados{
   private ObjectMapper mapper = new ObjectMapper();

    public DadosSeries obterDados(String json) {
        return null;
    }

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json,classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
