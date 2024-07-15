package br.com.alura.Screen.Match.services;


public interface IConverteDados {
  <T> T obterDados( String json, Class<T> classe);
}
