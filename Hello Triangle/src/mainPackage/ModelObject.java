package mainPackage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ModelObject {
	private LinkedList<Vertice> vertexList;
	private LinkedList<ObjectFace> faceList;
	private LinkedList<FaceNormal> normalList;
	
	public ModelObject() {
		vertexList = new LinkedList<Vertice>();
		faceList = new LinkedList<ObjectFace>();
		normalList = new LinkedList<FaceNormal>();
	}

	public void addVertex(Vertice vertex) {
		vertexList.add(vertex);
	}

	public void addFace(ObjectFace face) {
		faceList.add(face);
	}

	public float[] getTriangleData() {
		LinkedList<Float> triangleData = new LinkedList<Float>();
		
		for(int i = 0; i <vertexList.size(); ++i) {
			Vertice viq = vertexList.get(i);
			ArrayList<float[]> normals = new ArrayList<float[]>();
			
			for (ObjectFace face : faceList) {
				if(face.usesVert(i)) {
					FaceNormal fniq = normalList.get(face.getNormalIndex());
					float[] anNormal = {fniq.getX(),fniq.getY(),fniq.getZ()};
					normals.add(anNormal);
				}
			}
			float[] netNormal = new float[3];
			for(float[] toAvg : normals) {
				netNormal[0] += toAvg[0];
				netNormal[1] += toAvg[1];
				netNormal[2] += toAvg[2];
			}
			netNormal[0] /= normals.size();
			netNormal[1] /= normals.size();
			netNormal[2] /= normals.size();
			
			triangleData.add(viq.getX());
			triangleData.add(viq.getY());
			triangleData.add(viq.getZ());
			triangleData.add(netNormal[0]);
			triangleData.add(netNormal[1]);
			triangleData.add(netNormal[2]);
			
		}
		float[] finalData = new float[triangleData.size()];
		for (int i = 0; i < triangleData.size(); i++) {
			finalData[i] = triangleData.get(i);
			//System.out.println(finalData[i]);
		}
		/*
		LinkedList<Float> vertexData = new LinkedList<Float>();
		Iterator vertexTraverser = vertexList.iterator();
		while(vertexTraverser.hasNext()) {
			Vertice v = (Vertice)vertexTraverser.next();
			vertexData.add(v.getX());
			vertexData.add(v.getY());
			vertexData.add(v.getZ());
		}
		float[] finalData = new float[vertexData.size()];
		
		for (int i = 0; i < vertexData.size(); i++) {
			finalData[i] = vertexData.get(i);
			//System.out.println(finalData[i]);
		}
		*/
		return finalData;
	}

	public int[] getIndexData() {
		int[] faceIndices = new int[faceList.size()*3];
		for (int i = 0; i <faceList.size(); i++) {
			ObjectFace face = faceList.get(i);
			faceIndices[(i*3)] = face.getFirst();
			faceIndices[(i*3)+1] = face.getSecond();
			faceIndices[(i*3)+2] = face.getThird();

		}
		/*
		for (int i : faceIndices) {
			System.out.println(i);
		}*/
		return faceIndices;
	}

	public void addNormal(FaceNormal fn) {
		normalList.add(fn);
		
	}

}
