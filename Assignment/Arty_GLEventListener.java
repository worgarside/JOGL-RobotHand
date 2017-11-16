import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Arty_GLEventListener implements GLEventListener {
  
    private static final boolean DISPLAY_SHADERS = false;
    private static final int DIGIT_COUNT = 5;
    private static final int PHALANGE_COUNT = 3;
    private float aspect;
    private char currentASLPos;

    public Arty_GLEventListener(Camera camera) {
    this.camera = camera;
    }

    // ***************************************************
    /*
    * METHODS DEFINED BY GLEventListener
    */

    /* Initialisation */
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        initialise(gl);
        startTime = getSeconds();
    }
  
    /* Called to indicate the drawing surface has been moved and/or resized  */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        aspect = (float)width/(float)height;
    }

    /* Draw */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /* Clean up memory, if necessary */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        disposeMeshes(gl);
    }

    // ***************************************************
    /* TIME
    */

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

      // ***************************************************
      /* An array of random numbers
       */
  
      private int NUM_RANDOMS = 1000;
      private float[] randoms;
  
    private void createRandomNumbers() {
        randoms = new float[NUM_RANDOMS];
        for (int i=0; i<NUM_RANDOMS; ++i) {
            randoms[i] = (float)Math.random();
        }
    }
  
    // ***************************************************
    /* INTERACTION
    *
    *
    */

    private boolean animation = false;
    private double savedTime = 0;

    public void startAnimation() {
        animation = true;
        startTime = getSeconds()-savedTime;
    }

    public void stopAnimation() {
        animation = false;
        double elapsedTime = getSeconds()-startTime;
        savedTime = elapsedTime;
    }

    public void rotPalmXPos() {
        palmXAngle++;
        palmRotateX.setTransform(Mat4Transform.rotateAroundX(palmXAngle));
        palmRotateX.update();
    }

    public void rotPalmXNeg() {
        palmXAngle--;
        palmRotateX.setTransform(Mat4Transform.rotateAroundX(palmXAngle));
        palmRotateX.update();
    }

    public void rotPalmZPos() {
        palmZAngle++;
        palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmZAngle));
        palmRotateZ.update();
    }

    public void rotPalmZNeg() {
        palmZAngle--;
        palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmZAngle));
        palmRotateZ.update();
    }

    public void rotToAngle(int angle) {
        armRotateY.setTransform(Mat4Transform.rotateAroundY(angle));
        armRotateY.update();
    }

    public void asl(char letter){
        switch(letter) {
            case 'W':
                desiredPrmAngle = digitPrmAngleW;
                desiredSecAngle = digitSecAngleW;
                break;
            case 'I':
                desiredPrmAngle = digitPrmAngleI;
                desiredSecAngle = digitSecAngleI;
                break;
            default:
                System.out.println("Invalid ASL Position");
                System.exit(0);
        }

    }

    public void updateAngles(){
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                if (d!=0){
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    phalRotX[d][p].update();
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                    phalRotZ[d][p].update();
                }else{
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                    phalRotZ[d][p].update();
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentSecAngles[d]));
                    phalRotX[d][p].update();
                }
            }

        }
    }

    // ***************************************************
    /* THE SCENE
    * Now define all the methods to handle the scene.
    * This will be added to in later examples.
    */

    private Camera camera;
    private Mat4 perspective;
    private Mesh floor, sphere, cube, cube2;
    private Light light;
    private SGNode robotHand;

    private int palmXAngle, palmZAngle;

    private int[][] maxPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Maximum angle phalange can be (most acute)
    private int[][] minPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Minimum angle phalange can be (most obtuse)
    private int[] maxSecAngle = new int[DIGIT_COUNT];                                       // Maximum angle prox can be (most acute)
    private int[] minSecAngle = new int[DIGIT_COUNT];                                       // Minimum angle prox can be (most obtuse)
    private int[][] angleX = new int[DIGIT_COUNT][PHALANGE_COUNT];                          // Current angle of phalange
//    private boolean[] digitAnim = new boolean[DIGIT_COUNT];                                 // Boolean to check if digit is animating
    private TransformNode[][] phalRotX = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating phalanges about X-axis
    private TransformNode[][] phalRotZ = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating proximal phalanges about Z-axis
//    private int fing0ProxAngleZ;                                                            // Z-angle of fing0 (thumb) proximal phalange
    private TransformNode armRotateY, palmRotateX, palmRotateZ;
    private int[][] currentPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];
    private int[] currentSecAngles = new int[DIGIT_COUNT];

    private int[][] desiredPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];
    private int[] desiredSecAngle = new int[DIGIT_COUNT];

    private int[][] digitPrmAngleW = {
            {90, 85, 25},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {90, 90, 0}
    };

    private int[] digitSecAngleW = {35, -10, 0, 10, 30};

    private int[][] digitPrmAngleI = {
            {90, 90, 0},
            {90, 90, 0},
            {90, 90, 0},
            {90, 90, 0},
            {0, 0, 0}
    };

    private int[] digitSecAngleI = {60, 0, 0, 0, 10};

    private void initialise(GL3 gl) {
        createRandomNumbers();
        int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
        int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
        int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");

        // make meshes
        floor = new TwoTriangles(gl, textureId0);
        floor.setModelMatrix(Mat4Transform.scale(16,1,16));
        sphere = new Sphere(gl, textureId1, textureId2);
        cube = new Cube(gl, textureId3, textureId4);
        cube2 = new Cube(gl, textureId5, textureId6);

        light = new Light(gl);
        light.setCamera(camera);

        floor.setLight(light);
        floor.setCamera(camera);
        sphere.setLight(light);
        sphere.setCamera(camera);
        cube.setLight(light);
        cube.setCamera(camera);
        cube2.setLight(light);
        cube2.setCamera(camera);

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode phalangeShape[][] = new MeshNode[DIGIT_COUNT][PHALANGE_COUNT];
        MeshNode armShape = new MeshNode("Cube(arm)", cube);
        MeshNode palmShape = new MeshNode("Cube(palm)", cube);

        NameNode digit[][] = new NameNode[DIGIT_COUNT][PHALANGE_COUNT];
        robotHand = new NameNode("root");
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");

        TransformNode phalTLate[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];
        TransformNode phalTForm[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];

        // ------------ Dimensions + Positions ------------ \\

        float armWidth = 2f;
        float armHeight = 5f;
        float armDepth = 1.25f;
        float palmWidth = 4f;
        float palmHeight = 4f;
        float palmDepth = 1.25f;

        float fingXLgHeight = 1.8f;

        float fingLrgWidth = 0.8f;
        float fingLrgHeight = 1.5f;
        float fingLrgDepth = 0.8f;

        float fingMedWidth = 0.75f;
        float fingMedHeight = 1.3f;
        float fingMedDepth = 0.75f;

        float fingSmlWidth = 0.7f;
        float fingSmlHeight = 1.2f;
        float fingSmlDepth = 0.7f;

        float fingXSmWidth = 0.65f;
        float fingXSmHeight = 1.1f;
        float fingXSmDepth = 0.65f;

        float[] digitHrzPos = {palmWidth/2, 1.5f, 0.5f, -0.5f, -1.5f};

        float[][][] phalDims = {
                {{fingXLgHeight, fingLrgWidth, fingLrgDepth}, {fingLrgHeight, fingMedWidth, fingMedDepth}, {fingMedHeight, fingSmlWidth, fingSmlDepth}},
                {{fingLrgWidth, fingLrgHeight, fingLrgDepth}, {fingMedWidth, fingMedHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingLrgWidth, fingXLgHeight, fingLrgDepth}, {fingMedWidth, fingLrgHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingLrgWidth, fingLrgHeight, fingLrgDepth}, {fingMedWidth, fingMedHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingSmlWidth, fingXSmHeight, fingSmlDepth}, {fingXSmWidth, fingXSmHeight, fingXSmDepth}, {fingXSmWidth, fingXSmHeight, fingXSmDepth}}
        };


        // ------------ Initialise all Arrays ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            maxSecAngle[d] = 20;
            minSecAngle[d] = -20;
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                currentPrmAngles[d][p] = 1;
                if (d != 0){
                    maxPrmAngle[d][p] = 90;
                }
                minPrmAngle[d][p] = 0;
                phalangeShape[d][p] = new MeshNode("Cube(digit" + Integer.toString(d) + "-phal" + Integer.toString(p) + ")", cube);
                digit[d][p] = new NameNode("[" + Integer.toString(d) + "][" + Integer.toString(p) + "]");
            }
        }
        System.out.println("Variables initialised");

        // Thumb-Specific Angles
        maxPrmAngle[0][0] = 90;
        maxPrmAngle[0][1] = 60;
        maxPrmAngle[0][2] = 90;
        maxSecAngle[0] = 90;
        minSecAngle[0] = 0;


        // ------------ Initialising TranslationNodes ------------ \\ -- could go in loop

        phalTLate[0][0] = new TransformNode("phalTLate[0][0]", Mat4Transform.translate(digitHrzPos[0], 1f, 0.5f));
        phalTLate[0][1] = new TransformNode("phalTLate[0][1]", Mat4Transform.translate(fingXLgHeight, 0, 0));
        phalTLate[0][2] = new TransformNode("phalTLate[0][2]", Mat4Transform.translate(fingLrgHeight, 0, 0));

        phalTLate[1][0] = new TransformNode("phalTLate[1][0]", Mat4Transform.translate(digitHrzPos[1], palmHeight, 0));
        phalTLate[1][1] = new TransformNode("phalTLate[1][1]", Mat4Transform.translate(0, fingLrgHeight, 0));
        phalTLate[1][2] = new TransformNode("phalTLate[1][2]", Mat4Transform.translate(0, fingMedHeight, 0));

        phalTLate[2][0] = new TransformNode("phalTLate[2][0]", Mat4Transform.translate(digitHrzPos[2], palmHeight, 0));
        phalTLate[2][1] = new TransformNode("phalTLate[2][1]", Mat4Transform.translate(0, fingXLgHeight, 0));
        phalTLate[2][2] = new TransformNode("phalTLate[2][2]", Mat4Transform.translate(0, fingLrgHeight, 0));

        phalTLate[3][0] = new TransformNode("phalTLate[3][0]", Mat4Transform.translate(digitHrzPos[3], palmHeight, 0));
        phalTLate[3][1] = new TransformNode("phalTLate[3][1]", Mat4Transform.translate(0, fingLrgHeight, 0));
        phalTLate[3][2] = new TransformNode("phalTLate[3][2]", Mat4Transform.translate(0, fingMedHeight, 0));

        phalTLate[4][0] = new TransformNode("phalTLate[4][0]", Mat4Transform.translate(digitHrzPos[4], palmHeight, 0));
        phalTLate[4][1] = new TransformNode("phalTLate[4][1]", Mat4Transform.translate(0, fingXSmHeight, 0));
        phalTLate[4][2] = new TransformNode("phalTLate[4][2]", Mat4Transform.translate(0, fingXSmHeight, 0));


        // ------------ Arm + Palm ------------ \\

        Mat4 m = Mat4Transform.scale(armWidth, armHeight, armDepth); // Sets dimensions of arm
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0)); // Move up by 0.5*height for origin
        TransformNode armTransform = new TransformNode("arm transform", m);
        armRotateY = new TransformNode("arm rotate",Mat4Transform.rotateAroundY(0));

        TransformNode palmTranslate = new TransformNode("palm translate", Mat4Transform.translate(0,armHeight,0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(palmWidth, palmHeight, palmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode palmTransform = new TransformNode("palm transform", m);
        palmRotateX = new TransformNode("palmX rotate",Mat4Transform.rotateAroundX(0));
        palmRotateZ = new TransformNode("palmZ rotate",Mat4Transform.rotateAroundZ(0));


        // ------------ Node Generation ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                m = new Mat4(1);
                m = Mat4.multiply(m, Mat4Transform.scale(phalDims[d][p][0], phalDims[d][p][1], phalDims[d][p][2]));
                if (d==0) {
                    m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
                }else{
                    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                }
                phalTForm[d][p] = new TransformNode("phalTForm[" + d + "][" + Integer.toString(p) + "]", m);
                if (d==0) {
                    phalRotX[d][p] = new TransformNode("phalRotX[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentSecAngles[d]));
                    phalRotZ[d][p] = new TransformNode("phalRotZ[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                } else {
                    phalRotX[d][p] = new TransformNode("phalRotX[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    phalRotZ[d][p] = new TransformNode("phalRotZ[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                }

            }
        }

        // ------------ Scene Graph ------------ \\

        robotHand.addChild(arm);
            arm.addChild(armRotateY);
                armRotateY.addChild(armTransform);
                    armTransform.addChild(armShape);
                    armRotateY.addChild(palm);

                        palm.addChild(palmTranslate);
                            palmTranslate.addChild(palmRotateX);
                                palmRotateX.addChild(palmRotateZ);
                                    palmRotateZ.addChild(palmTransform);
                                        palmTransform.addChild(palmShape);

                                    palmRotateZ.addChild(phalTLate[0][0]);
                                        phalTLate[0][0].addChild(digit[0][0]);
                                            digit[0][0].addChild(phalRotX[0][0]);
                                                phalRotX[0][0].addChild(phalRotZ[0][0]);
                                                    phalRotZ[0][0].addChild(phalTForm[0][0]);
                                                        phalTForm[0][0].addChild(phalangeShape[0][0]);

                                                    phalRotZ[0][0].addChild(phalTLate[0][1]);
                                                        phalTLate[0][1].addChild(digit[0][1]);
                                                            digit[0][1].addChild(phalRotZ[0][1]);
                                                                phalRotZ[0][1].addChild(phalTForm[0][1]);
                                                                    phalTForm[0][1].addChild(phalangeShape[0][1]);

                                                                phalRotZ[0][1].addChild(phalTLate[0][2]);
                                                                    phalTLate[0][2].addChild(digit[0][2]);
                                                                        digit[0][2].addChild(phalRotZ[0][2]);
                                                                            phalRotZ[0][2].addChild(phalTForm[0][2]);
                                                                                phalTForm[0][2].addChild(phalangeShape[0][2]);

                                    for (int d = 1; d < DIGIT_COUNT; d++) {
                                        palmRotateZ.addChild(phalTLate[d][0]);
                                            phalTLate[d][0].addChild(digit[d][0]);
                                                digit[d][0].addChild(phalRotZ[d][0]);
                                                    phalRotZ[d][0].addChild(phalRotX[d][0]);
                                                        phalRotX[d][0].addChild(phalTForm[d][0]);
                                                            phalTForm[d][0].addChild(phalangeShape[d][0]);

                                                        phalRotX[d][0].addChild(phalTLate[d][1]);
                                                            phalTLate[d][1].addChild(digit[d][1]);
                                                                digit[d][1].addChild(phalRotX[d][1]);
                                                                    phalRotX[d][1].addChild(phalTForm[d][1]);
                                                                        phalTForm[d][1].addChild(phalangeShape[d][1]);

                                                                    phalRotX[d][1].addChild(phalTLate[d][2]);
                                                                        phalTLate[d][2].addChild(digit[d][2]);
                                                                            digit[d][2].addChild(phalRotX[d][2]);
                                                                                phalRotX[d][2].addChild(phalTForm[d][2]);
                                                                                    phalTForm[d][2].addChild(phalangeShape[d][2]);
                                    }

        robotHand.update();
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        updatePerspectiveMatrices();

        light.setPosition(getLightPosition());  // changing light position each frame
        light.render(gl);
        floor.render(gl);

        for (int d = 0; d < DIGIT_COUNT; d++) {
            //Primary Angles
            for (int p = 0; p < PHALANGE_COUNT; p++){
                if (currentPrmAngles[d][p] - desiredPrmAngle[d][p] < 0) {
                    if (currentPrmAngles[d][p] < maxPrmAngle[d][p]){
                        currentPrmAngles[d][p]++;
                    }
                } else if (currentPrmAngles[d][p] - desiredPrmAngle[d][p] > 0) {
                    if (currentPrmAngles[d][p] > minPrmAngle[d][p]){
                        currentPrmAngles[d][p]--;

                    }
                }
            }

            //Secondary Angles
            if (currentSecAngles[d] - desiredSecAngle[d] < 0) {
                if (currentSecAngles[d] < maxSecAngle[d]) {
                    currentSecAngles[d]++;
                }
            } else if (currentSecAngles[d] - desiredSecAngle[d] > 0) {
                if (currentSecAngles[d] > minSecAngle[d]) {
                    currentSecAngles[d]--;
                }
            }
        }

        updateAngles();
        robotHand.draw(gl);
    }



    private void updatePerspectiveMatrices() {
        // needs to be changed if user resizes the window
        perspective = Mat4Transform.perspective(45, aspect);
        light.setPerspective(perspective);
        floor.setPerspective(perspective);
        sphere.setPerspective(perspective);
        cube.setPerspective(perspective);
        cube2.setPerspective(perspective);
    }
  
    private void disposeMeshes(GL3 gl) {
        light.dispose(gl);
        floor.dispose(gl);
        sphere.dispose(gl);
        cube.dispose(gl);
        cube2.dispose(gl);
    }
  
    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds()-startTime;
        float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
        float y = 2.7f;
        float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
        return new Vec3(x,y,z);
        //return new Vec3(5f,3.4f,5f);
    }
  
}