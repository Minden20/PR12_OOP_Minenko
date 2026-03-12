package org.example.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Клас відвідувача басейну.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visitor {

    private int id;

    @NotBlank(message = "Ім'я відвідувача не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я повинно бути від 2 до 100 символів")
    private String name;

    @NotBlank(message = "Телефон не може бути порожнім")
    @Pattern(regexp = "\\+?\\d{10,13}", message = "Невірний формат телефону")
    private String phone;

    @NotNull(message = "Дата відвідування не може бути порожньою")
    private String visitDate;

    @Min(value = 1, message = "ID послуги повинен бути більше 0")
    private int serviceId;

    public Visitor(String name, String phone, String visitDate, int serviceId) {
        this.name = name;
        this.phone = phone;
        this.visitDate = visitDate;
        this.serviceId = serviceId;
    }
}
