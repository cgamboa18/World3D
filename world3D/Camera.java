package world3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import world3D.scene.Scene;
import world3D.scene.physics.Physics;
import world3D.scene.physics.PositionAttributes;

public class Camera implements KeyListener {
	public PositionAttributes positionAttributes;
	public boolean left, right, forward, back, jump;
	public final float ROTATION_SPEED = 0.045f;

	public Camera(float x, float y, float z, float xd, float yd, float xp, float yp) {
		positionAttributes = new PositionAttributes(x, y, z, xd, yd, xp, yp);
	}

	public void keyPressed(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			left = true;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			right = true;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			forward = true;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			back = true;
		if ((key.getKeyCode() == KeyEvent.VK_SPACE))
			jump = true;
	}

	public void keyReleased(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			left = false;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			right = false;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			forward = false;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			back = false;
		if ((key.getKeyCode() == KeyEvent.VK_SPACE))
			jump = false;
	}

	public void update(Scene world) {
		// Movement + Collision
		if (forward) {
			positionAttributes.accelerateXY(0.05f);
		}
		if (back) {		
			positionAttributes.accelerateXY(-0.05f);
		}
		// Looking LRs
		if (right) {
			float oldxDir = positionAttributes.xDir;
			positionAttributes.xDir = (float) (positionAttributes.xDir * Math.cos(-ROTATION_SPEED) - positionAttributes.yDir * Math.sin(-ROTATION_SPEED));
			positionAttributes.yDir = (float) (oldxDir * Math.sin(-ROTATION_SPEED) + positionAttributes.yDir * Math.cos(-ROTATION_SPEED));
			float oldxPlane = positionAttributes.xPlane;
			positionAttributes.xPlane = (float) (positionAttributes.xPlane * Math.cos(-ROTATION_SPEED) - positionAttributes.yPlane * Math.sin(-ROTATION_SPEED));
			positionAttributes.yPlane = (float) (oldxPlane * Math.sin(-ROTATION_SPEED) + positionAttributes.yPlane * Math.cos(-ROTATION_SPEED));
		}
		if (left) {
			float oldxDir = positionAttributes.xDir;
			positionAttributes.xDir = (float) (positionAttributes.xDir * Math.cos(ROTATION_SPEED) - positionAttributes.yDir * Math.sin(ROTATION_SPEED));
			positionAttributes.yDir = (float) (oldxDir * Math.sin(ROTATION_SPEED) + positionAttributes.yDir * Math.cos(ROTATION_SPEED));
			float oldxPlane = positionAttributes.xPlane;
			positionAttributes.xPlane = (float) (positionAttributes.xPlane * Math.cos(ROTATION_SPEED) - positionAttributes.yPlane * Math.sin(ROTATION_SPEED));
			positionAttributes.yPlane = (float) (oldxPlane * Math.sin(ROTATION_SPEED) + positionAttributes.yPlane * Math.cos(ROTATION_SPEED));
		}
		// Jump
		if (jump) {
			positionAttributes.accelerateZ(0.2f);
		}
		
		Physics.updatePositionAttributes(positionAttributes, world);
	}

	public void keyTyped(KeyEvent e) {

	}
}