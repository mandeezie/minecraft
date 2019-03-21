/***************************************************************
* file: Block.java
* author:   Amanda Cosentino
*           Diana Choi
*           Jacky Yang
*           Alexandra Hunter
* class: CS 4450
*
* assignment: program 3
* date last modified: 3/21/19
*
* purpose: create a block class to differentiate between blocks
* 
****************************************************************/ 
public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;
    
    //method: BlockType
    //purpose: create an enumerated list consisting of the block types
    public enum BlockType{
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int BlockID;
        
        //method: BlockType
        //purpose: set the block type to the specified ID
        BlockType(int i){
            BlockID = i;
        }
        
        //method: getID
        //purpose: return the BlockID
        public int getID(){
            return BlockID;
        }
        
        //method: setID
        //purpose: change the current BlockID to the specified one
        public void setID(int i)
        {
            BlockID = i;
        }
    }
    
    //method: Block
    //purpose: create a block with the specified BlockType
    public Block(BlockType type)
    {
        this.type = type;
    }
    
    //method: setCoords
    //purpose: set the specified coordinates
    public void setCoords(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: isActive
    //purpose: return whether or not the block is active
    public boolean isActive()
    {
        return isActive;
    }
    
    //method: setActive
    //purpose: change whether the block is active or not
    public void setActive(boolean active)
    {
        isActive = active;
    }
    
    //method: getID
    //purpose: return the BlockID type
    public int getID()
    {
        return type.getID();
    }
}
