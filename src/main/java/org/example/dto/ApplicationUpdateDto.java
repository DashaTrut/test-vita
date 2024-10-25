package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.StatusApplication;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApplicationUpdateDto {
    private String description;

    private StatusApplication statusApplication;
}