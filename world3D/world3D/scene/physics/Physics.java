package world3D.scene.physics;

import world3D.scene.Scene;

public class Physics {

	public static final float FRICTION = 1.15f;

	public static void updatePositionAttributes(PositionAttributes pa, Scene world) {
		//System.out.println(pa.xVel + " " + pa.yVel + " " + pa.zVel);

		// Update velocity based off of acceleration
		pa.xVel += pa.xAcc; //pa.xDir * 
		pa.yVel += pa.yAcc;
		pa.zVel += pa.zAcc;

		float totalVel = (float) Math.sqrt(Math.pow(pa.xVel, 2) + Math.pow(pa.yVel, 2));

		// Apply friction
		if (totalVel > 0) {
			pa.xVel /= FRICTION;
			pa.yVel /= FRICTION;

			if (totalVel < 0.0001) {
				pa.xVel = 0;
				pa.yVel = 0;
			}
		}

		// Collision
		if (null != world.getSector(pa.xPos + pa.xVel, pa.yPos)  && 
				-world.getSector(pa.xPos + pa.xVel, pa.yPos).getFloorHeight() + 2 <= pa.zPos &&
				world.getSector(pa.xPos + pa.xVel, pa.yPos).getCeilingHeight() >= pa.zPos) {
			pa.xPos += pa.xVel;
		} else {
			pa.xVel = -pa.xVel;
		}
		if (null != world.getSector(pa.xPos, pa.yPos + pa.yVel) && 
				-world.getSector(pa.xPos, pa.yPos + pa.yVel).getFloorHeight() + 2 <= pa.zPos &&
				world.getSector(pa.xPos, pa.yPos + pa.yVel).getCeilingHeight() >= pa.zPos) {
			pa.yPos += pa.yVel;
		} else {
			pa.yVel = -pa.yVel;
		}
		if (-world.getSector(pa.xPos, pa.yPos).getFloorHeight() + 2 < pa.zPos + pa.zVel &&
				world.getSector(pa.xPos, pa.yPos).getCeilingHeight() > pa.zPos + pa.zVel) {
			pa.zPos += pa.zVel;
		} else {
			pa.zVel = 0;
		}

		// Reset acceleration
		pa.xAcc = 0;
		pa.yAcc = 0;
		pa.zAcc = -0.05f;
	}
}
