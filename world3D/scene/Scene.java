package world3D.scene;

import java.io.IOException;
import java.util.ArrayList;

import world3D.scene.entity.Entity;
import world3D.scene.physics.PositionAttributes;
import world3D.screen.Camera;
import world3D.visual3D.OBJParser;

public class Scene {

	private ArrayList<Sector> sectors;
	private ArrayList<Entity> entities;

	public Scene() {
		sectors = new ArrayList<Sector>();
		entities = new ArrayList<Entity>();

		Sector s = new Sector(2, 6);

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

		Sector s2 = new Sector(8, 3);
		s2.defineSectorWall(-3, 3, 3, 3, true);
		s2.defineSectorWall(3, 3, 3, -3, true);
		s2.defineSectorWall(3, -3, -3, -3, true);
		s2.defineSectorWall(-3, -3, -3, 3, true);

		sectors.add(s);
		sectors.add(s2);
		/*
		Entity e = new Entity();
		e.setPositionAttributes(new PositionAttributes(0, 0, 0, 1, 0, 0, 0, 0));
		
		try {
			e.setModel(OBJParser.parseOBJ("models/cube.obj"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		entities.add(e);
		*/
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

	// Returns the sector closest to a point
	public Wall getNearestWall(float xp, float yp) {
		Wall closest = new Wall(0, 0, 0, 0, false);
		float minDist = Integer.MAX_VALUE;
		for (int sectorIndex = 0; sectorIndex < sectors.size(); sectorIndex++) {
			for (int wallIndex = 0; wallIndex < sectors.get(sectorIndex).getWalls().size(); wallIndex++) {

				float x1 = sectors.get(sectorIndex).getWalls().get(wallIndex).x1;
				float y1 = sectors.get(sectorIndex).getWalls().get(wallIndex).y1;
				float x2 = sectors.get(sectorIndex).getWalls().get(wallIndex).x2;
				float y2 = sectors.get(sectorIndex).getWalls().get(wallIndex).y2;

				float px = x2 - x1;
				float py = y2 - y1;
				float temp = (px * px) + (py * py);
				float u = ((xp - x1) * px + (yp - y1) * py) / (temp);
				if (u > 1) {
					u = 1;
				} else if (u < 0) {
					u = 0;
				}
				float x = x1 + u * px;
				float y = y1 + u * py;

				float dx = x - xp;
				float dy = y - yp;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);

				if (dist < minDist) {
					closest = sectors.get(sectorIndex).getWalls().get(wallIndex);
					minDist = dist;
				}

			}
		}

		return closest;
	}

	// Returns the distance to nearest point on the nearest wall
	public float[] getNearestDistance(float xp, float yp) {
		float minDist2 = Integer.MAX_VALUE;
		float minDist = Integer.MAX_VALUE;
		for (int sectorIndex = 0; sectorIndex < sectors.size(); sectorIndex++) {
			for (int wallIndex = 0; wallIndex < sectors.get(sectorIndex).getWalls().size(); wallIndex++) {

				float x1 = sectors.get(sectorIndex).getWalls().get(wallIndex).x1;
				float y1 = sectors.get(sectorIndex).getWalls().get(wallIndex).y1;
				float x2 = sectors.get(sectorIndex).getWalls().get(wallIndex).x2;
				float y2 = sectors.get(sectorIndex).getWalls().get(wallIndex).y2;

				float px = x2 - x1;
				float py = y2 - y1;
				float temp = (px * px) + (py * py);
				float u = ((xp - x1) * px + (yp - y1) * py) / (temp);
				if (u > 1) {
					u = 1;
				} else if (u < 0) {
					u = 0;
				}
				float x = x1 + u * px;
				float y = y1 + u * py;

				float dx = x - xp;
				float dy = y - yp;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);

				if (dist < minDist) {
					minDist2 = minDist;
					minDist = dist;
				} else if (dist < minDist2 && dist != minDist) {
					minDist2 = dist;
				}

			}
		}

		float distances[] = { minDist, minDist2 };
		return distances;
	}

	// Finds if point is within field of view
	public boolean pointInFov(float xp, float yp, Camera camera) {
		int count = 0;
		
		for (int viewBoundIndex = 0; viewBoundIndex < 3; viewBoundIndex++) {
			
			float x1 = camera.positionAttributes.xPos;
			float y1 = camera.positionAttributes.yPos;
			float x2 = camera.positionAttributes.xPos;
			float y2 = camera.positionAttributes.yPos;
			
			if (viewBoundIndex <= 1) {
				x1 += (camera.positionAttributes.xDir + -camera.positionAttributes.xPlane) * 100;//render dist 100
				y1 += (camera.positionAttributes.yDir + -camera.positionAttributes.yPlane) * 100;
			}
			if (viewBoundIndex >= 1) {
				x2 += (camera.positionAttributes.xDir + camera.positionAttributes.xPlane) * 100;
				y2 += (camera.positionAttributes.yDir + camera.positionAttributes.yPlane) * 100;
			}
			
			if ((yp < y1) != (yp < y2) && xp < x1 + ((yp - y1) / (y2 - y1)) * (x2 - x1)) {
				count++;
			}
		}
		
		if (count % 2 == 1) {
			return true;
		} else {
			count = 0;
		}
		return false;
	}

	// Finds entities that has a position within cameras FOV
	public Entity[] getVisibleEntities(Camera camera) {
		Entity[] visibleEntities = new Entity[entities.size()];
		int visibleEntityCount = 0;

		for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {

			if (entities.get(entityIndex).isVisible() && entities.get(entityIndex).getModel() != null) {
				boolean inBounds = false;
				for (int triangleIndex = 0; triangleIndex < entities.get(entityIndex).getModel().length; triangleIndex++) {
					for (int vertexIndex = 0; vertexIndex <= 2; vertexIndex++) {
						if (pointInFov(entities.get(entityIndex).getModel()[triangleIndex].verticies[vertexIndex][0],
								entities.get(entityIndex).getModel()[triangleIndex].verticies[vertexIndex][1], camera)) {
							inBounds = true;
						}
					}
					// If the entity is found to be visible, stop checking
					if (inBounds) {
						break;
					}
				}
				if (inBounds) {
					visibleEntities[visibleEntityCount] = entities.get(entityIndex);
					visibleEntityCount++;
				}
			}   
		}

		return visibleEntities;
	}
}
