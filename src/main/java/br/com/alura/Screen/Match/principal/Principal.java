package br.com.alura.Screen.Match.principal;

import br.com.alura.Screen.Match.modelo.*;
import br.com.alura.Screen.Match.repository.SerieRepository;
import br.com.alura.Screen.Match.services.ConsumoApi;
import br.com.alura.Screen.Match.services.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
//    List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
    this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = 6;
        while (opcao != 0) {

            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar series
                    4 - Buscar série por titulo
                    5 - Buscar série pelo ator
                    6 - Buscar top 5 séries
                    7 - Buscar pelo genero
                    8 - Buscar series por um total de temporadas
                    9 - Buscar episodio por trecho
                   10 - Buscar top 5 episodios pela serie
                   11 - Buscar episodios a partir de uma data
                                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4 :
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAutor();
                    break;
                case 6 :
                    buscarTop5Series();
                    break;
                case 7 :
                    buscarSeriePorGenero();
                    break;
                case 8 :
                    buscarSeriePorTemporada();
                    break;
                case 9 :
                    buscarEpisodioPorTrecho();
                    break;
                case 10 :
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome");
        var nomeSerie = leitura.nextLine();
        List<DadosTemporada> temporadas = new ArrayList<>();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serie.isPresent()) {

            var serieEncontrada = serie.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

                temporadas.add(dadosTemporada);

            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d->d.episodios().stream()
                            .map(e-> new Episodio(d.numero(), e)))
                    .toList();
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Série buscada " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!'");
        }
    }

    private void buscarSeriePorAutor() {
        System.out.println("Digite o nome do autor...");
        var nomeAtor = leitura.nextLine();

        System.out.println("Você gostaria de filtrar as avaliações a partir de qual nota? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

     List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " com a avaliação de: " + s.getAvaliacao()));
    }
    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> System.out.println(s.getTitulo() + " com a avaliação de: " + s.getAvaliacao()));
        }
    private void buscarSeriePorGenero() {
        System.out.println("Você busca filmes de qual genero?");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
       List<Serie> buscarSeriePorCategoria = repositorio.findByGenero(categoria);

        System.out.println("Series encontradas da categoria " + nomeGenero + ":");

        buscarSeriePorCategoria.forEach(System.out::println);
    }
    private void buscarSeriePorTemporada() {
        System.out.println("Qual o total de temporadas que voce busca?");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Você gostaria de filtrar as avaliações da temporada a partir de qual nota? ");
        var avaliacao = leitura.nextDouble();

        leitura.nextLine();
        List<Serie> buscarSeriePorTemporada = repositorio.seriesPorTemporadaEAValiacao(totalTemporadas, avaliacao);
        buscarSeriePorTemporada.forEach(s ->
                System.out.println("Serie filtrada: " + s.getTitulo() + " com a avaliação de: " + s.getAvaliacao()));
    }
    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome do epísodio para busca");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }
    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodioPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(),e.getAvaliacao()));
        }
    }
    private void buscarEpisodiosDepoisDeUmaData(){
        buscarSeriePorTitulo();
        Serie serie = serieBusca.get();
        if (serieBusca.isPresent()){
            System.out.println("Digite o ano limite do ano de lançamente");
            var anoDeLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieBusca, anoDeLancamento);
        }
    }
}