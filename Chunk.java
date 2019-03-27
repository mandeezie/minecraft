/***************************************************************
* file: Chunk.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/27/19
*
* purpose: create a chunk of blocks
* 
****************************************************************/ 
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    //method: render
    //purpose: render the entire chunk of blocks
    public void render(){
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: rebuild the mesh
    public void rebuildMesh(float Startx, float Starty, float Startz){
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers(); 
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        int max = 15;
        SimplexNoise noise = new SimplexNoise(max, .3, 6);
        
        for(float x = 0; x < CHUNK_SIZE; x++)
        {
            //int i = (int)(xStart + x * ((XEnd-xStart)/xResolution));
            for(float z = 0; z < CHUNK_SIZE; z++)
            {
                for(float y = 0; y < CHUNK_SIZE; y++)
                {
                    float height = (Starty + (int)(100*noise.getNoise((int)x, (int)y, (int)z)) * CUBE_LENGTH);
                    VertexPositionData.put(createCube((float)(Startx + x * CUBE_LENGTH), 
                            (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)), 
                            (float)(Startz + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int)x][(int)y][(int)z])));
                    VertexTextureData.put(createTexCube((float) 0, (float)0,Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
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
        return new float[] {1, 1, 1};
    }
    
    
    //method: createTexCube
    //purpose: create the texture mapping for each cube
    public static float[] createTexCube(float x, float y, Block block) 
    {
        float offset = (1024f/16)/1024f;
        switch (block.getID()) {
            case 0://grass
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
                
            case 1://sand
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2};
                
            case 2://water
                return new float[] {
                // BOTTOM QUAD
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // TOP
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // FRONT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1,
                // BACK QUAD
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // LEFT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                // RIGHT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1}; 
                
            case 3://dirt
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // TOP
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
                
            case 4://stone
                return new float[] {
                // BOTTOM QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // TOP
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // FRONT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // RIGHT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1};
              
            case 5://bedrock
                return new float[] {
                // BOTTOM QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // TOP
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2};
                
                default:
                return new float[] { 1, 1, 1 };
        }
    }
 
    //method: Chunk
    //purpose: create the chunk of blocks relative to the starting position
    public Chunk(int Startx, int Starty, int Startz){
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch(Exception e){
        }
        
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int y = 0; y < CHUNK_SIZE; y++)
            {
                for(int z = 0; z < CHUNK_SIZE; z++)
                {
                    if(r.nextFloat() > 0.8f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    else if(r.nextFloat() > 0.7f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    else if(r.nextFloat() > 0.5f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }
                    else if(r.nextFloat() > 0.3f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                    else if(r.nextFloat() > 0.2f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    else{
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        startX = Startx;
        startY = Starty;
        startZ = Startz;
        rebuildMesh(Startx, Starty, Startz);
    }            
}

