package ru.giv13.javacrm.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.giv13.javacrm.project.Project;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "\"user\"")
@Accessors(chain = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    private String notes;

    @Lob
    private byte[] avatar;

    @ColumnDefault("true")
    private boolean isActive = true;

    @ColumnDefault("true")
    private boolean isTokenExpired = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Project> projects;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_participant", joinColumns = @JoinColumn(name = "participant_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<Project> participation;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().flatMap(role -> role.getAuthorities().stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
