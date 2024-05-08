package org.example.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StatusApplicationConverter implements AttributeConverter<StatusApplication, String> {

    @Override
    public String convertToDatabaseColumn(StatusApplication statusApplication) {
        if (statusApplication == null) {
            return null;
        }
        return statusApplication.name();
    }

    @Override
    public StatusApplication convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StatusApplication.valueOf(dbData);
    }
}
