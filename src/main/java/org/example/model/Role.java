package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles", schema = "public")
public class Role implements GrantedAuthority {
    @Id
    private Integer id;
    private String name;
    @Transient
    @ManyToMany(mappedBy = "status")
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

    public Role() {
    }

    // public StatusUser(Long id) {
//        this.id = id;
//    }

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

//    CUSTOMER, //пользователь
//    OPERATOR,
//    ADMINISTRATOR
}
