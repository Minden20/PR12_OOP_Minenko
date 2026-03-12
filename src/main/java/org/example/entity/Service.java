package org.example.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Клас послуги басейну.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    private int id;

    @NotBlank(message = "Назва послуги не може бути порожньою")
    @Size(min = 2, max = 255, message = "Назва повинна бути від 2 до 255 символів")
    private String name;

    @Size(max = 255, message = "Опис не може перевищувати 255 символів")
    private String description;

    public Service(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
