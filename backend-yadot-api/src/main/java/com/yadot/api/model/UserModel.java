package com.yadot.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String nome;

    @Column (nullable = false)
    private String sobrenome;

    @NotBlank(message = "O campo de SENHA não pode estar vazio.")
    @Pattern(regexp = ".*[A-Z].*", message = "Utilize pelo menos 1 caractere MAIÚSCULO")
    @Pattern(regexp = ".*[!@#$%^&*Ç].*", message = "Utilize pelo menos 1 caractere ESPECIAL")
    @Size(min = 8)
    private String senhaHash;

    @Column (nullable = false, unique = true)
    @NotNull
    @Email
    private String email;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<HabitModel> habitos;
}