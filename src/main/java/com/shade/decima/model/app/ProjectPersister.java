package com.shade.decima.model.app;

import com.shade.decima.model.packfile.resource.Resource;
import com.shade.decima.model.util.NotNull;
import com.shade.decima.ui.navigator.NavigatorNode;
import com.shade.decima.ui.navigator.impl.NavigatorFileNode;

import java.util.HashMap;
import java.util.Map;

public class ProjectPersister {
    private final Map<NavigatorFileNode, Resource> changes;

    public ProjectPersister() {
        this.changes = new HashMap<>();
    }

    @NotNull
    public Map<NavigatorFileNode, Resource> getChanges() {
        return changes;
    }

    public void addChange(@NotNull NavigatorFileNode node, @NotNull Resource resource) {
        changes.put(node, resource);
    }

    public void clearChanges() {
        changes.clear();
    }

    public void removeChange(@NotNull NavigatorFileNode node) {
        changes.remove(node);
    }

    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    public boolean hasChangesInPath(@NotNull NavigatorNode node) {
        if (changes.isEmpty()) {
            return false;
        }

        for (NavigatorFileNode changedNode : changes.keySet()) {
            for (NavigatorNode currentNode = changedNode; currentNode != null; currentNode = currentNode.getParent()) {
                if (currentNode == node) {
                    return true;
                }
            }
        }

        return false;
    }
}
