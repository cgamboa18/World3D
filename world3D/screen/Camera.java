package world3D.screen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import world3D.scene.Scene;
import world3D.scene.physics.Physics;
import world3D.scene.physics.PositionAttributes;

public class Camera implements KeyListener {
	public PositionAttributes positionAttributes;
	public boolean lookLeft, lookRight, lookUp, lookDown, forward, back, left, right, jump;
	public final float ROTATION_SPEED = 0.045f;

	public Camera(float x, float y, float z, float xd, float yd, float zd, float xp, float yp) {
		positionAttributes = new PositionAttributes(x, y, z, xd, yd, zd, xp, yp);
	}

	public void keyPressed(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			lookLeft = true;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			lookRight = true;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			lookUp = true;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			lookDown = true;
		if ((key.getKeyCode() == KeyEvent.VK_W))
			forward = true;
		if ((key.getKeyCode() == KeyEvent.VK_S))
			back = true;
		if ((key.getKeyCode() == KeyEvent.VK_A))
			left = true;
		if ((key.getKeyCode() == KeyEvent.VK_D))
			right = true;
		if ((key.getKeyCode() == KeyEvent.VK_SPACE))
			jump = true;
	}

	public void keyReleased(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			lookLeft = false;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			lookRight = false;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			lookUp = false;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			lookDown = false;
		if ((key.getKeyCode() == KeyEvent.VK_W))
			forward = false;
		if ((key.getKeyCode() == KeyEvent.VK_S))
			back = false;
		if ((key.getKeyCode() == KeyEvent.VK_A))
			left = false;
		if ((key.getKeyCode() == KeyEvent.VK_D))
			right = false;
		if ((key.getKeyCode() == KeyEvent.VK_SPACE))
			jump = false;
	}

	public void update(Scene world) {
		// Movement + Collision
		if (forward) {
			positionAttributes.accelerateXY(0.05f, false);
		}
		if (back) {
			positionAttributes.accelerateXY(-0.05f, false);
		}
		if (left) {
			positionAttributes.accelerateXY(0.05f, true);
		}
		if (right) {
			positionAttributes.accelerateXY(-0.05f, true);
		}

		// Looking
		if (lookRight) {
			float oldxDir = positionAttributes.xDir;
			positionAttributes.xDir = (float) (positionAttributes.xDir * Math.cos(-ROTATION_SPEED)
					- positionAttributes.yDir * Math.sin(-ROTATION_SPEED));
			positionAttributes.yDir = (float) (oldxDir * Math.sin(-ROTATION_SPEED) + positionAttributes.yDir * Math.cos(-ROTATION_SPEED));
			float oldxPlane = positionAttributes.xPlane;
			positionAttributes.xPlane = (float) (positionAttributes.xPlane * Math.cos(-ROTATION_SPEED)
					- positionAttributes.yPlane * Math.sin(-ROTATION_SPEED));
			positionAttributes.yPlane = (float) (oldxPlane * Math.sin(-ROTATION_SPEED) + positionAttributes.yPlane * Math.cos(-ROTATION_SPEED));
		}
		if (lookLeft) {
			float oldxDir = positionAttributes.xDir;
			positionAttributes.xDir = (float) (positionAttributes.xDir * Math.cos(ROTATION_SPEED)
					- positionAttributes.yDir * Math.sin(ROTATION_SPEED));
			positionAttributes.yDir = (float) (oldxDir * Math.sin(ROTATION_SPEED) + positionAttributes.yDir * Math.cos(ROTATION_SPEED));
			float oldxPlane = positionAttributes.xPlane;
			positionAttributes.xPlane = (float) (positionAttributes.xPlane * Math.cos(ROTATION_SPEED)
					- positionAttributes.yPlane * Math.sin(ROTATION_SPEED));
			positionAttributes.yPlane = (float) (oldxPlane * Math.sin(ROTATION_SPEED) + positionAttributes.yPlane * Math.cos(ROTATION_SPEED));
		}
		if (lookUp) {
			if (positionAttributes.zDir + ROTATION_SPEED <= 1)
				positionAttributes.zDir += ROTATION_SPEED;
		}
		if (lookDown) {
			if (positionAttributes.zDir - ROTATION_SPEED >= -1)
				positionAttributes.zDir -= ROTATION_SPEED;
		}

		// Jump
		if (jump) {
			positionAttributes.accelerateZ(0.2f);
		}

		Physics.updatePositionAttributes(positionAttributes, world);
		//System.out.println(positionAttributes.xPos + " " + positionAttributes.yPos + " " + positionAttributes.zPos);
	}

	public void keyTyped(KeyEvent e) {

	}
}