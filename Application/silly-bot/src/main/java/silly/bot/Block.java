package silly.bot;

import java.io.Serializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

enum BlockCategory {
    Start(Color.rgb(76, 151, 255), Color.rgb(51, 115, 204), 0, 0),
    Movement(Color.rgb(255, 102, 128), Color.rgb(255, 51, 85), 25, 50),
    Display(Color.rgb(89, 192, 89), Color.rgb(56, 148, 56), 75, 325),
    Sensors(Color.rgb(92, 177, 214), Color.rgb(46, 142, 184), 125, 450),
    Control(Color.rgb(153, 102, 255), Color.rgb(119, 77, 203), 175, 575),
    Operands(Color.rgb(255, 171, 25), Color.rgb(207, 139, 23), 225, 925);

    public final Color fill;
    public final Color border;
    public final int menuButtonPos;
    public final int menuTextPos;

    private BlockCategory(Color fill, Color border, int menuButtonPos, int menuTextPos) {
        this.fill = fill;
        this.border = border;
        this.menuButtonPos = menuButtonPos;
        this.menuTextPos = menuTextPos;
    }
}

enum BlockShape {
    Default {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    Operand {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    Value {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    Nesting {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    DoubleNesting {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    Start {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    };

    abstract Path getPath(Position position, int width, int height);
}

enum BlockType {
    MoveForward(BlockShape.Default, BlockCategory.Movement, 150, 30, 2, 75),
    RotateLeft(BlockShape.Default, BlockCategory.Movement, 150, 25, 0, 150),
    RotateRight(BlockShape.Default, BlockCategory.Movement, 150, 25, 0, 225),
    SetColor(BlockShape.Default, BlockCategory.Display, 100, 50, 1, 350),
    GetSensorValue(BlockShape.Value, BlockCategory.Sensors, 100, 50, 0, 475),
    Wait(BlockShape.Default, BlockCategory.Control, 100, 50, 1, 600),
    If(BlockShape.Nesting, BlockCategory.Control, 100, 50, 3, 675),
    IfEl(BlockShape.DoubleNesting, BlockCategory.Control, 100, 50, 3, 750),
    Loop(BlockShape.Nesting, BlockCategory.Control, 100, 50, 2, 825),
    Equal(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2, 950),
    Less(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2, 1025),
    Greater(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2, 1100),
    Start(BlockShape.Start, BlockCategory.Start, 125, 25, 0, 0);

    public final BlockShape shape;
    public final BlockCategory category;
    public final int startWidth;
    public final int startHeight;
    public final int parameterCount;
    public final int menuPosition;

    private BlockType(BlockShape shape, BlockCategory category, int startWidth, int startHeight, int parameterCount, int menuPosition) {
        this.shape = shape;
        this.category = category;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.parameterCount = parameterCount;
        this.menuPosition = menuPosition;
    }
}

public class Block implements Serializable {
    static int nextBlockId = 1;
    private int id;
    BlockType blockType;
    int aboveBlock = 0;
    int belowBlock = 0;
    Position position = new Position(0, 0);
    boolean isDragging = false;
    Position mouseOffset = new Position(0, 0);
    int width;
    int height;
    int menuPosition;
    Path blockPath;

    public Block(BlockType b, double xPos, double yPos) {
        this.id = nextBlockId;
        nextBlockId++;
        this.blockType = b;
        this.position = new Position(xPos - 325, yPos);
        this.width = BlockType.Start.startWidth;
        this.height = BlockType.Start.startHeight;
        this.menuPosition = blockType.menuPosition;
        this.blockPath = blockType.shape.getPath(position, width, height);
    }

    public int getId() {
        return id;
    }

    public boolean isMouseOnBlock(double mouseX, double mouseY) {
        return blockPath.contains(mouseX, mouseY);
    }

    public String getJSONCode() {
        return "";
    }

    public GraphicsContext drawBlock(GraphicsContext gc) {
        blockPath = blockType.shape.getPath(position, width, height);

        gc.setStroke(blockType.category.border);
        gc.setFill(blockType.category.fill);

        gc.beginPath();
        gc.appendSVGPath(BlockPaths.pathToString(blockPath));
        gc.closePath();
        gc.fill();
        return gc;
    }
}