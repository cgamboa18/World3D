package world3D.scene.entity;

import world3D.scene.physics.PositionAttributes;
import world3D.visual3D.Triangle;

public class Entity {
	
	private boolean visible;
	private PositionAttributes positionAttributes;
	private Triangle[] model;

	public Entity() {
		setPositionAttributes(null);
		setModel(null);
		visible = true;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public PositionAttributes getPositionAttributes() {
		return positionAttributes;
	}

	public void setPositionAttributes(PositionAttributes positionAttributes) {
		this.positionAttributes = positionAttributes;
	}

	public Triangle[] getModel() {
		return model;
	}

	public void setModel(Triangle[] model) {
		this.model = model;
	}
}
