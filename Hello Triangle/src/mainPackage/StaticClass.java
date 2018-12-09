package mainPackage;
import org.apache.commons.io.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.JSONObject;
import org.lwjgl.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;


import guiPackage.StrokeListener;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.opengl.GL11.*;



public class StaticClass {
	
	//Website string. The location of the Ubuntu Server at runtime. Using ifconfig address.
	private static final String website = "http://192.168.1.166/";
	//A List of the names of the files downloaded from the Server
	private static LinkedList<String> modelFiles = new LinkedList<String>();
	//Global array used by a model to pass data to Shaders.
	private static int indices[] = {  // note that we start from 0!
		    0, 1, 3,   // first triangle
		    1, 2, 3    // second triangle
		};
	//Global variable that determines the color of the model being viewed.
	private static Vector3f objectColor = new Vector3f(0.0f,1.0f,0.0f);
	//Current Model. The model that is inProgress.
	private static ModelObject inPro = new ModelObject();
	//Timer variables to update camera at 60fps
	private static Timer tickTime = new Timer("Timer");
	private static int tick = 0;
	private static boolean tickIsGo = true;
	
	//VBO, VAO, EBO
	private static int perVertexData;
	private static int VAO;
	private static int indexOrderData;

	//Main method.
	public static void main(String[] args) {
		System.out.println("Here We go...");
		//MODEL LOADING
		try {
			//Connect to homebrew server. Download all models available. Use POST
			URL serverPage = new URL("http://192.168.1.166/modelDump.php");
			HttpURLConnection activeConnection = (HttpURLConnection) serverPage.openConnection();
			activeConnection.setRequestMethod("POST");
			activeConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			activeConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			//Post the password.
			String postParams = "password=alpastordetrack719";
			
			activeConnection.setDoOutput(true);
			DataOutputStream outboundStream = new DataOutputStream(activeConnection.getOutputStream());
			outboundStream.writeBytes(postParams);
			outboundStream.flush();
			outboundStream.close();
			
			//Take in response.
			int responseCode = activeConnection.getResponseCode();
			System.out.println(responseCode);
			//Read incoming response. Should be a JSON string.
			BufferedReader incomingStream = new BufferedReader(new InputStreamReader(activeConnection.getInputStream()));
			String incomingString;
			//Prep JSON string
			incomingString = incomingStream.readLine();
			
			//Parse server JSON string with model file locations.
			JSONObject json = new JSONObject(incomingString);
			//For every key, get the model name without extension, and use it with the Server location to download file.
			Iterator<String> keyIt = json.keys();
			while(keyIt.hasNext()) {
				String daKey = (String) json.get(keyIt.next());
				String[] nameSansFolder = daKey.split("/");
				
				
				StringBuilder stringMaker = new StringBuilder();
				stringMaker.append(website);
				stringMaker.append(daKey);
				
				URL resourceLoc = new URL(stringMaker.toString());
				File tmpFileLoc = new File("tmp\\"+nameSansFolder[1]);
				FileUtils.copyURLToFile(resourceLoc, tmpFileLoc);
				modelFiles.add(nameSansFolder[1]);
			}
		} catch (MalformedURLException e1) {		
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Set up SWT window/
		Display mainDisplay = new Display();
		Shell mainShell = new Shell(mainDisplay);
		
		//Set layout for main window.
		mainShell.setLayout(new GridLayout(5,false));
		//Spacing label
		Label placeholder = new Label(mainShell,SWT.HORIZONTAL);
		GridData labelPlace = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false,1,1);
		placeholder.setLayoutData(labelPlace);
		//Color Control
		Scale rScale = new Scale(mainShell,SWT.VERTICAL);
		rScale.setMaximum(255);
		rScale.setMinimum(0);
		rScale.setIncrement(1);
		rScale.setSelection(255);
		rScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int rVal = rScale.getMaximum() - rScale.getSelection();
				
				objectColor.x = rVal/255.0f;
				
			}
		});
		GridData rPlace = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false,1,1);
		rScale.setLayoutData(rPlace);
		
		Scale gScale = new Scale(mainShell,SWT.VERTICAL);
		gScale.setMaximum(255);
		gScale.setMinimum(0);
		gScale.setIncrement(1);
		gScale.setSelection(0);
		gScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int gVal = gScale.getMaximum() - gScale.getSelection();
				objectColor.y = gVal/255.0f;
			}
		});
		GridData gPlace = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false,1,1);
		gScale.setLayoutData(gPlace);
		
		Scale bScale = new Scale(mainShell,SWT.VERTICAL);
		bScale.setMaximum(255);
		bScale.setMinimum(0);
		bScale.setIncrement(1);
		bScale.setSelection(255);
		bScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int bVal = bScale.getMaximum() - bScale.getSelection();
				objectColor.z = bVal/255.0f;
			}
		});
		GridData bPlace = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false,1,1);
		bScale.setLayoutData(bPlace);
		//Model selector
		Combo comboBox = new Combo(mainShell,SWT.DROP_DOWN);
		for(int i = 0; i < modelFiles.size() ; ++i) {
			String fileName = modelFiles.get(i);
			String[] mFileSansExt = fileName.split("\\.");
			//System.out.println(mFileSansExt.length);
			comboBox.add(mFileSansExt[0],i);
		}
		comboBox.setText("sphere");
		//Everytime a new object is selected, change model inProgress and parse the correct file for model data.
		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				File selectedFile = new File("tmp\\"+modelFiles.get(comboBox.getSelectionIndex()));
				inPro = new ModelObject();
				
				try {
					Scanner objScanner = new Scanner(selectedFile);
					while(objScanner.hasNext()) {
						String fullToken = objScanner.nextLine();
						String[] brokenToken = fullToken.split(" ");
						switch(brokenToken[0]) {
							case "#": 
								break;
							case "o": //System.out.println("Object"+brokenToken[1]);
								break;
							case "v": Vertice vertex = new Vertice(Float.parseFloat(brokenToken[1]),Float.parseFloat(brokenToken[2]),Float.parseFloat(brokenToken[3])); 
								inPro.addVertex(vertex);
								break;
							case "vn": FaceNormal fn = new FaceNormal(Float.parseFloat(brokenToken[1]),Float.parseFloat(brokenToken[2]),Float.parseFloat(brokenToken[3]));
								inPro.addNormal(fn);
								break;
							case "f": 
								int[] faceValues = new int[3];
								int normalNumber = -1;
								for(int i = 1; i <= 3; i++) {
									String[] tempe = brokenToken[i].split("//");
									faceValues[i-1] = Integer.parseInt(tempe[0]) - 1;
									normalNumber = Integer.parseInt(tempe[1])-1;
								}
								ObjectFace face = new ObjectFace(faceValues);
								face.setNormalIndex(normalNumber);
								inPro.addFace(face);
								break;
						}
					}
					objScanner.close();
					
					GL30.glBindVertexArray(VAO);
					
					GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, perVertexData);
					float[] triangleData = inPro.getTriangleData();
					GL30.glBufferData(GL30.GL_ARRAY_BUFFER,triangleData,GL30.GL_STATIC_DRAW);
					
					GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexOrderData);
					indices = inPro.getIndexData();
					GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
					
					GL30.glBindVertexArray(0);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		GridData whereTreeD = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false,1,1);
		comboBox.setLayoutData(whereTreeD);
		//Opengl Canvas
		GLData data = new GLData();
		data.doubleBuffer = true;
		GLCanvas canvas = new GLCanvas(mainShell,0,data);
		canvas.setCurrent();
		GridData placementData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
		canvas.setLayoutData(placementData);
		
		mainShell.pack();
		
		GL.createCapabilities();
		
		//Read in and compile shaders.
		//VERTEX SHADER
		StringBuilder vertSource = new StringBuilder();
		File vShader = new File("Shaders\\VS");
		Scanner shaderHolder;
		try {
			shaderHolder = new Scanner(vShader);
			while(shaderHolder.hasNextLine()) {
				vertSource.append(shaderHolder.nextLine()).append(System.getProperty("line.separator"));
			}
			shaderHolder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//FRAGMENT SHADER
		StringBuilder fragSource = new StringBuilder();
		File fShader = new File("Shaders\\FS");
		Scanner fragHolder;
		try {
			fragHolder = new Scanner(fShader);
			while(fragHolder.hasNextLine()) {
				fragSource.append(fragHolder.nextLine()).append(System.getProperty("line.separator"));
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		int linkedProgram = GL30.glCreateProgram();
		int vertexShaderId = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
		int fragShaderId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
		
		GL30.glShaderSource(vertexShaderId, vertSource.toString());
		GL30.glShaderSource(fragShaderId, fragSource.toString());
		
		GL30.glCompileShader(vertexShaderId);
		GL30.glCompileShader(fragShaderId);
		
		int[] success = new int[1]; 
		
		//Validate the compilation of shaders
		GL30.glGetShaderiv(vertexShaderId, GL30.GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			System.out.println(GL30.glGetShaderInfoLog(vertexShaderId));	
		}
		
		GL30.glGetShaderiv(fragShaderId, GL30.GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			System.out.println(GL30.glGetShaderInfoLog(fragShaderId));
		}
		
		GL30.glAttachShader(linkedProgram, vertexShaderId);
		GL30.glAttachShader(linkedProgram, fragShaderId);
		GL30.glLinkProgram(linkedProgram);
		
		//Validate linking of shaders
		GL30.glGetProgramiv(linkedProgram, GL30.GL_LINK_STATUS, success);
		
		if(success[0] == 0) {
			System.out.println(GL30.glGetProgramInfoLog(linkedProgram));
		}
		
		GL30.glUseProgram(linkedProgram);
		//TODO remove
		//GL30.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		
		//Default model file parse and prep for display.
		File defaultFile = new File("tmp\\"+modelFiles.get(0));
		inPro = new ModelObject();
		try {
			Scanner objScanner = new Scanner(defaultFile);
			while(objScanner.hasNext()) {
				String fullToken = objScanner.nextLine();
				String[] brokenToken = fullToken.split(" ");
				switch(brokenToken[0]) {
					case "#": 
						break;
					case "o": //System.out.println("Object"+brokenToken[1]);
						break;
					case "v": Vertice vertex = new Vertice(Float.parseFloat(brokenToken[1]),Float.parseFloat(brokenToken[2]),Float.parseFloat(brokenToken[3])); 
						inPro.addVertex(vertex);
						break;
					case "vn": FaceNormal fn = new FaceNormal(Float.parseFloat(brokenToken[1]),Float.parseFloat(brokenToken[2]),Float.parseFloat(brokenToken[3]));
						inPro.addNormal(fn);
						break;
					case "f": 
						int[] faceValues = new int[3];
						int normalNumber = -1;
						for(int i = 1; i <= 3; i++) {
							String[] tempe = brokenToken[i].split("//");
							faceValues[i-1] = Integer.parseInt(tempe[0]) - 1;
							normalNumber = Integer.parseInt(tempe[1])-1;
						}
						ObjectFace face = new ObjectFace(faceValues);
						face.setNormalIndex(normalNumber);
						inPro.addFace(face);
						break;
				}
			}
			objScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		float[] triangleData = {
				0.5f,  0.5f, 0.0f,  // top right
			     0.5f, -0.5f, 0.0f,  // bottom right
			    -0.5f, -0.5f, 0.0f,  // bottom left
			    -0.5f,  0.5f, 0.0f   // top left
		};
		
		
		//To get a VBO ID
		IntBuffer hoor = BufferUtils.createIntBuffer(1);
		GL30.glGenBuffers(hoor);
		perVertexData = hoor.get(0);
		//TO get a EBO ID
		IntBuffer hoor2 = BufferUtils.createIntBuffer(1);
		GL30.glGenBuffers(hoor2);
		indexOrderData = hoor2.get(0);
		//VAO ID
		VAO = GL30.glGenVertexArrays();
		//
		GL30.glBindVertexArray(VAO);
		
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, perVertexData);
		triangleData = inPro.getTriangleData();
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER,triangleData,GL30.GL_STATIC_DRAW);
		
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexOrderData);
		indices = inPro.getIndexData();
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
		
		GL30.glVertexAttribPointer(1, 3, GL_FLOAT, false, 6* Float.BYTES, 3);
		GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		GL30.glEnableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
		
		//Listener for all actions.
		StrokeListener mainListener = new StrokeListener();
		mainListener.setPositionMatrix();
		mainDisplay.addFilter(SWT.KeyDown, mainListener);
		mainDisplay.addFilter(SWT.KeyUp, mainListener);
		
		canvas.addMouseMoveListener(mainListener);
		canvas.addMouseListener(mainListener);
		
		FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
		
		Matrix4f modelMatrixData = new Matrix4f().translate(0.0f,0.0f,0.0f);
		
		modelMatrixData.get(modelMatrixBuffer);
		
		FloatBuffer camMatrixBuffer = BufferUtils.createFloatBuffer(16);
		FloatBuffer lightColorBuffer = BufferUtils.createFloatBuffer(3);
		FloatBuffer objectColorBuffer = BufferUtils.createFloatBuffer(3);
		FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(3);
		
		Vector3f lightColor = new Vector3f(1.0f,1.0f,1.0f);
		Vector3f lightPosition = new Vector3f(1.0f,0.0f,0.0f);
		
		lightPosition.get(lightPositionBuffer);
		lightColor.get(lightColorBuffer);
		objectColor.get(objectColorBuffer);
		
		//Schedule 60 hz camera update.
		TimerTask tickGo = new TimerTask() {
			public void run() {
				tickIsGo = true;
			}
		};
		tickTime.scheduleAtFixedRate(tickGo, 17, 17);
		
		mainShell.setSize(1200, 900);
		mainShell.open();
		//RENDERING LOOP
		mainDisplay.asyncExec(new Runnable() {
			public void run() {
				canvas.setCurrent();
				Point canvasSize = canvas.getSize();
				int uniLoc = GL30.glGetUniformLocation(linkedProgram, "cameraMatrix");
				int modelLoc = GL30.glGetUniformLocation(linkedProgram, "modelMatrix");
				int lightColorLocation = GL30.glGetUniformLocation(linkedProgram, "lightColor");
				int objColorLocation = GL30.glGetUniformLocation(linkedProgram, "objectColor");
				int lightPosLocation = GL30.glGetUniformLocation(linkedProgram, "lightPos");
				if(tickIsGo) {
					tick++;
					mainListener.fillBuffer(camMatrixBuffer,tick);
					tickIsGo = false;
				}
				
				objectColor.get(objectColorBuffer);
				
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glEnable(GL_DEPTH_TEST);
				glViewport(0,0,canvasSize.x,canvasSize.y);
				
				GL30.glUseProgram(linkedProgram);
				
				GL30.glBindVertexArray(VAO);
				GL30.glUniform3fv(lightPosLocation, lightPositionBuffer);
				GL30.glUniform3fv(lightColorLocation, lightColorBuffer);
				GL30.glUniform3fv(objColorLocation, objectColorBuffer);
				GL30.glUniformMatrix4fv(modelLoc,false,modelMatrixBuffer);
				GL30.glUniformMatrix4fv(uniLoc,false,camMatrixBuffer);
				GL30.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT,0);
				
				glClearColor(1.0f,0.0f,0.0f,0.0f);
				
				
				
				canvas.swapBuffers();
				mainDisplay.asyncExec(this);
			}
		});
		
		while(!mainShell.isDisposed()) {
			if(!mainDisplay.readAndDispatch()) {
				mainDisplay.sleep();
			}
		}
		/*
		File directoryToClean = new File("tmp");
		try {
			FileUtils.cleanDirectory(directoryToClean);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("And there, that wasnt so bad was it?");
	}

}
