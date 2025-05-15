package com.example.asamurik_rest_api.entity;


import jakarta.persistence.*;
import jakarta.persistence.Transient;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "ID", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "Name", nullable = false, length = 100)
    private String name;


    @Transient
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
