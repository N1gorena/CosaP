package mainPackage;

import java.util.Collection;

public class FaceNormal {
	
	private float[] dirVector = new float[3];
	
	public FaceNormal(float parseFloat, float parseFloat2, float parseFloat3) {
		dirVector[0] = parseFloat;
		dirVector[1] = parseFloat2;
		dirVector[2] = parseFloat3;
	}

	public float getX() {
		return dirVector[0];
	}
	public float getY() {
		return dirVector[1];
	}
	public float getZ() {
		return dirVector[2];
	}
	

}
