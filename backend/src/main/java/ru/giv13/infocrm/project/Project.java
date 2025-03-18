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
    @JoinColumn(name = "responsible_id")
    @JsonIgnore
    private User responsible;

    @Column(name = "responsible_id", insertable = false, updatable = false)
    private Integer responsibleId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_participant", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
    @JsonIgnore
    private List<User> participants;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_participant", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "participant_id", insertable = false, updatable = false)
    private List<Integer> participantIds;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
