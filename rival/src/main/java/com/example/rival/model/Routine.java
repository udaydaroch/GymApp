package com.example.rival.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routines")
public class Routine implements Prototype<Routine> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    // true = other users can see and copy it
    @Column(name = "is_shared")
    private boolean shared;

    private int timesCopied;

    // The routine this one was copied from, if any
    @ManyToOne
    @JoinColumn(name = "copied_from_id")
    private Routine copiedFrom;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExercise> items = new ArrayList<>();

    public Routine() {
    }

    public Routine(String name, String description, AppUser owner, boolean shared) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.shared = shared;
    }

    public void addItem(RoutineExercise item) {
        item.setRoutine(this);
        items.add(item);
    }

    // A new routine with the same name, description and exercises.
    // The copy starts private, with no copies of its own, and remembers its original.
    // Each exercise line is copied too (a deep copy), so editing one routine
    // can never change the other. The caller sets the new owner.
    @Override
    public Routine copy() {
        Routine copy = new Routine(name, description, owner, false);
        copy.setCopiedFrom(this);
        for (RoutineExercise item : items) {
            copy.addItem(item.copy());
        }
        return copy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AppUser getOwner() { return owner; }
    public void setOwner(AppUser owner) { this.owner = owner; }

    public boolean isShared() { return shared; }
    public void setShared(boolean shared) { this.shared = shared; }

    public int getTimesCopied() { return timesCopied; }
    public void setTimesCopied(int timesCopied) { this.timesCopied = timesCopied; }

    public Routine getCopiedFrom() { return copiedFrom; }
    public void setCopiedFrom(Routine copiedFrom) { this.copiedFrom = copiedFrom; }

    public List<RoutineExercise> getItems() { return items; }
    public void setItems(List<RoutineExercise> items) { this.items = items; }
}
