package com.maurice.DocumentManagement.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@SuperBuilder
@ToString
@Setter
@Getter
@Entity
@NoArgsConstructor
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Long id;
    @NotEmpty(message = "Folder name required")
    @Column(length = 100, nullable = false)
    private String name;
    private String description;
    private String key;
    @ToString.Exclude
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<DocumentDetail> documentDetails;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Folder(String name, String description, String key) {
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public void addDocumentDetail(DocumentDetail documentDetail){
        this.documentDetails.add(documentDetail);
    }
}

