package silly.bot;

import javafx.scene.image.Image;
import java.net.URL;

enum BlockType {
    Wait(""),
    SetSpeed("/temp.png"),
    Move(""),
    RotateLeft(""),
    RotateRight(""),
    SetLeftColor(""),
    SetRightColor("");

    public final String imagePath;

    private BlockType(String imagePath) {
        this.imagePath = imagePath;
    }
}

public class Block {
    private int id;
    BlockType blockType;
    int aboveBlock;
    int belowBlock;
    double xPos = 0.0;
    double yPos = 0.0;
    
    public Block(int id, BlockType b) {
        this.id = id;
        blockType = b;
    }

    public int getId() {
        return id;
    }
}