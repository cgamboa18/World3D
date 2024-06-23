package world3D.scene;

import java.util.ArrayList;

public class Sector {

	private ArrayList<Wall> sectorWalls;
	private float floorHeight;
	private float ceilingHeight;

	public Sector(float floorHeight, float ceilingHeight) {
		sectorWalls = new ArrayList<Wall>();
		this.setFloorHeight(floorHeight);
		this.setCeilingHeight(ceilingHeight);
	}

	public void defineSectorWall(float x1, float y1, float x2, float y2, boolean portal) {
		Wall w = new Wall(x1, y1, x2, y2, portal);
		sectorWalls.add(w);
	}
	
	public ArrayList<Wall> getWalls(){
		return sectorWalls;
	}

	public float getFloorHeight() {
		return floorHeight;
	}

	public void setFloorHeight(float floorHeight) {
		this.floorHeight = floorHeight;
	}

	public float getCeilingHeight() {
		return ceilingHeight;
	}

	public void setCeilingHeight(float ceilingHeight) {
		this.ceilingHeight = ceilingHeight;
	}

}
