package silly.bot;

enum BlockType {
    Wait(""),
    SetSpeed(""),
    Move(""),
    RotateLeft(""),
    RotateRight(""),
    SetLeftColor(""),
    SetRightColor("");

    public final String image;

    private BlockType(String image) {
        this.image = image;
    }
}

public class Block {
    private int id;
    private BlockType blockType;
    private int aboveBlock;
    private int belowBlock;
    private double xPos = 0.0;
    private double yPos = 0.0;
    
    public Block(BlockType b) {
        blockType = b;
    }
}