package world3D;

public class Line {
	public float beta;// Corrects fish eye effect
	public float lineHeight;
	public float portalLowerLimit;
	public float portalUpperLimit;
	public float outerCeiling; // The height of the sector just after the portal
	public float outerFloor;
	public float innerCeiling; // The height of the sector just before the portal
	public float innerFloor;
	public float finalCeiling;
	public float finalFloor;
	public float rayPortalDistance;
	public float rayIntersectionDistance;
	
	public Line(int width, int height) {
		portalLowerLimit = 0;
		portalUpperLimit = height;
	}
	
	
}
