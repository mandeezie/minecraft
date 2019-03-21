/***************************************************************
* file: FPCameraController.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/21/19
*
* purpose: create a way to manipulate the first person camera
* to be able to display the correct information to the screen
* 
****************************************************************/ 

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    //3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lPosition = null;
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;
    private Vector3Float me;
    
    private Chunk chunk;

    //method: FPCameraController
    //purpose: initialize the camera variables
    public FPCameraController(float x, float y, float z)
    {   
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
        
        chunk = new Chunk((int)lPosition.x, (int)lPosition.y, (int)lPosition.z);
    }

    //method: yaw
    //purpose: increment the camera's current yaw rotation
    public void yaw(float amount)
    {
        yaw += amount;
    }
    //method: pitch
    //purpose: increment the camera's current yaw rotation
    public void pitch(float amount)
    {
        pitch -= amount;
    }

    //method: walkForward
    //purpose: move the camera forward
    public void walkForward(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: walkBackwards
    //purpose: moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    //method: strafeLeft
    //purpose: strafes the camera left relative to its current rotation (yaw)
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: strafeRight
    //purpose: strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: moveUp
    //purpose: moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance)
    {
        position.y -= distance;
    }
    
    //method: moveDown
    //purpose: moves the camera down
    public void moveDown(float distance)
    {
        position.y += distance;
    }

    //method: lookThrough
    // purpose: translates and rotate the matrix so that it looks through the camera
    public void lookThrough()
    {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    //method: gameLoop
    //purpose: continuously update the screen
    public void gameLoop()
    {
        FPCameraController camera = new FPCameraController(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //length of frame
        float lastTime = 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
            
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))
                camera.walkForward(movementSpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                camera.walkBackwards(movementSpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
                camera.strafeLeft(movementSpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
                camera.strafeRight(movementSpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Keyboard.isKeyDown(Keyboard.KEY_Q))//move up {
                camera.moveUp(movementSpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_E) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) 
                camera.moveDown(movementSpeed);
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            chunk.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    //method: render
    //purpose: draw the desired scene
    private void render() {
        try{
            glBegin(GL_QUADS);
            //right, Blue
                glColor3f(0.0f,0.0f,1.0f);
                glVertex3f( 1.0f,1.0f,-1.0f);
                glVertex3f(1.0f,1.0f,1.0f);
                glVertex3f(1.0f, -1.0f,1.0f);
                glVertex3f( 1.0f, -1.0f,-1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //top, R+B - purple
                glColor3f(1.0f,0.0f,1.0f);
                glVertex3f( 1.0f,1.0f,-1.0f);
                glVertex3f(-1.0f,1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,1.0f);
                glVertex3f( 1.0f, 1.0f,1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //bottom, Red
                glColor3f(1.0f,0.0f,0.0f);
                glVertex3f( 1.0f,-1.0f,1.0f);
                glVertex3f(-1.0f,-1.0f,1.0f);
                glVertex3f(-1.0f, -1.0f,-1.0f);
                glVertex3f( 1.0f, -1.0f,-1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //front, G+B 
                glColor3f(0.0f,1.0f,1.0f);
                glVertex3f( 1.0f,1.0f,1.0f);
                glVertex3f(-1.0f,1.0f,1.0f);
                glVertex3f(-1.0f, -1.0f,1.0f);
                glVertex3f( 1.0f, -1.0f,1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //back, R+G - yellow
                glColor3f(1.0f,1.0f,1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //left, Green
                glColor3f(0.0f,1.0f,0.0f);
                glVertex3f(-1.0f,1.0f,1.0f);
                glVertex3f(-1.0f,1.0f,-1.0f);
                glVertex3f(-1.0f, -1.0f,-1.0f);
                glVertex3f(-1.0f, -1.0f,1.0f);
            glEnd();
            
        }catch(Exception e){
        }
    }
    }


