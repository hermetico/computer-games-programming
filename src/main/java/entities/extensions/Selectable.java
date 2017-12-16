package entities.extensions;

import entities.Entity;
import physics.AABB;
import org.joml.Vector3f;

public interface Selectable {
    Boolean getSelected();
    Boolean getDebugSelected();

    void setSelected(Boolean selected);
    void setDebugSelected(Boolean selected);

    AABB getAABB();
    Entity getEntity();


}
