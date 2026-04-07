package com.yadot.api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private String sobrenome;

    @NotBlank(message = "O campo não pode estar vazio.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "O campo de SENHA não pode estar vazio.")
    @Pattern(regexp = ".*[A-Z].*", message = "Utilize pelo menos 1 caractere MAIÚSCULO")
    @Pattern(regexp = ".*[!@#$%^&*Ç].*", message = "Utilize pelo menos 1 caractere ESPECIAL")
    @Size(min = 8)
    private String senha;
}
