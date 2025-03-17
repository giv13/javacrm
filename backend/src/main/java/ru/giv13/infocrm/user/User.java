package ru.giv13.infocrm.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name="\"user\"")
@Accessors(chain = true)
public class User implements UserDetails {
    public interface Default {}
    public interface Profile {}

    @Id
    @GeneratedValue
    @JsonView(Default.class)
    private Integer id;

    @Column(length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    @JsonIgnore
    private String password;

    @JsonView(Default.class)
    private String notes;

    @Lob
    private byte[] avatar;

    @ColumnDefault("true")
    @JsonView(Default.class)
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonView(Default.class)
    private List<Role> roles;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().flatMap(role -> role.getAuthorities().stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @JsonView(Profile.class)
    public List<String> getPermissions() {
        return getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive;
    }
}
