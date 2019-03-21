/***************************************************************
* file: Chunk.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/21/19
*
* purpose: create a chunk of blocks
* 
****************************************************************/ 
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    
    //method: render
    //purpose: render the entire chunk of blocks
    public void render(){
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: rebuild the mesh
    public void rebuildMesh(float Startx, float Starty, float Startz){
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE) * 6 * 12);
        for(float x = 0; x < CHUNK_SIZE; x++)
        {
            for(float z = 0; z < CHUNK_SIZE; z++)
            {
                for(float y = 0; y < CHUNK_SIZE; y++)
                {
                    VertexPositionData.put(createCube((float)(Startx + x * CUBE_LENGTH), 
                            (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)), 
                            (float)(Startz + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int)x][(int)y][(int)z])));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: createCubeVertexCol
    //purpose: create an array of the cube vertices
    private float[] createCubeVertexCol(float[] CubeColorArray){
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for(int i = 0; i < cubeColors.length; i++)
        {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //mehtod: createCube
    //purpose: create the cube at the given location 
    public static float[] createCube(float x, float y, float z)
    {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            //TOP
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //BOTTOM
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            //FRONT
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            //BACK
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            //LEFT
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            //RIGHT
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }
    
    //method: getCubeColor
    //purpose: return the current cube color of the given block
    private float[] getCubeColor(Block block)
    {
        switch(block.getID())
        {
            case 1:
                return new float[] {0, 1, 0};
            case 2:
                return new float[] {1, .5f, 0};
            case 3:
                return new float[] {0, 0, 1};
        }
        return new float[]{1, 1, 1};
    }
    
    //method: Chunk
    //purpose: create the chunk of blocks relative to the starting position
    public Chunk(int Startx, int Starty, int Startz){
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int y = 0; y < CHUNK_SIZE; y++)
            {
                for(int z = 0; z < CHUNK_SIZE; z++)
                {
                    if(r.nextFloat() > 0.7f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    else if(r.nextFloat() > 0.4f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    else if(r.nextFloat() > 0.2f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }
                    //DEFAULT VALUE IS???????
                    else{
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        startX = Startx;
        startY = Starty;
        startZ = Startz;
        rebuildMesh(Startx, Starty, Startz);
    }            
}

