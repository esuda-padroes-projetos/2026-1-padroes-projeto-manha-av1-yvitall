package com.yadot.api.model;

import com.yadot.api.enums.Categoria;
import com.yadot.api.enums.DiasSemana;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // Lombok
@Entity // Transforma a classe em uma entidade no banco de dados (necessario em classesModel/Entities)
@Table(name = "tb_habitos") //criacao de uma tabela com nome da tabela
@NoArgsConstructor
@AllArgsConstructor
public class HabitModel {
    @Id //add auto na tabela o id (dependency JavaPersistenceAPI/BD)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //forma de gerar o id (1, 2...)

    private Long habitId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private UserModel usuario;

    @Size(min = 3, max = 50)
    @NotBlank(message = "É obrigatório preencher o campo.")
    @ManyToOne
    private String habitName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Categoria categoria;

    @NotBlank(message = "É obrigatório selecionar um ícone.")
    private String habitIcon;

    @ElementCollection(targetClass = DiasSemana.class)
    @CollectionTable(name = "habitos_dias", joinColumns = @JoinColumn(name = "habit_id"))
    @Enumerated(EnumType.STRING) // ao inves de armazenar os numeros(indices do enum) salvaremos a string contendo dia da semana
    private List<DiasSemana> diasDaSemana;


    // Um Hábito tem um histórico de vários check-ins
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<CheckinModel> historicoCheckins;
}
