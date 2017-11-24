package inputs;

import entities.Camera;
import entities.Entity;
import entities.extensions.Selectable;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class SelectableDetector {
        private final Vector3f max;

        private final Vector3f min;

        private final Vector2f nearFar;


        public SelectableDetector() {
            min = new Vector3f();
            max = new Vector3f();
            nearFar = new Vector2f();
        }

        public void selectGameItem(List<Selectable> selectables, Camera camera, Vector3f ray) {
            selectGameItem(selectables, camera.getPosition(), ray);
        }

        protected boolean selectGameItem(List<Selectable> selectables, Vector3f center, Vector3f ray) {
            boolean selected = false;
            Selectable selectedEntity = null;
            float closestDistance = Float.POSITIVE_INFINITY;

            for (Selectable entity : selectables) {
                entity.setSelected(false);
                min.set(entity.getPosition());
                max.set(entity.getPosition());
                min.add(-entity.getScale(), -entity.getScale(), -entity.getScale());
                max.add(entity.getScale(), entity.getScale(), entity.getScale());
                if (Intersectionf.intersectRayAab(center, ray, min, max, nearFar) && nearFar.x < closestDistance) {
                    closestDistance = nearFar.x;
                    selectedEntity = entity;
                }
            }

            if (selectedEntity != null) {
                selectedEntity.setSelected(true);
                selected = true;
                System.out.println(selectedEntity.getEntityDescription());
            }
            return selected;
        }
}