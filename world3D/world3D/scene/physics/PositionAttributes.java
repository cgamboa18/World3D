package world3D.scene.physics;

public class PositionAttributes {
	public float xPos, yPos, zPos, xDir, yDir, zDir, xPlane, yPlane;
	public float xVel, yVel, zVel, xAcc, yAcc, zAcc;
	
	public PositionAttributes(float x, float y, float z, float xd, float yd, float zd, float xp, float yp) {
		xPos = x;
		yPos = y;
		zPos = z;
		xDir = xd;
		yDir = yd;
		zDir = zd;
		xPlane = xp;
		yPlane = yp;
		xVel = 0;
		yVel = 0;
		xAcc = 0;
		yAcc = 0;
	}
	
	public void accelerateXY(float magnitude, boolean lateral) {
		if(lateral) {
			xAcc += yDir * -magnitude;
			yAcc += xDir * magnitude;
		} else {
			xAcc += xDir * magnitude;
			yAcc += yDir * magnitude;
		}
			
	}
	
	public void accelerateZ(float magnitude) {
		zAcc += magnitude;
	}
	
}
