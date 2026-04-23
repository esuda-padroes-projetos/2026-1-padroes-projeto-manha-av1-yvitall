package com.yadot.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Entity // Transforma a classe em uma entidade no banco de dados (necessario em classesModel/Entities)
@Table(name = "tb_checkin") //criacao de uma tabela com nome da tabela
@NoArgsConstructor
@AllArgsConstructor
public class CheckinModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkinId;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    @NotNull
    private HabitModel habit;

    @NotNull
    private LocalDate dataCheckin;
}
