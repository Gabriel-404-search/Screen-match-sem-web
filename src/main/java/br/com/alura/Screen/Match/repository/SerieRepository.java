package br.com.alura.Screen.Match.repository;

import br.com.alura.Screen.Match.modelo.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> FindByTituloContainsIgnoreCase(String nomeSerie);
}