package silly.bot;

import javafx.scene.image.Image; 

enum BlockType {
    //Wait(""),
    SetSpeed(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 75),
    RotateLeft(new Image(Editor.class.getResource("/temp2.jpeg").toExternalForm()), 150),
    RotateRight(new Image(Editor.class.getResource("/temp3.jpeg").toExternalForm()), 225),
    SetColor(new Image(Editor.class.getResource("/temp4.jpeg").toExternalForm()), 350);

    public final Image image;
    public final int menuPositionY;

    private BlockType(Image image, int posY) {
        this.image = image;
        this.menuPositionY = posY;
    }
}

public class Block {
    static int nextBlockId = 1;
    private int id;
    BlockType blockType;
    int aboveBlock = 0;
    int belowBlock = 0;
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