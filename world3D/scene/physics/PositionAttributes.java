package world3D.scene.physics;

public class PositionAttributes {
	public float xPos, yPos, zPos, xDir, yDir, xPlane, yPlane;
	public float xVel, yVel, zVel, xAcc, yAcc, zAcc;
	
	public PositionAttributes(float x, float y, float z, float xd, float yd, float xp, float yp) {
		xPos = x;
		yPos = y;
		zPos = z;
		xDir = xd;
		yDir = yd;
		xPlane = xp;
		yPlane = yp;
		xVel = 0;
		yVel = 0;
		xAcc = 0;
		yAcc = 0;
	}
	
	public void accelerateXY(float magnitude) {
		xAcc += magnitude;
		yAcc += magnitude;
	}
	
	public void accelerateZ(float magnitude) {
		zAcc += magnitude;
	}
	
}
