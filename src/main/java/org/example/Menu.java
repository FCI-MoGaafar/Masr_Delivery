package org.example;

import java.util.*;

public class Menu {
    private final Map<Integer, MenuItem> items = new LinkedHashMap<>();

    public void addItem(MenuItem item) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null");
        if (items.containsKey(item.getId()))
            throw new IllegalArgumentException("Item already exists");

        items.put(item.getId(), item);
    }

    public void removeItem(int id) {

        if (items.remove(id) == null)
            throw new IllegalArgumentException("Item not found");
    }

    public MenuItem getItem(int id) {
        return items.get(id);
    }

    public void changeAvailability(int id, boolean available) {

        MenuItem item = getItem(id);

        if (item == null)
            throw new IllegalArgumentException("Item not found");

        item.setAvailable(available);
    }

    public Collection<MenuItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }
}
