package com.maurice.DocumentManagement.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "document_id")
    private Long id;
    @NotEmpty(message = "File name required")
    @Column(nullable = false, name = "file_name", length = 100)
    private String fileName;
    @NotEmpty(message = "File type required")
    @Column(nullable = false, name = "file_type", length = 10)
    private String fileType;
    @Column(nullable = false)
    private Long size;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "is_public", length = 1)
    private Boolean isPublic;
    @Column
    private String key;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "detail_id")
    private DocumentDetail documentDetail;
    @ManyToMany(mappedBy = "documents")
    @ToString.Exclude
    private List<Category> categories;

    public Document(String fileName, String fileType, Status status, Boolean isPublic, long size, String key) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = status;
        this.isPublic = isPublic;
        this.size = size;
        this.key = key;
    }
}
