import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Arty extends JFrame implements ActionListener {
  
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private GLCanvas canvas;
    private Arty_GLEventListener glEventListener;
    private final FPSAnimator animator;
    private Camera camera;

    public static void main(String[] args) {
        Arty b1 = new Arty("COM3503 - Robot Hand");
        b1.getContentPane().setPreferredSize(dimension);
        b1.pack();
        b1.setVisible(true);
    }

    public Arty(String textForTitleBar) {
        super(textForTitleBar);
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        canvas = new GLCanvas(glcapabilities);
        camera = new Camera(new Vec3(4f,12f,18f), Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
        glEventListener = new Arty_GLEventListener(camera);
        canvas.addGLEventListener(glEventListener);
        canvas.addMouseMotionListener(new MyMouseInput(camera));
        canvas.addKeyListener(new MyKeyboardInput(camera));
        getContentPane().add(canvas, BorderLayout.CENTER);

        JMenuBar menuBar=new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);

        JPanel panel = new JPanel();
        JButton btn = new JButton("camera X");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("camera Z");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("start");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("stop");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("X++");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("X--");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("Z++");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("Z--");
        btn.addActionListener(this);
        panel.add(btn);
//        btn = new JButton("Rot++");
//        btn.addActionListener(this);
//        panel.add(btn);
//        btn = new JButton("Rot--");
//        btn.addActionListener(this);
//        panel.add(btn);
        JSlider armAngleSlider = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        armAngleSlider.addChangeListener(sliderListener);
        panel.add(armAngleSlider);
        this.add(panel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                remove(canvas);
                dispose();
                System.exit(0);
            }
        });
        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    ChangeListener sliderListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event)
        {
            JSlider source = (JSlider) event.getSource();
            glEventListener.rotToAngle(source.getValue());
        }
    };

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("camera X")) {
            camera.setCamera(Camera.CameraType.X);
            canvas.requestFocusInWindow();
        }
        else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
            camera.setCamera(Camera.CameraType.Z);
            canvas.requestFocusInWindow();
        }
        else if (e.getActionCommand().equalsIgnoreCase("start")) {
            glEventListener.startAnimation();
        }
        else if (e.getActionCommand().equalsIgnoreCase("stop")) {
            glEventListener.stopAnimation();
        }
        else if (e.getActionCommand().equalsIgnoreCase("X++")) {
            glEventListener.rotPalmXPos();
        }
        else if (e.getActionCommand().equalsIgnoreCase("X--")) {
            glEventListener.rotPalmXNeg();
        }
        else if (e.getActionCommand().equalsIgnoreCase("Z++")) {
            glEventListener.rotPalmZPos();
        }
        else if (e.getActionCommand().equalsIgnoreCase("Z--")) {
            glEventListener.rotPalmZNeg();
        }
//        else if (e.getActionCommand().equalsIgnoreCase("Rot++")) {
//            glEventListener.rotClockwise();
//        }
//        else if (e.getActionCommand().equalsIgnoreCase("Rot--")) {
//            glEventListener.rotAntiClockwise();
//        }
        else if(e.getActionCommand().equalsIgnoreCase("quit")){
            System.exit(0);
        }
    }
  
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}