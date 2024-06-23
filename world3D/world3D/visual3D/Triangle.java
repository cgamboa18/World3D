package world3D.visual3D;

public class Triangle {
	public float[][] verticies;
	
	public Triangle(float[] v1, float[] v2, float[] v3) {
		verticies = new float[3][3];
		verticies[0] = v1;
		verticies[1] = v2;
		verticies[2] = v3;
	}
}
