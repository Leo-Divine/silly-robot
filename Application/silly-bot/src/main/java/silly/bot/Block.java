package silly.bot;

import java.io.Serializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawDefaultBlock(gc, block);
        }
    },
    Operand {
        @Override
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawDefaultBlock(gc, block);
        }
    },
    Value {
        @Override
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawDefaultBlock(gc, block);
        }
    },
    Nesting {
        @Override
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawDefaultBlock(gc, block);
        }
    },
    DoubleNesting {
        @Override
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawDefaultBlock(gc, block);
        }
    },
    Start {
        @Override
        public GraphicsContext drawBlock(GraphicsContext gc, Block block) {
            return BlockPaths.drawStartBlock(gc, block);
        }
    };

    abstract GraphicsContext drawBlock(GraphicsContext gc, Block block);
}

enum BlockType {
    MoveForward(BlockShape.Default, BlockCategory.Movement, 150, 25, 2),
    RotateLeft(BlockShape.Default, BlockCategory.Movement, 150, 25, 0),
    RotateRight(BlockShape.Default, BlockCategory.Movement, 150, 25, 0),
    SetColor(BlockShape.Default, BlockCategory.Display, 100, 50, 1),
    GetSensorValue(BlockShape.Value, BlockCategory.Sensors, 100, 50, 0),
    Wait(BlockShape.Default, BlockCategory.Control, 100, 50, 1),
    If(BlockShape.Nesting, BlockCategory.Control, 100, 50, 3),
    IfEl(BlockShape.DoubleNesting, BlockCategory.Control, 100, 50, 3),
    Loop(BlockShape.Nesting, BlockCategory.Control, 100, 50, 2),
    Equal(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2),
    Less(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2),
    Greater(BlockShape.Operand, BlockCategory.Operands, 100, 50, 2),
    Start(BlockShape.Start, BlockCategory.Start, 125, 25, 0);

    public final BlockShape shape;
    public final BlockCategory category;
    public final int startWidth;
    public final int startHeight;
    public final int parameterCount;

    private BlockType(BlockShape shape, BlockCategory category, int startWidth, int startHeight, int parameterCount) {
        this.shape = shape;
        this.category = category;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.parameterCount = parameterCount;
    }
}

abstract class Block implements Serializable {
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

    public Block(BlockType b, double xPos, double yPos) {
        this.id = nextBlockId;
        nextBlockId++;
        this.blockType = b;
        this.position = new Position(xPos - 325, yPos);
    }

    public int getId() {
        return id;
    }

    public boolean isMouseOnBlock(double mouseX, double mouseY) {
        return mouseX - 325 >= position.x && 
        mouseX - 325 <= position.x + width &&
        mouseY >= position.y &&
        mouseY <= position.y + height;
    }

    public String getJSONCode() {
        return "";
    }

    public GraphicsContext drawBlock(GraphicsContext gc) {
        return blockType.shape.drawBlock(gc, this);
    }
}

class Start extends Block {
    public Start(double xPos, double yPos) {
        super(BlockType.Start, xPos, yPos);
        width = BlockType.Start.startWidth;
        height = BlockType.Start.startHeight;
        menuPosition = 0;
    }
}

class MoveForward extends Block {
    public MoveForward(double xPos, double yPos) {
        super(BlockType.MoveForward, xPos, yPos);
        width = BlockType.MoveForward.startWidth;
        height = BlockType.MoveForward.startHeight;
        menuPosition = 75;
    }
}