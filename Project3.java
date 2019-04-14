/***************************************************************
* file: Project3.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/27/19
*
* purpose: this program uses OpenGL to create a minecraft-like
* world
* 
****************************************************************/ 

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Mouse;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;


public class Project3 {
    
    private FPCameraController fp;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    //method: start
    //purpose: initializes the window
    public void start() {
        try {
            createWindow();
            initGL();
            fp = new FPCameraController(0, 0, 0, 1);
            fp.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose: sets the size of the window and displays it to the screen
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i = 0; i < d.length; i++)
        {
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32)
            {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);//(new DisplayMode(640, 480));
        Display.setTitle("Final Project - DJ double A");
        Display.create();
    }
    
    //method: initLightArrays
    //purpose: initialize the light arrays
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
}

    
    //method: initGL
    //purpose: initialize the GL properties
    private void initGL() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
  
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        glOrtho(0, 640, 0, 480, -1, 1);
    }
    
    //method: main
    //purpose: start the program
    public static void main(String[] args) {
        Project3 basic = new Project3();
        basic.start();
    }
}