package org.example.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Клас користувача системи "Гостьова книга басейну".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;

    @NotBlank(message = "Ім'я не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я повинно бути від 2 до 100 символів")
    private String name;

    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Невірний формат email")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 4, message = "Пароль повинен містити мінімум 4 символи")
    private String hashedPassword;

    @Size(max = 512, message = "URL зображення занадто довгий")
    private String imageUrl;

    public User(String name, String email, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }
}
