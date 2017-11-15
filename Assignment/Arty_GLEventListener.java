import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Arty_GLEventListener implements GLEventListener {
  
    private static final boolean DISPLAY_SHADERS = false;
    private float aspect;

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

    public void curlFing1(){
        fing1Anim = true;
    }

    public void curlFing2(){
        fing2Anim = true;
    }

    public void curlFing3(){
        fing3Anim = true;
    }

    public void curlFing4(){
        fing4Anim = true;
    }

    public void curlThumb(){
        thumbAnim = true;
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

    private float xPosition = 0;
    private int palmXAngle, palmZAngle;


    
    private boolean fing1Straight = true;
    private boolean fing1Anim = false;
    private TransformNode fing1ProxRotateX, fing1ProxRotateZ, fing1MiddRotateX, fing1DistRotateX;
    private int fing1ProxAngleX, fing1MiddAngleX, fing1DistAngleX; // default to 0

    private boolean fing2Straight = true;
    private boolean fing2Anim = false;
    private TransformNode fing2ProxRotateX, fing2ProxRotateZ, fing2MiddRotateX, fing2DistRotateX;
    private int fing2ProxAngleX, fing2MiddAngleX, fing2DistAngleX; // default to 0

    private boolean fing3Straight = true;
    private boolean fing3Anim = false;
    private TransformNode fing3ProxRotateX, fing3ProxRotateZ, fing3MiddRotateX, fing3DistRotateX;
    private int fing3ProxAngleX, fing3MiddAngleX, fing3DistAngleX; // default to 0

    private boolean fing4Straight = true;
    private boolean fing4Anim = false;
    private TransformNode fing4ProxRotateX, fing4ProxRotateZ, fing4MiddRotateX, fing4DistRotateX;
    private int fing4ProxAngleX, fing4MiddAngleX, fing4DistAngleX; // default to 0

    private boolean thumbStraight = true;
    private boolean thumbAnim = false;
    private TransformNode thumbProxRotateY, thumbProxRotateZ, thumbMiddRotateZ, thumbDistRotateZ;
    private int thumbProxAngleY, thumbProxAngleZ, thumbMiddAngleZ, thumbDistAngleZ; // default to 0
    
    private TransformNode armRotateY, palmRotateX, palmRotateZ;
  
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

        // ------------ MeshNodes + NameNodes ------------ \\

        MeshNode armShape = new MeshNode("Cube(arm)", cube);
        MeshNode palmShape = new MeshNode("Cube(palm)", cube);
        MeshNode fing1ProxShape = new MeshNode("Cube(fing1Prox)", cube);
        MeshNode fing1MiddShape = new MeshNode("Cube(fing1Midd)", cube);
        MeshNode fing1DistShape = new MeshNode("Cube(fing1Dist)", cube);
        MeshNode fing2ProxShape = new MeshNode("Cube(fing2Prox)", cube);
        MeshNode fing2MiddShape = new MeshNode("Cube(fing2Midd)", cube);
        MeshNode fing2DistShape = new MeshNode("Cube(fing2Dist)", cube);
        MeshNode fing3ProxShape = new MeshNode("Cube(fing3Prox)", cube);
        MeshNode fing3MiddShape = new MeshNode("Cube(fing3Midd)", cube);
        MeshNode fing3DistShape = new MeshNode("Cube(fing3Dist)", cube);
        MeshNode fing4ProxShape = new MeshNode("Cube(fing4Prox)", cube);
        MeshNode fing4MiddShape = new MeshNode("Cube(fing4Midd)", cube);
        MeshNode fing4DistShape = new MeshNode("Cube(fing4Dist)", cube);
        MeshNode thumbProxShape = new MeshNode("Cube(thumbProx)", cube);
        MeshNode thumbMiddShape = new MeshNode("Cube(thumbMidd)", cube);
        MeshNode thumbDistShape = new MeshNode("Cube(thumbDist)", cube);

        robotHand = new NameNode("root");
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");
        NameNode fing1Prox = new NameNode("fing1Prox");
        NameNode fing1Midd = new NameNode("fing1Midd");
        NameNode fing1Dist = new NameNode("fing1Dist");
        NameNode fing2Prox = new NameNode("fing2Prox");
        NameNode fing2Midd = new NameNode("fing2Midd");
        NameNode fing2Dist = new NameNode("fing2Dist");
        NameNode fing3Prox = new NameNode("fing3Prox");
        NameNode fing3Midd = new NameNode("fing3Midd");
        NameNode fing3Dist = new NameNode("fing3Dist");
        NameNode fing4Prox = new NameNode("fing4Prox");
        NameNode fing4Midd = new NameNode("fing4Midd");
        NameNode fing4Dist = new NameNode("fing4Dist");
        NameNode thumbProx = new NameNode("thumbProx");
        NameNode thumbMidd = new NameNode("thumbMidd");
        NameNode thumbDist = new NameNode("thumbDist");

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

        float fingXXSWidth = 0.55f;
        float fingXXSHeight = 1f;
        float fingXXSDepth = 0.55f;

        float fingProxVert = 0;//armHeight;// + palmHeight;

        float fingMiddXLgVert = fingProxVert + fingXLgHeight;
        float fingMiddLrgVert = fingProxVert + fingLrgHeight;
        float fingMiddMedVert = fingProxVert + fingMedHeight;
        float fingMiddXSmVert = fingProxVert + fingXSmHeight;

        float fingDistXLVert = fingMiddXLgVert + fingLrgHeight;
        float fingDistLMVert = fingMiddLrgVert + fingMedHeight;
        float fingDistMMVert = fingMiddMedVert + fingMedHeight;
        float fingDistSXVert = fingMiddXSmVert + fingXSmHeight;

        float fing1HrzPos = 1.5f;
        float fing2HrzPos = 0.5f;
        float fing3HrzPos = -0.5f;
        float fing4HrzPos = -1.5f;

        float thumbProxVert = armHeight + 0.3f;
        float thumbMiddVert = thumbProxVert + 0.025f;
        float thumbDistVert = thumbMiddVert + 0.025f;

        float thumbProxHrz = palmWidth/2;
        float thumbMiddHrz = thumbProxHrz + fingLrgHeight + 0.15f;
        float thumbDistHrz = thumbMiddHrz + fingMedHeight + 0.1f;


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

        // ------------ Finger #1 (Index) ------------ \\

        TransformNode fing1ProxTranslate = new TransformNode("fing1Prox translate",
                Mat4Transform.translate(fing1HrzPos, palmHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing1ProxTransform = new TransformNode("fing1Prox transform", m);
        fing1ProxRotateX = new TransformNode("fing1Prox Xrotate", Mat4Transform.rotateAroundX(0));
        fing1ProxRotateZ = new TransformNode("fing1Prox Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode fing1MiddTranslate = new TransformNode("fing1Midd translate",
                Mat4Transform.translate(0, fingLrgHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode fing1MiddTransform = new TransformNode("fing1Midd transform", m);
        fing1MiddRotateX = new TransformNode("fing1Midd Xrotate", Mat4Transform.rotateAroundX(0));


        TransformNode fing1DistTranslate = new TransformNode("fing1Dist translate",
                Mat4Transform.translate(0, fingMedHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing1DistTransform = new TransformNode("fing1Dist transform", m);
        fing1DistRotateX = new TransformNode("fing1Dist Xrotate", Mat4Transform.rotateAroundX(0));

        // ------------ Finger #2 (Middle) ------------ \\

        TransformNode fing2ProxTranslate = new TransformNode("fing2Prox translate",
                Mat4Transform.translate(fing2HrzPos, palmHeight, 0));
        m = new Mat4(2);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingXLgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing2ProxTransform = new TransformNode("fing2Prox transform", m);
        fing2ProxRotateX = new TransformNode("fing2Prox Xrotate", Mat4Transform.rotateAroundX(0));
        fing2ProxRotateZ = new TransformNode("fing2Prox Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode fing2MiddTranslate = new TransformNode("fing2Midd translate",
                Mat4Transform.translate(0, fingXLgHeight, 0));
        m = new Mat4(2);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingLrgHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode fing2MiddTransform = new TransformNode("fing2Midd transform", m);
        fing2MiddRotateX = new TransformNode("fing2Midd Xrotate", Mat4Transform.rotateAroundX(0));


        TransformNode fing2DistTranslate = new TransformNode("fing2Dist translate",
                Mat4Transform.translate(0, fingLrgHeight, 0));
        m = new Mat4(2);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing2DistTransform = new TransformNode("fing2Dist transform", m);
        fing2DistRotateX = new TransformNode("fing2Dist Xrotate", Mat4Transform.rotateAroundX(0));

        // ------------ Finger #3 (Ring) ------------ \\

        TransformNode fing3ProxTranslate = new TransformNode("fing3Prox translate",
                Mat4Transform.translate(fing3HrzPos, palmHeight, 0));
        m = new Mat4(3);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing3ProxTransform = new TransformNode("fing3Prox transform", m);
        fing3ProxRotateX = new TransformNode("fing3Prox Xrotate", Mat4Transform.rotateAroundX(0));
        fing3ProxRotateZ = new TransformNode("fing3Prox Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode fing3MiddTranslate = new TransformNode("fing3Midd translate",
                Mat4Transform.translate(0, fingLrgHeight, 0));
        m = new Mat4(3);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode fing3MiddTransform = new TransformNode("fing3Midd transform", m);
        fing3MiddRotateX = new TransformNode("fing3Midd Xrotate", Mat4Transform.rotateAroundX(0));


        TransformNode fing3DistTranslate = new TransformNode("fing3Dist translate",
                Mat4Transform.translate(0, fingMedHeight, 0));
        m = new Mat4(3);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing3DistTransform = new TransformNode("fing3Dist transform", m);
        fing3DistRotateX = new TransformNode("fing3Dist Xrotate", Mat4Transform.rotateAroundX(0));

        // ------------ Finger #4 (Little) ------------ \\

        TransformNode fing4ProxTranslate = new TransformNode("fing4Prox translate",
                Mat4Transform.translate(fing4HrzPos, palmHeight, 0));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingXSmHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing4ProxTransform = new TransformNode("fing4Prox transform", m);
        fing4ProxRotateX = new TransformNode("fing4Prox Xrotate", Mat4Transform.rotateAroundX(0));
        fing4ProxRotateZ = new TransformNode("fing4Prox Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode fing4MiddTranslate = new TransformNode("fing4Midd translate",
                Mat4Transform.translate(0, fingXSmHeight, 0));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXSmWidth, fingXSmHeight, fingXSmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode fing4MiddTransform = new TransformNode("fing4Midd transform", m);
        fing4MiddRotateX = new TransformNode("fing4Midd Xrotate", Mat4Transform.rotateAroundX(0));


        TransformNode fing4DistTranslate = new TransformNode("fing4Dist translate",
                Mat4Transform.translate(0, fingXSmHeight, 0));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXSmWidth, fingXSmHeight, fingXSmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing4DistTransform = new TransformNode("fing4Dist transform", m);
        fing4DistRotateX = new TransformNode("fing4Dist Xrotate", Mat4Transform.rotateAroundX(0));


        // ------------ Thumb ------------ \\

        TransformNode thumbProxTranslate = new TransformNode("thumbProx translate",
                Mat4Transform.translate(thumbProxHrz, 1f, 0.5f));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXLgHeight, fingLrgWidth, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        TransformNode thumbProxTransform = new TransformNode("thumbProx transform", m);
        thumbProxRotateY = new TransformNode("thumbProx Yrotate", Mat4Transform.rotateAroundY(0));
        thumbProxRotateZ = new TransformNode("thumbProx Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode thumbMiddTranslate = new TransformNode("thumbMidd translate",
                Mat4Transform.translate(fingXLgHeight, 0, 0));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgHeight, fingMedWidth, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        TransformNode thumbMiddTransform = new TransformNode("thumbMidd transform", m);
        thumbMiddRotateZ = new TransformNode("thumbMidd Zrotate", Mat4Transform.rotateAroundZ(0));


        TransformNode thumbDistTranslate = new TransformNode("thumbDist translate",
                Mat4Transform.translate(fingLrgHeight, 0, 0));
        m = new Mat4(4);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedHeight, fingSmlWidth, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        TransformNode thumbDistTransform = new TransformNode("thumbDist transform", m);
        thumbDistRotateZ = new TransformNode("thumbDist Zrotate", Mat4Transform.rotateAroundZ(0));




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

                                    palmRotateZ.addChild(fing1ProxTranslate);
                                        fing1ProxTranslate.addChild(fing1Prox);
                                            fing1Prox.addChild(fing1ProxRotateX);
                                                fing1ProxRotateX.addChild(fing1ProxTransform);
                                                    fing1ProxTransform.addChild(fing1ProxShape);
                                                fing1ProxRotateX.addChild(fing1MiddTranslate);
                                                    fing1MiddTranslate.addChild(fing1Midd);
                                                        fing1Midd.addChild(fing1MiddRotateX);
                                                            fing1MiddRotateX.addChild(fing1MiddTransform);
                                                                fing1MiddTransform.addChild(fing1MiddShape);
                                                            fing1MiddRotateX.addChild(fing1DistTranslate);
                                                                fing1DistTranslate.addChild(fing1Dist);
                                                                    fing1Dist.addChild(fing1DistRotateX);
                                                                        fing1DistRotateX.addChild(fing1DistTransform);
                                                                            fing1DistTransform.addChild(fing1DistShape);

                                    palmRotateZ.addChild(fing2ProxTranslate);
                                        fing2ProxTranslate.addChild(fing2Prox);
                                            fing2Prox.addChild(fing2ProxRotateX);
                                                fing2ProxRotateX.addChild(fing2ProxTransform);
                                                    fing2ProxTransform.addChild(fing2ProxShape);
                                                fing2ProxRotateX.addChild(fing2MiddTranslate);
                                                    fing2MiddTranslate.addChild(fing2Midd);
                                                        fing2Midd.addChild(fing2MiddRotateX);
                                                            fing2MiddRotateX.addChild(fing2MiddTransform);
                                                                fing2MiddTransform.addChild(fing2MiddShape);
                                                            fing2MiddRotateX.addChild(fing2DistTranslate);
                                                                fing2DistTranslate.addChild(fing2Dist);
                                                                    fing2Dist.addChild(fing2DistRotateX);
                                                                        fing2DistRotateX.addChild(fing2DistTransform);
                                                                            fing2DistTransform.addChild(fing2DistShape);

                                    palmRotateZ.addChild(fing3ProxTranslate);
                                        fing3ProxTranslate.addChild(fing3Prox);
                                            fing3Prox.addChild(fing3ProxRotateX);
                                                fing3ProxRotateX.addChild(fing3ProxTransform);
                                                    fing3ProxTransform.addChild(fing3ProxShape);
                                                fing3ProxRotateX.addChild(fing3MiddTranslate);
                                                    fing3MiddTranslate.addChild(fing3Midd);
                                                        fing3Midd.addChild(fing3MiddRotateX);
                                                            fing3MiddRotateX.addChild(fing3MiddTransform);
                                                                fing3MiddTransform.addChild(fing3MiddShape);
                                                            fing3MiddRotateX.addChild(fing3DistTranslate);
                                                                fing3DistTranslate.addChild(fing3Dist);
                                                                    fing3Dist.addChild(fing3DistRotateX);
                                                                        fing3DistRotateX.addChild(fing3DistTransform);
                                                                            fing3DistTransform.addChild(fing3DistShape);

                                    palmRotateZ.addChild(fing4ProxTranslate);
                                        fing4ProxTranslate.addChild(fing4Prox);
                                            fing4Prox.addChild(fing4ProxRotateX);
                                                fing4ProxRotateX.addChild(fing4ProxTransform);
                                                    fing4ProxTransform.addChild(fing4ProxShape);
                                                fing4ProxRotateX.addChild(fing4MiddTranslate);
                                                    fing4MiddTranslate.addChild(fing4Midd);
                                                        fing4Midd.addChild(fing4MiddRotateX);
                                                            fing4MiddRotateX.addChild(fing4MiddTransform);
                                                                fing4MiddTransform.addChild(fing4MiddShape);
                                                            fing4MiddRotateX.addChild(fing4DistTranslate);
                                                                fing4DistTranslate.addChild(fing4Dist);
                                                                    fing4Dist.addChild(fing4DistRotateX);
                                                                        fing4DistRotateX.addChild(fing4DistTransform);
                                                                            fing4DistTransform.addChild(fing4DistShape);

                                    palmRotateZ.addChild(thumbProxTranslate);
                                        thumbProxTranslate.addChild(thumbProx);
                                            thumbProx.addChild(thumbProxRotateY);
                                                thumbProxRotateY.addChild(thumbProxRotateZ);
                                                    thumbProxRotateZ.addChild(thumbMiddTranslate);
                                                        thumbMiddTranslate.addChild(thumbMidd);
                                                            thumbMidd.addChild(thumbMiddRotateZ);
                                                                thumbMiddRotateZ.addChild(thumbDistTranslate);
                                                                    thumbDistTranslate.addChild(thumbDist);
                                                                        thumbDist.addChild(thumbDistRotateZ);
                                                                            thumbDistRotateZ.addChild(thumbDistTransform);
                                                                                thumbDistTransform.addChild(thumbDistShape);
                                                                thumbMiddRotateZ.addChild(thumbMiddTransform);
                                                                    thumbMiddTransform.addChild(thumbMiddShape);
                                                    thumbProxRotateZ.addChild(thumbProxTransform);
                                                        thumbProxTransform.addChild(thumbProxShape);

        robotHand.update();
    }
    private int count = 0;
    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        updatePerspectiveMatrices();

        light.setPosition(getLightPosition());  // changing light position each frame
        light.render(gl);
        floor.render(gl);

        if (fing1Anim) {
            if (fing1Straight) {
                fing1ProxAngleX++;
                fing1ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing1ProxAngleX));
                fing1ProxRotateX.update();

                fing1MiddAngleX++;
                fing1MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing1MiddAngleX));
                fing1MiddRotateX.update();

                fing1DistAngleX++;
                fing1DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing1DistAngleX));
                fing1DistRotateX.update();

                if (fing1ProxAngleX > 90) {
                    fing1Anim = false;
                    fing1Straight = false;
                }
            }else{
                fing1ProxAngleX--;
                fing1ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing1ProxAngleX));
                fing1ProxRotateX.update();

                fing1MiddAngleX--;
                fing1MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing1MiddAngleX));
                fing1MiddRotateX.update();

                fing1DistAngleX--;
                fing1DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing1DistAngleX));
                fing1DistRotateX.update();

                if (fing1ProxAngleX < 1) {
                    fing1Anim = false;
                    fing1Straight = true;
                }
            }
        }

        if (fing2Anim) {
            if (fing2Straight) {
                fing2ProxAngleX++;
                fing2ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing2ProxAngleX));
                fing2ProxRotateX.update();

                fing2MiddAngleX++;
                fing2MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing2MiddAngleX));
                fing2MiddRotateX.update();

                fing2DistAngleX++;
                fing2DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing2DistAngleX));
                fing2DistRotateX.update();

                if (fing2ProxAngleX > 90) {
                    fing2Anim = false;
                    fing2Straight = false;
                }
            }else{
                fing2ProxAngleX--;
                fing2ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing2ProxAngleX));
                fing2ProxRotateX.update();

                fing2MiddAngleX--;
                fing2MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing2MiddAngleX));
                fing2MiddRotateX.update();

                fing2DistAngleX--;
                fing2DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing2DistAngleX));
                fing2DistRotateX.update();

                if (fing2ProxAngleX < 1) {
                    fing2Anim = false;
                    fing2Straight = true;
                }
            }
        }

        if (fing3Anim) {
            if (fing3Straight) {
                fing3ProxAngleX++;
                fing3ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing3ProxAngleX));
                fing3ProxRotateX.update();

                fing3MiddAngleX++;
                fing3MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing3MiddAngleX));
                fing3MiddRotateX.update();

                fing3DistAngleX++;
                fing3DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing3DistAngleX));
                fing3DistRotateX.update();

                if (fing3ProxAngleX > 90) {
                    fing3Anim = false;
                    fing3Straight = false;
                }
            }else{
                fing3ProxAngleX--;
                fing3ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing3ProxAngleX));
                fing3ProxRotateX.update();

                fing3MiddAngleX--;
                fing3MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing3MiddAngleX));
                fing3MiddRotateX.update();

                fing3DistAngleX--;
                fing3DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing3DistAngleX));
                fing3DistRotateX.update();

                if (fing3ProxAngleX < 1) {
                    fing3Anim = false;
                    fing3Straight = true;
                }
            }
        }

        if (fing4Anim) {
            if (fing4Straight) {
                fing4ProxAngleX++;
                fing4ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing4ProxAngleX));
                fing4ProxRotateX.update();

                fing4MiddAngleX++;
                fing4MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing4MiddAngleX));
                fing4MiddRotateX.update();

                fing4DistAngleX++;
                fing4DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing4DistAngleX));
                fing4DistRotateX.update();

                if (fing4ProxAngleX > 90) {
                    fing4Anim = false;
                    fing4Straight = false;
                }
            }else{
                fing4ProxAngleX--;
                fing4ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing4ProxAngleX));
                fing4ProxRotateX.update();

                fing4MiddAngleX--;
                fing4MiddRotateX.setTransform(Mat4Transform.rotateAroundX(fing4MiddAngleX));
                fing4MiddRotateX.update();

                fing4DistAngleX--;
                fing4DistRotateX.setTransform(Mat4Transform.rotateAroundX(fing4DistAngleX));
                fing4DistRotateX.update();

                if (fing4ProxAngleX < 1) {
                    fing4Anim = false;
                    fing4Straight = true;
                }
            }
        }

        if (thumbAnim) {
            if (thumbStraight) {
                thumbProxAngleY--;
                thumbProxRotateY.setTransform(Mat4Transform.rotateAroundY(thumbProxAngleY));
                thumbProxRotateY.update();

                thumbProxAngleZ++;
                thumbProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbProxAngleZ));
                thumbProxRotateZ.update();

                thumbMiddAngleZ++;
                thumbMiddRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbMiddAngleZ));
                thumbMiddRotateZ.update();

                thumbDistAngleZ++;
                thumbDistRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbDistAngleZ));
                thumbDistRotateZ.update();

                if (thumbProxAngleY < -45) {
                    thumbAnim = false;
                    thumbStraight = false;
                }
            }else{
                thumbProxAngleY++;
                thumbProxRotateY.setTransform(Mat4Transform.rotateAroundY(thumbProxAngleY));
                thumbProxRotateY.update();

                thumbProxAngleZ--;
                thumbProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbProxAngleZ));
                thumbProxRotateZ.update();

                thumbMiddAngleZ--;
                thumbMiddRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbMiddAngleZ));
                thumbMiddRotateZ.update();

                thumbDistAngleZ--;
                thumbDistRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbDistAngleZ));
                thumbDistRotateZ.update();

                if (thumbProxAngleY > 1) {
                    thumbAnim = false;
                    thumbStraight = true;
                }
            }
        }

        // rotate fing1Prox about Z
        // stop when angle reached


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