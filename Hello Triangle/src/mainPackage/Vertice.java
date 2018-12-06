package mainPackage;

public class Vertice {
	private float[] locationXYZ;
	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;
	
	public Vertice(float parseFloat, float parseFloat2, float parseFloat3) {
		locationXYZ = new float[3];
		locationXYZ[0] = parseFloat;
		locationXYZ[1] = parseFloat2;
		locationXYZ[2] = parseFloat3;
	}

	public void Vertice() {
		locationXYZ = new float[3];
	}

	public Float getX() {
		// TODO Auto-generated method stub
		return locationXYZ[X];
	}

	public Float getY() {
		// TODO Auto-generated method stub
		return locationXYZ[Y];
	}

	public Float getZ() {
		// TODO Auto-generated method stub
		return locationXYZ[Z];
	}

	
	
}
