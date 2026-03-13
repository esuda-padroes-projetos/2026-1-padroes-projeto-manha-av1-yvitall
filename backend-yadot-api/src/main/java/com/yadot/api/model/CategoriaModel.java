package com.yadot.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "tb_categoria")
public class CategoriaModel {
@Id
@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String nome;

    public CategoriaModel() {
    }
    public CategoriaModel(String nome) {
        this.nome = nome;
    }
    @OneToMany(mappedBy = "categoria")
    private List<HabitModel> habitos;
}
