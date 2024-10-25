package org.example.model;

import lombok.*;
import org.example.converter.StatusApplicationConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications", schema = "public")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private LocalDateTime timeCreate;
    @Convert(converter = StatusApplicationConverter.class)
    @Column(name = "status_application", nullable = false)
    private StatusApplication statusApplication;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return getId() == that.getId() && getUser() == that.getUser() && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getTimeCreate(), that.getTimeCreate()) && getStatusApplication() == that.getStatusApplication();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

