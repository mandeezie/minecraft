/***************************************************************
* file: Project3.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/21/19
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


public class Project3 {
    
    private FPCameraController fp;
    private DisplayMode displayMode;
    
    //method: start
    //purpose: initializes the window
    public void start() {
        try {
            createWindow();
            initGL();
            fp = new FPCameraController(0f,0f,0f);
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
    
    //method: initGL
    //purpose: initialize the GL properties
    private void initGL() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);

        
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
