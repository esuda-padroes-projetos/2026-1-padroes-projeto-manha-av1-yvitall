package com.yadot.api.repository;

import com.yadot.api.model.HabitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Essa interface herda todos os poderes de salvar, deletar e buscar do Spring com a função já pronta do JPA
public interface HabitRepository extends JpaRepository<HabitModel, Long> {
}