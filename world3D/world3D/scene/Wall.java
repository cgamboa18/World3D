package world3D.scene;

public class Wall {
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public boolean portal;
	
	public Wall(float x1, float y1, float x2, float y2, boolean portal) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		this.portal = portal;
		
	}
	
}
