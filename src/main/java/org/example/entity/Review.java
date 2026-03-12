package org.example.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Клас відгуку про послугу басейну.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private int id;

    @Min(value = 1, message = "ID користувача повинен бути більше 0")
    private int userId;

    @Min(value = 1, message = "ID послуги повинен бути більше 0")
    private int serviceId;

    @NotBlank(message = "Текст відгуку не може бути порожнім")
    @Size(min = 5, max = 255, message = "Текст відгуку повинен бути від 5 до 255 символів")
    private String text;

    @Min(value = 1, message = "Рейтинг повинен бути від 1 до 5")
    @Max(value = 5, message = "Рейтинг повинен бути від 1 до 5")
    private int rating;

    public Review(int userId, int serviceId, String text, int rating) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.text = text;
        this.rating = rating;
    }
}
