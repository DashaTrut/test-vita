package org.example.dto;

import lombok.*;
import org.example.model.StatusApplication;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    @NotBlank(message = "Заявка не может быть пустой")
    private String description;

    @NotNull
    private StatusApplication statusApplication;
}
