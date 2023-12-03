package ge.itstep.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Getter
    @Setter
    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    @Getter
    @Setter
    private List<Role> roles = new ArrayList<>();

    @Getter
    @Setter
    private int playedMatches;
    @Getter
    @Setter
    private int draws;
    @Getter
    @Setter
    private int loses;
    @Getter
    @Setter
    private int wins;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.passwordHash = password;


    }

}
