package world3D.scene;

import java.util.ArrayList;

public class Scene {

	private ArrayList<Sector> sectors;

	public Scene() {
		sectors = new ArrayList<Sector>();

		Sector s = new Sector(2, 3);
		
		s.defineSectorWall(-6, 9, 6, 9, false);
		s.defineSectorWall(6, 9, 9, 6, false);
		s.defineSectorWall(9, 6, 9, -6, false);
		s.defineSectorWall(9, -6, 6, -9, false);
		s.defineSectorWall(6, -9, -6, -9, false);
		s.defineSectorWall(-6, -9, -9, -6, false);
		s.defineSectorWall(-9, -6, -9, 6, false);
		s.defineSectorWall(-9, 6, -6, 9, false);
		
		s.defineSectorWall(-3, 3, 3, 3, true);
		s.defineSectorWall(3, 3, 3, -3, true);
		s.defineSectorWall(3, -3, -3, -3, true);
		s.defineSectorWall(-3, -3, -3, 3, true);
		
		Sector s2 = new Sector(6, 2);
		s2.defineSectorWall(-1.5f, 3, 3, 3, true);
		s2.defineSectorWall(3, 3, 3, -3, true);
		s2.defineSectorWall(3, -3, -3, -3, true);
		s2.defineSectorWall(-3, -3, -3, 1.5f, true);
		s2.defineSectorWall(-3, 1.5f, -1.5f, 1.5f, false);
		s2.defineSectorWall(-1.5f, 1.5f, -1.5f, 3, false);

		sectors.add(s);
		sectors.add(s2);
	}
	// returns the sector a point is located in in
	public Sector getSector(float xp, float yp) {
		int count = 0;
		for (int sectorIndex = 0; sectorIndex < sectors.size(); sectorIndex++) {
			for (int wallIndex = 0; wallIndex < sectors.get(sectorIndex).getWalls().size(); wallIndex++) {
				
				float x1 = sectors.get(sectorIndex).getWalls().get(wallIndex).x1;
				float y1 = sectors.get(sectorIndex).getWalls().get(wallIndex).y1;
				float x2 = sectors.get(sectorIndex).getWalls().get(wallIndex).x2;
				float y2 = sectors.get(sectorIndex).getWalls().get(wallIndex).y2;
				
				if ((yp < y1) != (yp < y2) && xp < x1 + ((yp - y1) / (y2 - y1)) * (x2 - x1)) { 
					count++;
				}
			}
			
			if (count % 2 == 1) {
				return sectors.get(sectorIndex);
			} else {
				count = 0;
			}
		}
		
		return null;
	}
	// Checks to see if a coordinate is within a sector
	public boolean pointInBounds(float xp, float yp) {
		int count = 0;
		for (int sectorIndex = 0; sectorIndex < sectors.size(); sectorIndex++) {
			for (int wallIndex = 0; wallIndex < sectors.get(sectorIndex).getWalls().size(); wallIndex++) {
				
				if(sectors.get(sectorIndex).getWalls().get(wallIndex).portal) {
					continue;
				}
				
				float x1 = sectors.get(sectorIndex).getWalls().get(wallIndex).x1;
				float y1 = sectors.get(sectorIndex).getWalls().get(wallIndex).y1;
				float x2 = sectors.get(sectorIndex).getWalls().get(wallIndex).x2;
				float y2 = sectors.get(sectorIndex).getWalls().get(wallIndex).y2;

				if ((yp < y1) != (yp < y2) && xp < x1 + ((yp - y1) / (y2 - y1)) * (x2 - x1)) {
					count++;
				}
			}
		}

		if (count % 2 == 1) {
			return true;
		} else {
			return false;
		}
	}
	// Returns the sector closest to a point
	public Sector getNearestSector(float xp, float yp) {
		Sector closest = new Sector(0,0);
		float minDist = 1000;
		for (int sectorIndex = 0; sectorIndex < sectors.size(); sectorIndex++) {
			for (int wallIndex = 0; wallIndex < sectors.get(sectorIndex).getWalls().size(); wallIndex++) {

				float x1 = sectors.get(sectorIndex).getWalls().get(wallIndex).x1;
				float y1 = sectors.get(sectorIndex).getWalls().get(wallIndex).y1;
				float x2 = sectors.get(sectorIndex).getWalls().get(wallIndex).x2;
				float y2 = sectors.get(sectorIndex).getWalls().get(wallIndex).y2;

		        float px=x2-x1;
		        float py=y2-y1;
		        float temp=(px*px)+(py*py);
		        float u=((xp - x1) * px + (yp - y1) * py) / (temp);
		        if(u>1){
		            u=1;
		        }
		        else if(u<0){
		            u=0;
		        }
		        float x = x1 + u * px;
		        float y = y1 + u * py;

		        float dx = x - xp;
		        float dy = y - yp;
		        float dist = (float) Math.sqrt(dx*dx + dy*dy);
		        
		        if (dist < minDist) {
		        	closest = sectors.get(sectorIndex);
		        	minDist = dist;
		        }

			}
		}

		return closest;
	}
}
