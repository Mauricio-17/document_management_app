package com.maurice.DocumentManagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@ToString
@SuperBuilder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Share extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private PermissionAsset permission;
    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<DocumentDetail> documentDetails;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "share_user",
            joinColumns = @JoinColumn(name = "share_id"),
            inverseJoinColumns =  @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<UserEntity> users;

    public Share(PermissionAsset permission, List<DocumentDetail> documentDetails, List<UserEntity> users) {
        this.permission = permission;
        this.documentDetails = documentDetails;
        this.users = users;
    }

}
