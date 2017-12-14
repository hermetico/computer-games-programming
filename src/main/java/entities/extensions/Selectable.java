package entities.extensions;

import physics.AABB;
import org.joml.Vector3f;

public interface Selectable {
    Boolean getSelected();
    Boolean getDebugSelected();
    void setSelected(Boolean selected);
    void setDebugSelected(Boolean selected);
    Vector3f getBoxPosition();
    Vector3f getBoxScale();
    String getEntityDescription();
    float getScale();
    AABB getAABB();


}
