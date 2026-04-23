package com.yadot.api.dto;

import com.yadot.api.enums.Categoria;
import com.yadot.api.enums.DiasSemana;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitRequest {
    @NotNull
    private Long userId;

    @NotBlank(message = "É obrigatório preencher o nome.")
    @Size(min = 3, max = 50)
    private String nome;

    @NotNull(message = "É obrigatório selecionar uma categoria")
    private Categoria categoria;

    private List<DiasSemana> diasSemana;

    @NotBlank(message = "É obrigatório selecionar um ícone.")
    private String habitIcon;
}
