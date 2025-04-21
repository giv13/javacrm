package ru.giv13.infocrm.project;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import ru.giv13.infocrm.user.User;

import java.time.LocalDateTime;
import java.util.Set;

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
    @JoinColumn(name = "responsible_id")
    private User responsible;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_participant", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Set<User> participants;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
