package silly.bot;

import java.io.Serializable;

import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath; 

enum BlockShape {
    Default,
    Operand,
    Value,
    Nesting,
    DoubleNesting,
    Start;
}

enum BlockType {
    MoveForward(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 75, BlockShape.Default, 100, 50, 2),
    RotateLeft(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 150, BlockShape.Default, 100, 50, 0),
    RotateRight(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 225, BlockShape.Default, 100, 50, 0),
    SetColor(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 350, BlockShape.Default, 100, 50, 1),
    GetSensorValue(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 475, BlockShape.Value, 100, 50, 0),
    Wait(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 600, BlockShape.Default, 100, 50, 1),
    If(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 675, BlockShape.Nesting, 100, 50, 3),
    IfEl(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 750, BlockShape.DoubleNesting, 100, 50, 3),
    Loop(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 825, BlockShape.Nesting, 100, 50, 2),
    Equal(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 950, BlockShape.Operand, 100, 50, 2),
    Less(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 1025, BlockShape.Operand, 100, 50, 2),
    Greater(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 1100, BlockShape.Operand, 100, 50, 2),
    Start(new Image(Editor.class.getResource("/temp.png").toExternalForm()), 0, BlockShape.Start, 100, 50, 0);

    public final Image image;
    public final int menuPositionY;
    public final BlockShape shape;
    public final int startWidth;
    public final int startHeight;
    public final int parameterCount;

    private BlockType(Image image, int posY, BlockShape shape, int width, int height, int parameterCount) {
        this.image = image;
        this.menuPositionY = posY;
        this.shape = shape;
        this.startWidth = width;
        this.startHeight = height;
        this.parameterCount = parameterCount;
    }
}

public class Block implements Serializable {
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
    int[] parameters = {0, 0, 0};
    
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
        mouseX - 325 <= xPos + blockType.startWidth &&
        mouseY >= yPos &&
        mouseY <= yPos + blockType.startHeight;
    }

    public String getJSONCode() {
        return "";
    }
}