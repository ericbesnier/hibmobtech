package com.hibernatus.hibmobtech.model;

/**
 * Created by Eric on 18/01/2016.
 */
public class BasicRefEntity {
    Long id;
    String description;
    String category;

    public BasicRefEntity(String description) {
        this.description = description;
    }

    public BasicRefEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicRefEntity)) return false;

        BasicRefEntity that = (BasicRefEntity) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
