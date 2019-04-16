/***************************************************************
* file: Chunk.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/28/19
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
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    public int startX,startY,startZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    private SimplexNoise noise;
    private int seed;
    
   
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
    public void rebuildMesh(float Startx, float Starty, float Startz) {
        int[][] materialBoundaries = new int[4][4];
        //sand
        materialBoundaries[0][0] = r.nextInt(10); //x-min
        materialBoundaries[0][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[0][2] = r.nextInt(10); //z-min
        materialBoundaries[0][3] = r.nextInt(10) + 10; //z-max
        //water
        materialBoundaries[1][0] = r.nextInt(10); //x-min
        materialBoundaries[1][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[1][2] = r.nextInt(10); //z-min
        materialBoundaries[1][3] = r.nextInt(10) + 10; //z-max
        //dirt
        materialBoundaries[2][0] = r.nextInt(5); //x-min
        materialBoundaries[2][1] = r.nextInt(5) + 5; //x-max
        materialBoundaries[2][2] = r.nextInt(5); //z-min
        materialBoundaries[2][3] = r.nextInt(5) + 5; //z-max
        //stone
        materialBoundaries[3][0] = r.nextInt(5); //x-min
        materialBoundaries[3][1] = r.nextInt(5) + 5; //x-max
        materialBoundaries[3][2] = r.nextInt(5); //z-min
        materialBoundaries[3][3] = r.nextInt(5) + 5; //z-max
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        int height;
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        for (float x = -Startx; x < -Startx +CHUNK_SIZE; x++) {
            for (float z = -Startz; z < -Startz + CHUNK_SIZE; z++) {
                height = (int) (((noise.getNoise((int)x + (int)Startx/2, (int)z + (int)Startz/2)) + 1) / 2 * 10) + 5;
                for (float y = 0; y <= height; y++) {
                    if(y <= height){
                        if(y == height)
                            blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                        //check sand
                        if(checkMaterialBoundaries(0, (int)x, (int)y, (int)z, materialBoundaries)){
                            blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                        //check water
                        else if(checkMaterialBoundaries(1, (int)x, (int)y, (int)z, materialBoundaries)){
                            blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                        }
                        //check dirt
                        else if(checkMaterialBoundaries(2, (int)x, (int)y, (int)z, materialBoundaries)){
                            blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                        }
                        //check stone
                        else if(checkMaterialBoundaries(3, (int)x, (int)y, (int)z, materialBoundaries)){
                            blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Stone);
                        }
                    }
                    VertexPositionData.put(createCube((float)(Startx + x * CUBE_LENGTH), 
                            (float)(Starty + y * CUBE_LENGTH), 
                            (float)(Startz + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x + (int)Startx][(int) y][(int) z + (int)Startz])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, blocks[(int) x + (int)Startx][(int) y][(int) z + (int)Startz]));
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
    
    //method: checkMaterialBoundaries
    //purpose: check if block at x, y, z is within valid boundaries to generate
    //terrain texture of type (0:sand, 1:water, 2:dirt, 3:stone)
    private boolean checkMaterialBoundaries(int type, int x, int y, int z, int[][] bounds){
        if(x >= bounds[type][0] &&
           x <= bounds[type][1] &&
           z >= bounds[type][2] &&
           z <= bounds[type][3])
        {
            return true;
        }
        return false;
    }
    
    
    //method: createCubeVertexCol
    //purpose: create an array of the cube vertices
    
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4*6];
        if (cubeColorArray[0] == 0) {
            for (int i = 0; i < cubeColors.length; i++) {
                cubeColors[i] = 1;
            }
            for (int i = 0; i < 12; i += 3) {
                cubeColors[i] = 0.8f;
                cubeColors[i + 1] = 1;
                cubeColors[i + 2] = 0.5f;
            }
        }else{
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        }
        return cubeColors;
    }
    
    //method: createCube
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
    private float[] getCubeColor(Block block) {
        //grass
        if (block.getID() == 0 ) {
            return new float[] {0, 0, 0};
        }
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
    public Chunk(int startX, int startY, int startZ, int s) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.err.println("Error loading terrain: terrain.png not found");
        }
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        seed = s;
        noise = new SimplexNoise(120, 0.5, seed);
        r = new Random();
        int height;
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    float random = r.nextFloat();
                    /*To-do: add water*/
                    if(y == 0)
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    else if(y == 1 || y == 2){
                        if(random > 0.5f)
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        else
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else{
                        if(random > 0.4f)
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        else
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                }
            }
       }

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        rebuildMesh(startX, startY, startZ);
    } 
       
    
}
