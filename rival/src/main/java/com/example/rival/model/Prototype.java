package com.example.rival.model;

// PROTOTYPE PATTERN: an object that knows how to make a copy of itself.
// The code asking for a copy doesn't need to know which fields exist or
// how nested objects are copied; that knowledge stays inside the class.
public interface Prototype<T> {

    // Returns a new, unsaved copy (no id), independent of the original
    T copy();
}
