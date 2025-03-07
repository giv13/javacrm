package ru.giv13.infocrm.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Permission {
    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private EPermisson name;

    @Column(length = 25)
    private String displayName;
}
