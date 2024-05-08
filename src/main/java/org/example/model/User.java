package org.example.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String name;
    @ElementCollection(targetClass = StatusUser.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "status", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "status_name")
    private List<StatusUser> status;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() == user.getId() && getName().equals(user.getName());
    }
}
