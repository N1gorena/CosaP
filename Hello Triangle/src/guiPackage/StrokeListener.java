package guiPackage;

import java.nio.FloatBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StrokeListener implements Listener,KeyListener,MouseMoveListener, MouseListener{
	
	private Matrix4f positionMatrix;
	private Vector3f camStartPos = new Vector3f(0.0f,0.0f,5.0f);
	private Vector3f camHeading = new Vector3f(0.0f,0.0f,-1.0f);
	private Vector3f camUp = new Vector3f(0.0f,1.0f,0.0f);
	private Vector3f t = new Vector3f();
	private boolean[] keysDepressed = new boolean[512];
	private boolean mouseDown = false;
	private boolean lockBase = false;
	private float genX = 0,genY = 0;
	private float yaw = -90.0f;
	private float pitch = 0.0f;
	
	private Vector3f todoDel = new Vector3f(0.0f,0.0f,0.0f);
	
	public void setPositionMatrix() {
		this.positionMatrix = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f).lookAt(camStartPos,todoDel,camUp);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keysDepressed[e.keyCode] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDepressed[e.keyCode] = false;
		
	}

	public void fillBuffer(FloatBuffer camMatrixBuffer, int tick) {
		int deg = tick%360;
		
		double x = 5*Math.cos(Math.toRadians(deg));
		double z = 5*Math.sin(Math.toRadians(deg));
		
		Vector3f dirVec = new Vector3f();
		camHeading.mul(0.05f, dirVec);
		
		Vector3f rightVec = new Vector3f();
		camHeading.cross(camUp, rightVec);
		
		rightVec.normalize();
		rightVec.mul(0.05f);
		
		/*
		//Unicode W
		if(keysDepressed[119]) {
			camStartPos.add(dirVec);
		}//S
		if(keysDepressed[115]) {
			camStartPos.sub(dirVec);
		}//A
		if(keysDepressed[97]) {
			camStartPos.sub(rightVec);
		}//D
		if(keysDepressed[100]) {
			camStartPos.add(rightVec);
		}
		*/
		//camStartPos.add(camHeading, t);
		camStartPos.x = (float) x;
		camStartPos.z = (float) z;
		
		this.positionMatrix = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f).lookAt(camStartPos,t,camUp);
		
		positionMatrix.get(camMatrixBuffer);
		
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if(mouseDown && !lockBase) {
			genX = e.x;
			genY = e.y;
			//lockBase = true;
		}
		else if(mouseDown && lockBase) {
			float deltaX = e.x - genX;
			float deltaY = e.y - genY;
			genX = e.x;
			genY = e.y;
			
			float temperVal = 0.05f;
			deltaX *= temperVal;
			deltaY *= temperVal;
			
			yaw += deltaX;
			pitch+= deltaY;
			
			if (pitch > 89.0f) {
		        pitch = 89.0f;
			}
		    if (pitch < -89.0f) {
		        pitch = -89.0f;
		    }
		    
		    Vector3f newHeading = new Vector3f();
		    newHeading.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		    newHeading.y = (float) Math.sin(Math.toRadians(pitch));
		    newHeading.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		    
		    camHeading = newHeading.normalize();
		}
		
		
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if(e.button == 1) {
			this.mouseDown = true;
		}
		
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if(e.button == 1) {
			this.mouseDown = false;
			this.lockBase = false;
		}
		
	}

	@Override
	public void handleEvent(Event event) {
		keysDepressed[event.keyCode] = event.type == SWT.KeyDown ? true : false;
		
	}
	
	
}

