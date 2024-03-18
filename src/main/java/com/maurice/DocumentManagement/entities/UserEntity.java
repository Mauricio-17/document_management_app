package com.maurice.DocumentManagement.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@ToString
@SuperBuilder
@Setter
@Getter
@Entity
@Table(name = "user_app")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @NotEmpty(message = "Name required")
    @Column(nullable = false, length = 70)
    private String name;
    @Column(length = 70)
    private String lastname;
    @Email(message = "The field must look like an email")
    @NotEmpty(message = "Email required")
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    @NotEmpty(message = "Password required")
    @Column(nullable = false)
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
    /*
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified; */
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Share> shares;

    public UserEntity(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(plan.getName()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
