package ru.giv13.infocrm.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import ru.giv13.infocrm.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Accessors(chain = true)
public class Project {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 25)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private Integer ownerId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "user_id")
    private List<Integer> team;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
