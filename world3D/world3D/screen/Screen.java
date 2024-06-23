package world3D.screen;

import java.util.ArrayList;

import world3D.scene.Scene;
import world3D.scene.entity.Entity;
import world3D.visual3D.Texture;

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
	// Updates the pixels on the screen
	public int[] update(Camera camera, int[] pixels) {
		// Reset background
		clearbg(pixels);

		// Create entity pixel layer
		int triangleRenderPixels[] = renderEntityModels(camera, world.getVisibleEntities(camera));

		for (int x = 0; x < width; x++) {
			boolean intersected = false;
			float resolution = 0.001f;
			float step;
			float cameraX = 2 * x / (float) (width) - 1;
			float rayDirX = camera.positionAttributes.xDir + camera.positionAttributes.xPlane * cameraX;
			float rayDirY = camera.positionAttributes.yDir + camera.positionAttributes.yPlane * cameraX;
			float rayPosX = camera.positionAttributes.xPos;
			float rayPosY = camera.positionAttributes.yPos;

			ArrayList<float[]> portalCoordinates = new ArrayList<float[]>();

			// Get the intersection coordinates of the walls in view
			while (!intersected) {

				if (null != world.getSector(rayPosX + rayDirX * resolution, rayPosY + rayDirY * resolution)) {

					if (world.getSector(rayPosX, rayPosY) != world.getSector(rayPosX + rayDirX * resolution, rayPosY + rayDirY * resolution)) { // TODO:Verify
						float[] coord = { rayPosX, rayPosY };
						portalCoordinates.add(coord);

						step = (float) (world.getNearestDistance(rayPosX, rayPosY)[1] / Math.sqrt(Math.pow(rayDirX, 2) + Math.pow(rayDirY, 2)))
								* 0.99f;
					} else {

						step = (float) (world.getNearestDistance(rayPosX, rayPosY)[0] / Math.sqrt(Math.pow(rayDirX, 2) + Math.pow(rayDirY, 2)))
								* 0.99f;
					}

					if (step < resolution)
						step = resolution;

					rayPosX += rayDirX * step;
					rayPosY += rayDirY * step;
				} else {

					intersected = true;
				}
			}

			// Find distance and height of border walls
			Line line = new Line(width, height);

			line.beta = (float) (Math.abs(width / 2 - x) * (Math.atan(-0.66) / (width / 2)));// Corrects fish eye effect
			line.finalCeiling = world.getSector(rayPosX, rayPosY).getCeilingHeight();
			line.finalFloor = world.getSector(rayPosX, rayPosY).getFloorHeight();
			line.rayIntersectionDistance = (float) Math.sqrt(Math.pow(rayPosX - camera.positionAttributes.xPos, 2)
					+ Math.pow(rayPosY - camera.positionAttributes.yPos, 2));

			drawPortals(x, line, camera, pixels, portalCoordinates);
			drawWall(x, line, camera, pixels);
			drawEntities(x, line, pixels, triangleRenderPixels);
		}

		return pixels;
	}

	// draws walls onto the screen
	private void drawWall(int x, Line line, Camera camera, int[] pixels) {

		if (line.rayIntersectionDistance > 0) {
			line.lineHeight = (float) Math.abs(height / (line.rayIntersectionDistance * (Math.cos(line.beta))));
		} else {
			line.lineHeight = height;
		}
		// Find where pixels should be drawn
		int drawStart = (int) (((-line.lineHeight * (line.finalCeiling - camera.positionAttributes.zPos)) / 2 + height / 2)
				+ (camera.positionAttributes.zDir * height));
		if (drawStart <= 0)
			drawStart = 0;
		int drawEnd = (int) (((line.lineHeight * (line.finalFloor + camera.positionAttributes.zPos)) / 2 + height / 2)
				+ (camera.positionAttributes.zDir * height));
		if (drawEnd >= height)
			drawEnd = height - 1;

		for (int y = drawStart; y < drawEnd; y++) {
			if (y > line.portalUpperLimit || y < line.portalLowerLimit)
				continue;
			// Calculate the color based on rayIntersectionDistance
			int green = Math.max(0, 255 - (int) (line.rayIntersectionDistance * 8));
			int blue = Math.min(255, (int) (line.rayIntersectionDistance * 8));
			// Combine the RGB components into a single color value
			int color = (0 << 16) | (green << 8) | blue;

			pixels[x + y * width] = color;
		}
	}
	// draws 'portals' (intersection between two sectors) onto the screen
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
			
			// find where pixels should be drawn
			int drawStartInner = (int) (((-line.lineHeight * (line.innerCeiling - camera.positionAttributes.zPos)) / 2 + height / 2)
					+ (camera.positionAttributes.zDir * height));
			if (drawStartInner <= 0)
				drawStartInner = 0;
			int drawEndInner = (int) (((line.lineHeight * (line.innerFloor + camera.positionAttributes.zPos)) / 2 + height / 2)
					+ (camera.positionAttributes.zDir * height));
			if (drawEndInner >= height)
				drawEndInner = height - 1;

			int drawStartOuter = (int) (((-line.lineHeight * (line.outerCeiling - camera.positionAttributes.zPos)) / 2 + height / 2)
					+ (camera.positionAttributes.zDir * height));
			if (drawStartOuter <= 0)
				drawStartOuter = 0;
			int drawEndOuter = (int) (((line.lineHeight * (line.outerFloor + camera.positionAttributes.zPos)) / 2 + height / 2)
					+ (camera.positionAttributes.zDir * height));
			if (drawEndOuter >= height)
				drawEndOuter = height - 1;

			// Draw portal pixels
			for (int y = drawStartInner; y < drawEndInner; y++) {
				if (y > line.portalUpperLimit || y < line.portalLowerLimit)
					continue;
				if (y > drawStartOuter && y < drawEndOuter)
					continue;

				// Calculate the color based on rayIntersectionDistance
				int red = Math.max(0, 255 - (int) (line.rayPortalDistance * 8));
				int blue = Math.min(255, (int) (line.rayPortalDistance * 8));

				// Combine the RGB components into a single color value
				int color = (red << 16) | (0 << 8) | blue;

				pixels[x + y * width] = color;
			}

			// limit where walls can be drawn to inside the portal
			if (drawStartInner > drawStartOuter) {
				if (line.portalLowerLimit < drawStartInner)
					line.portalLowerLimit = drawStartInner;
			} else {
				if (line.portalLowerLimit < drawStartOuter)
					line.portalLowerLimit = drawStartOuter;
			}
			if (drawEndInner < drawEndOuter) {
				if (line.portalUpperLimit > drawEndInner)
					line.portalUpperLimit = drawEndInner;
			} else {
				if (line.portalUpperLimit > drawEndOuter)
					line.portalUpperLimit = drawEndOuter;
			}
		}
	}

	// Merge entity pixel layer onto main pixels
	private void drawEntities(int x, Line line, int[] pixels, int[] triangleRenderPixels) {
		for (int y = 0; y < height; y++) {
			if (triangleRenderPixels[x + y * width] != -1) {
				pixels[x + y * width] = triangleRenderPixels[x + y * width];
			}
		}
	}
	
	// Get and convert entity models into triangles, draw onto new layer of pixels
	private int[] renderEntityModels(Camera camera, Entity[] entities) {
		int[] triangleRenderPixels = new int[width * height];
		for (int init = 0; init < width * height; init++) {
			triangleRenderPixels[init] = -1;
		}
		int[][] triangleVerticies2D = new int[3][2];
		float[] cameraPosition = {
				camera.positionAttributes.xPos,
				camera.positionAttributes.yPos,
				camera.positionAttributes.zPos };
		float[] cameraOrientation = {
				camera.positionAttributes.xDir,
				camera.positionAttributes.yDir,
				camera.positionAttributes.zDir };
		for (int entityIndex = 0; entityIndex < entities.length; entityIndex++) {
			if (entities[entityIndex] == null) {
				break;
			}
			for (int triangleIndex = 0; triangleIndex < entities[entityIndex].getModel().length; triangleIndex++) {
				for (int vertexIndex = 0; vertexIndex < 3; vertexIndex++) {
					float[] point2D = convertDimension(
							entities[entityIndex].getModel()[triangleIndex].verticies[vertexIndex],
							cameraPosition,
							cameraOrientation);
					if (point2D != null) {
						int xPixel = (int) point2D[0] + width / 2;
						int yPixel = (int) point2D[1] + height / 2;

						//------
						//if (xPixel > 0 && yPixel > 0 && xPixel < width && yPixel < height)
						//	triangleRenderPixels[xPixel + yPixel * width] = 0xFFFFFF;
						//-----
						triangleVerticies2D[vertexIndex][0] = xPixel;
						triangleVerticies2D[vertexIndex][1] = yPixel;
					}
				}
				int minX = width, minY = height, maxX = 0, maxY = 0;
				for (int i = 0; i < 3; i++) {
					if (triangleVerticies2D[i][0] < minX) {
						minX = triangleVerticies2D[i][0];
					}
					if (triangleVerticies2D[i][1] < minY) {
						minY = triangleVerticies2D[i][1];
					}
					if (triangleVerticies2D[i][0] > maxX) {
						maxX = triangleVerticies2D[i][0];
					}
					if (triangleVerticies2D[i][1] > maxY) {
						maxY = triangleVerticies2D[i][1];
					}
				}
				for (int x = minX; x < maxX; x++) {
					for (int y = minY; y < maxY; y++) {
						if (pointInTriangle(x, y, triangleVerticies2D) &&
							x > 0 && y > 0 && x < width && y < height) {
							triangleRenderPixels[x + y * width] = 0xFFFFFF;
						}
					}
				}
			}
		}
		return triangleRenderPixels;
	}

	// find if 2D point lays within a triangle
	private boolean pointInTriangle(int xp, int yp, int[][] triangleVerticies) {
		float s1 = triangleVerticies[2][1] - triangleVerticies[0][1];
		float s2 = triangleVerticies[2][0] - triangleVerticies[0][0];
		float s3 = triangleVerticies[1][1] - triangleVerticies[0][1];
		float s4 = yp - triangleVerticies[0][1];

		float w1 = (triangleVerticies[0][0] * s1 + s4 * s2 - xp * s1) / (s3 * s2 - (triangleVerticies[1][0] - triangleVerticies[0][0]) * s1);
		float w2 = (s4 - w1 * s3) / s1;

		if (w1 >= 0 && w2 >= 0 && (w1 + w2) <= 1) {
			return true;
		} else {
			return false;
		}

	}

	// Converts a 3D point in space into a 2D point on the screen
	private float[] convertDimension(float[] point3D, float[] cameraPoint, float[] cameraOrientation) {
		// Step 1: Translate the 3D point to the camera's coordinate system
		float[] translatedPoint = new float[3];
		translatedPoint[0] = point3D[0] - cameraPoint[0];
		translatedPoint[1] = point3D[1] - cameraPoint[1];
		translatedPoint[2] = point3D[2] - cameraPoint[2];

		// Step 2: Compute the cameras right and up vectors
		float[] upVector = { 0, 0, -1 };

		// Compute the right vector as the cross product of upVector and
		// cameraOrientation
		float[] rightVector = {
				upVector[1] * cameraOrientation[2] - upVector[2] * cameraOrientation[1],
				upVector[2] * cameraOrientation[0] - upVector[0] * cameraOrientation[2],
				upVector[0] * cameraOrientation[1] - upVector[1] * cameraOrientation[0]
		};

		// Normalize the right vector
		float rightVectorLength = (float) Math
				.sqrt(rightVector[0] * rightVector[0] + rightVector[1] * rightVector[1] + rightVector[2] * rightVector[2]);
		rightVector[0] /= rightVectorLength;
		rightVector[1] /= rightVectorLength;
		rightVector[2] /= rightVectorLength;

		// Compute the adjusted up vector as the cross product of cameraOrientation and
		// rightVector
		float[] adjustedUpVector = {
				cameraOrientation[1] * rightVector[2] - cameraOrientation[2] * rightVector[1],
				cameraOrientation[2] * rightVector[0] - cameraOrientation[0] * rightVector[2],
				cameraOrientation[0] * rightVector[1] - cameraOrientation[1] * rightVector[0]
		};

		// Step 3: Rotate the translated point to align with the camera's view direction
		float[] rotatedPoint = new float[3];
		rotatedPoint[0] = translatedPoint[0] * rightVector[0] + translatedPoint[1] * rightVector[1] + translatedPoint[2] * rightVector[2];
		rotatedPoint[1] = translatedPoint[0] * adjustedUpVector[0] + translatedPoint[1] * adjustedUpVector[1]
				+ translatedPoint[2] * adjustedUpVector[2];
		rotatedPoint[2] = translatedPoint[0] * cameraOrientation[0] + translatedPoint[1] * cameraOrientation[1]
				+ translatedPoint[2] * cameraOrientation[2];

		// Point is behind camera
		if (rotatedPoint[2] <= 0) {
			return null;
		}

		// Step 4: Perspective projection onto a 2D plane
		float focalLength = (float) (height / (2 * Math.tan(Math.toRadians(27))));
		float[] point2D = new float[2];
		point2D[0] = focalLength * rotatedPoint[0] / rotatedPoint[2];
		point2D[1] = focalLength * rotatedPoint[1] / rotatedPoint[2];

		return point2D;
	}

	// Clears background between frames
	public void clearbg(int[] pixels) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height / 2; y++) {
				if (pixels[x + y * width] != (y * 0xFF / height * 2) + 0xFF)
					pixels[x + y * width] = (y * 0xFF / height * 2) + 0xFF;
			}
			for (int y = height / 2; y < height; y++) {
				if (pixels[x + y * width] != ((height - y) * 0xFF / height * 2) + 0xFF)
					pixels[x + y * width] = ((height - y) * 0xFF / height * 2) + 0xFF;
			}
		}
	}
}