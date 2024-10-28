package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles", schema = "public")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Transient
    @ManyToMany(mappedBy = "status")
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }


    // public StatusUser(Long id) {
//        this.id = id;
//    }

//    public Role(Integer id, String name) {
//        this.id = id;
//        this.name = name;
//    }

//    CUSTOMER, //пользователь
//    OPERATOR,
//    ADMINISTRATOR
}
