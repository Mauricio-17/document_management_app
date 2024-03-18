package com.maurice.DocumentManagement.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@SuperBuilder
@Setter
@Getter
@Entity
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    private String description;
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "category_document",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documents;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
