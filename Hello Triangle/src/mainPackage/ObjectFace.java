package mainPackage;

public class ObjectFace {

	private int[] faceVertices;
	private int faceNormalIndex = -1;
	private int FIRST = 0;
	private int SECOND = 1;
	private int THIRD = 2;
	
	public ObjectFace() {
		faceVertices = new int[3];
	}

	public ObjectFace(int[] faceValues) {
			faceVertices = faceValues;
	}

	public int getFirst() {
		// TODO Auto-generated method stub
		return faceVertices[FIRST];
	}

	public int getSecond() {
		// TODO Auto-generated method stub
		return faceVertices[SECOND];
	}

	public int getThird() {
		// TODO Auto-generated method stub
		return faceVertices[THIRD];
	}

	public void setNormalIndex(int normalNumber) {
		faceNormalIndex = normalNumber;
		
	}

	public int getNormalIndex() {
		
		return faceNormalIndex;
	}

	public boolean usesVert(int i) {
		if(faceVertices[FIRST] == i || faceVertices[SECOND] == i || faceVertices[THIRD] == i)
			return true;
		else
			return false;
	}

}
