package com.yadot.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter // Lombok
@Entity // Transforma a classe em uma entidade no banco de dados
@Table(name = "tb_habitos") //criacao de uma tabela com nome da tabela

public class HabitModel {
    @Id //add auto na tabela o id (dependency JavaPersistenceAPI/BD)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long habitId;
    String habitName;
    String habitCategory;
    String habitIcon;

    public HabitModel() {
    }

    public HabitModel(String habitName, String habitCategory, String habitIcon) {
        this.habitName = habitName;
        this.habitCategory = habitCategory;
        this.habitIcon = habitIcon;
    }
}
