package entities.extensions;

import entities.BoundingBox;
import entities.Entity;
import models.RawEntity;
import org.joml.Vector3f;

public interface Selectable {
    Boolean getSelected();
    void setSelected(Boolean selected);
    Vector3f getBoxPosition();
    Vector3f getBoxScale();
    String getEntityDescription();
    float getScale();
    BoundingBox getBoundingBox();


}
