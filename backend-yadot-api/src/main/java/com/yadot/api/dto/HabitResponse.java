package com.yadot.api.dto;

import com.yadot.api.enums.Categoria;
import com.yadot.api.enums.DiasSemana;
import com.yadot.api.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitResponse {
    private Long id;

    private String nome;

    private Categoria categoria;

    private List<DiasSemana> diasSemana;

    @NotNull
    private Status status;
}
