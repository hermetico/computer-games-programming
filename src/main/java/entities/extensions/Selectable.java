package entities.extensions;

import entities.BoundingBox;
import entities.Entity;
import models.RawEntity;
import org.joml.Vector3f;

public interface Selectable {
    Boolean selected = false;

    Boolean getSelected();
    void setSelected(Boolean selected);
    Vector3f getPosition();
    float getScale();
    String getEntityDescription();
    RawEntity getRawEntity();
    BoundingBox getBoundingBox();
    Entity getEntity();

}
