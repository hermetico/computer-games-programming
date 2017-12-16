package inputs;

import entities.Camera;
import entities.Entity;
import entities.extensions.Selectable;
import org.joml.*;
import physics.AABB;
import utils.Maths;

import java.util.List;

public class SelectableDetector {
        private final Vector2f nearFar;

        public SelectableDetector() {
            nearFar = new Vector2f();
        }

        public void selectGameItem(List<Selectable> selectables, Camera camera, Vector3f ray) {
             selectGameItem(selectables, camera.getPosition(), ray);
        }

        protected boolean selectGameItem(List<Selectable> selectables, Vector3f center, Vector3f ray) {
            boolean selected = false;
            Selectable selectedEntity = null;
            float closestDistance = Float.POSITIVE_INFINITY;


            for (Selectable selectable : selectables) {
                selectable.setSelected(false);

                Entity entity = selectable.getEntity();
                AABB aabb = selectable.getAABB();
                Matrix4f rotationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 0,0,0, entity.getScale());
                
                Vector4f min4 = rotationMatrix.transform(new Vector4f(aabb.getMin(), 1));
                Vector4f max4 = rotationMatrix.transform(new Vector4f(aabb.getMax(), 1));
                
                Vector3f min = new Vector3f(min4.x, min4.y, min4.z);
                Vector3f max = new Vector3f(max4.x, max4.y, max4.z);

                if (Intersectionf.intersectRayAab(center, ray, min, max, nearFar) && nearFar.x < closestDistance) {
                    closestDistance = nearFar.x;
                    selectedEntity = selectable;
                }
            }

            if (selectedEntity != null) {
                selectedEntity.setSelected(true);
                selected = true;
            }
            return selected;
        }

}