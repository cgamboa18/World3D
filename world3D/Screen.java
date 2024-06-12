package world3D;

import java.util.ArrayList;

import world3D.scene.Scene;

public class Screen {
	public Scene world;
	public int width, height;
	public ArrayList<Texture> textures;

	public Screen(Scene world, ArrayList<Texture> textures, int width, int height) {
		this.world = world;
		this.textures = textures;
		this.width = width;
		this.height = height;
	}

	public int[] update(Camera camera, int[] pixels) {
		// Reset background
		clearbg(pixels);

		for (int x = 0; x < width; x++) {
			boolean intersected = false;
			float step = 0.1f;
			float cameraX = 2 * x / (float) (width) - 1;
			float rayDirX = camera.positionAttributes.xDir + camera.positionAttributes.xPlane * cameraX;
			float rayDirY = camera.positionAttributes.yDir + camera.positionAttributes.yPlane * cameraX;
			float rayPosX = camera.positionAttributes.xPos;
			float rayPosY = camera.positionAttributes.yPos;

			ArrayList<float[]> portalCoordinates = new ArrayList<float[]>();

			// Get the intersection coordinates of the walls in view
			while (!intersected) {
				
				if (null != world.getSector(rayPosX + rayDirX * step, rayPosY + rayDirY * step)) {
					if (world.getSector(rayPosX, rayPosY) != world.getSector(rayPosX + rayDirX * step, rayPosY + rayDirY * step)) { //TODO: Verify
						
						float[] coord = { rayPosX, rayPosY };
						portalCoordinates.add(coord);
					}
					
					rayPosX += rayDirX * step;
					rayPosY += rayDirY * step;
					
				} else {
					intersected = true;
				}
			}

			// Find distance and height of border walls
			Line line = new Line(width, height);

			line.beta = (float) (Math.abs(width / 2 - x) * (0.58 / (width / 2)));// Corrects fish eye effect
			line.finalCeiling = world.getSector(rayPosX, rayPosY).getCeilingHeight();
			line.finalFloor = world.getSector(rayPosX, rayPosY).getFloorHeight();
			line.rayIntersectionDistance = (float) Math.sqrt(Math.pow(rayPosX - camera.positionAttributes.xPos, 2)
					+ Math.pow(rayPosY - camera.positionAttributes.yPos, 2));

			drawPortals(x, line, camera, pixels, portalCoordinates);
			drawWall(x, line, camera, pixels);
		}

		return pixels;
	}

	private void drawWall(int x, Line line, Camera camera, int[] pixels) {

		if (line.rayIntersectionDistance > 0) {
			line.lineHeight = (float) Math.abs(height / (line.rayIntersectionDistance * (Math.cos(line.beta))));
		} else {
			line.lineHeight = height;
		}

		int drawStart = (int) (-line.lineHeight * (line.finalCeiling - camera.positionAttributes.zPos)) / 2 + height / 2;
		if (drawStart <= 0)
			drawStart = 0;
		int drawEnd = (int) ((line.lineHeight * (line.finalFloor + camera.positionAttributes.zPos)) / 2 + height / 2);
		if (drawEnd >= height)
			drawEnd = height - 1;

		for (int y = drawStart / 2; y < drawEnd; y++) {
		    if (y > line.portalUpperLimit || y < line.portalLowerLimit)
		        continue;
		    
		    // Calculate the color based on rayIntersectionDistance
		    int green = Math.max(0, 255 - (int)(line.rayIntersectionDistance * 8));
		    int blue = Math.min(255, (int)(line.rayIntersectionDistance * 8));

		    // Combine the RGB components into a single color value
		    int color = (0 << 16) | (green << 8) | blue;

		    pixels[x + y * width] = color;
		}
	}

	private void drawPortals(int x, Line line, Camera camera, int[] pixels, ArrayList<float[]> portalCoordinates) {

		for (int portalIndex = 0; portalIndex < portalCoordinates.size(); portalIndex++) {

			// Find the ceiling and floor before and after portal
			if (portalIndex == portalCoordinates.size() - 1) {
				line.outerCeiling = line.finalCeiling;
				line.outerFloor = line.finalFloor;
			} else {
				line.outerCeiling = world.getSector(portalCoordinates.get(portalIndex + 1)[0],
						portalCoordinates.get(portalIndex + 1)[1]).getCeilingHeight();

				line.outerFloor = world.getSector(portalCoordinates.get(portalIndex + 1)[0],
						portalCoordinates.get(portalIndex + 1)[1]).getFloorHeight();
			}

			line.innerCeiling = world.getSector(portalCoordinates.get(portalIndex)[0],
					portalCoordinates.get(portalIndex)[1]).getCeilingHeight();

			line.innerFloor = world.getSector(portalCoordinates.get(portalIndex)[0],
					portalCoordinates.get(portalIndex)[1]).getFloorHeight();

			// Find distance from camera to portal and height of the line
			line.rayPortalDistance = (float) Math.sqrt(Math.pow(portalCoordinates.get(portalIndex)[0] - camera.positionAttributes.xPos, 2)
					+ Math.pow(portalCoordinates.get(portalIndex)[1] - camera.positionAttributes.yPos, 2));

			if (line.rayPortalDistance > 0) {
				line.lineHeight = (float) Math.abs(height / (line.rayPortalDistance * (Math.cos(line.beta))));
			} else {
				line.lineHeight = height;
			}

			int drawStartInner = (int) ((-line.lineHeight * (line.innerCeiling - camera.positionAttributes.zPos)) / 2 + height / 2);
			if (drawStartInner <= 0)
				drawStartInner = 0;
			int drawEndInner = (int) ((line.lineHeight * (line.innerFloor + camera.positionAttributes.zPos)) / 2 + height / 2);
			if (drawEndInner >= height)
				drawEndInner = height - 1;

			int drawStartOuter = (int) ((-line.lineHeight * (line.outerCeiling - camera.positionAttributes.zPos)) / 2 + height / 2);
			if (drawStartOuter <= 0)
				drawStartOuter = 0;
			int drawEndOuter = (int) ((line.lineHeight * (line.outerFloor + camera.positionAttributes.zPos)) / 2 + height / 2);
			if (drawEndOuter >= height)
				drawEndOuter = height - 1;

			// Draw portal pixels
			for (int y = drawStartInner / 2; y < drawEndInner; y++) {
				if (y > line.portalUpperLimit || y < line.portalLowerLimit)
					continue;
				if (y > drawStartOuter / 2 && y < drawEndOuter)
					continue;
				
				// Calculate the color based on rayIntersectionDistance
			    int green = Math.max(0, 255 - (int)(line.rayPortalDistance * 8));
			    int blue = Math.min(255, (int)(line.rayPortalDistance * 8));

			    // Combine the RGB components into a single color value
			    int color = (0 << 16) | (green << 8) | blue;

			    pixels[x + y * width] = color;
			}

			// limit where walls can be drawn to inside the portal
			if (drawStartInner > drawStartOuter) {
				if(line.portalLowerLimit < drawStartInner / 2)
					line.portalLowerLimit = drawStartInner / 2;
			} else {
				if(line.portalLowerLimit < drawStartOuter / 2)
					line.portalLowerLimit = drawStartOuter / 2;
			}
			if (drawEndInner < drawEndOuter) {
				if(line.portalUpperLimit > drawEndInner)
					line.portalUpperLimit = drawEndInner;
			} else {
				if(line.portalUpperLimit > drawEndOuter)
					line.portalUpperLimit = drawEndOuter;
			}
		}
	}
	
	public void clearbg(int [] pixels) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height / 2; y++) {
				if(pixels[x + y * width] != (y * 0xFF / height * 2) + 0xFF)
					pixels[x + y * width] = (y * 0xFF / height * 2) + 0xFF;
			}
			for (int y = height / 2; y < height; y++) {
				if(pixels[x + y * width] != ((height - y) * 0xFF / height * 2) + 0xFF)
					pixels[x + y * width] = ((height - y) * 0xFF / height * 2) + 0xFF;
			}
		}
	}
}