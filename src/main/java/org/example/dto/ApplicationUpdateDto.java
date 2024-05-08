package org.example.dto;

import lombok.*;
import org.example.model.StatusApplication;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApplicationUpdateDto {
    private String description;

    private StatusApplication statusApplication;
}