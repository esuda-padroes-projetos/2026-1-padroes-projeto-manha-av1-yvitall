package com.yadot.api.repository; // Verifique se o nome do pacote está correto no seu projeto

import com.yadot.api.model.CategoriaModel; // Importe a sua classe CategoriaModel
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    // o Spring Boot já fabrica automaticamente os métodos de CRUD (salvar, buscar, deletar) para a Categoria!
}