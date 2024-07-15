package br.com.alura.Screen.Match;

import br.com.alura.Screen.Match.modelo.DadosSeries;
import br.com.alura.Screen.Match.services.ConsumoAPI;
import br.com.alura.Screen.Match.services.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=lost&apikey=41b349d0");
		System.out.println(json);
		ConverterDados conversor = new ConverterDados();
		DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
		System.out.println(dados);
	}
}
