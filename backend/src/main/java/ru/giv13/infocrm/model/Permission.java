package ru.giv13.infocrm.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private EPermisson name;
    @Column(length = 25)
    private String displayName;
}
