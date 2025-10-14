package silly.bot;

import javafx.scene.image.Image; 

enum BlockType {
    //Wait(""),
    SetSpeed(new Image(Editor.class.getResource("/temp.png").toExternalForm())),
    RotateLeft(new Image(Editor.class.getResource("/temp2.jpeg").toExternalForm())),
    RotateRight(new Image(Editor.class.getResource("/temp3.jpeg").toExternalForm())),
    SetColor(new Image(Editor.class.getResource("/temp4.jpeg").toExternalForm()));

    public final Image image;

    private BlockType(Image image) {
        this.image = image;
    }
}

public class Block {
    static int nextBlockId = 1;
    private int id;
    BlockType blockType;
    int aboveBlock;
    int belowBlock;
    double xPos = 0.0;
    double yPos = 0.0;
    boolean isDragging = false;
    double mouseOffsetX = 0;
    double mouseOffsetY = 0;
    
    public Block(BlockType b) {
        this.id = nextBlockId;
        nextBlockId++;
        blockType = b;
    }

    public Block(BlockType b, double xPos, double yPos) {
        this.id = nextBlockId;
        nextBlockId++;
        this.blockType = b;
        this.xPos = xPos - 325;
        this.yPos = yPos;
    }

    public int getId() {
        return id;
    }

    public boolean isMouseOnBlock(double mouseX, double mouseY) {
        return mouseX - 325 >= xPos && 
        mouseX - 325 <= xPos + 100 &&
        mouseY >= yPos &&
        mouseY <= yPos + 50;
    }
}