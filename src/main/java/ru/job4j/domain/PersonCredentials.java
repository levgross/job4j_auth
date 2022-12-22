package ru.job4j.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PersonCredentials {
    @EqualsAndHashCode.Include
    @NotBlank(message = "Login must not be empty!")
    private String login;
    private String password;
}
