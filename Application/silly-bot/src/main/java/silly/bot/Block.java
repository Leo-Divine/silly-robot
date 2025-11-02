package silly.bot;

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
            return BlockPaths.drawOperandBlock(position, width, height);
        }
    },
    Value {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawValueBlock(position, width, height);
        }
    },
    Nesting {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawNestingBlock(position, width, height, 25);
        }
    },
    DoubleNesting {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDoubleNestingBlock(position, width, height, 25, 25);
        }
    },
    Start {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawStartBlock(position, width, height);
        }
    };

    abstract Path getPath(Position position, int width, int height);
}

enum BlockType {
    MoveForward(BlockShape.Default, BlockCategory.Movement, 150, 35, 2, 75),
    RotateLeft(BlockShape.Default, BlockCategory.Movement, 150, 35, 0, 150),
    RotateRight(BlockShape.Default, BlockCategory.Movement, 150, 35, 0, 225),
    SetColor(BlockShape.Default, BlockCategory.Display, 100, 35, 1, 350),
    GetSensorValue(BlockShape.Value, BlockCategory.Sensors, 100, 30, 0, 475),
    Wait(BlockShape.Default, BlockCategory.Control, 100, 35, 1, 600),
    If(BlockShape.Nesting, BlockCategory.Control, 150, 40, 3, 675),
    IfEl(BlockShape.DoubleNesting, BlockCategory.Control, 150, 40, 3, 750),
    Loop(BlockShape.Nesting, BlockCategory.Control, 150, 40, 2, 825),
    Equal(BlockShape.Operand, BlockCategory.Operands, 100, 35, 2, 950),
    Less(BlockShape.Operand, BlockCategory.Operands, 100, 35, 2, 1025),
    Greater(BlockShape.Operand, BlockCategory.Operands, 100, 35, 2, 1100),
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

public abstract class Block {
    static int nextBlockId = 1;
    static double borderWidth = 1.15;
    private int id;
    BlockType blockType;
    int aboveBlock = 0;
    int belowBlock = 0;
    Position position = new Position(0, 0);
    boolean isDragging = false;
    Position mouseOffset = new Position(0, 0);
    protected int baseWidth;
    protected int baseHeight;

    public Block(BlockType type, Position position) {
        this.id = nextBlockId;
        nextBlockId++;
        this.blockType = type;
        this.position = position;
        position.x -= 325;
        this.baseWidth = blockType.startWidth;
        this.baseHeight = blockType.startHeight;
    }

    public int getId() {
        return id;
    }

    abstract int getWidth();
    abstract int getHeight();
    abstract Path getPath();

    public boolean isMouseOnBlock(Position mousePosition) {
        return getPath().contains(mousePosition.x, mousePosition.y);
    }

    public GraphicsContext drawBlock(GraphicsContext gc) {
        gc.setStroke(blockType.category.border);
        gc.setLineWidth(borderWidth);
        gc.setFill(blockType.category.fill);

        gc.beginPath();
        String test = BlockPaths.pathToString(getPath());
        gc.appendSVGPath(test);
        gc.closePath();
        gc.fill();
        gc.stroke();
        return gc;
    }
}

class DefaultBlock extends Block {
    public DefaultBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawDefaultBlock(position, baseWidth, baseHeight);
    }
}

class ValueBlock extends Block {
    public ValueBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawValueBlock(position, baseWidth, baseHeight);
    }
}

class OperandBlock extends Block {
    public OperandBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawOperandBlock(position, baseWidth, baseHeight);
    }
}

class StartBlock extends Block {
    public StartBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight;
    }

    public String getCode() {
        return "";
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawStartBlock(position, baseWidth, baseHeight);
    }
}

class NestingBlock extends Block {
    int nestedBlock;

    public NestingBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight + getTotalNestingBlockHeight() + 24 + 16;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawNestingBlock(position, baseWidth, baseHeight, getTotalNestingBlockHeight());
    }

    private int getTotalNestingBlockHeight() {
        return 25;
    }
}

class DoubleNestingBlock extends Block {
    int firstNestedBlock;
    int secondNestedBlock;

    public DoubleNestingBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        // TODO Add Changing Width With Value and Operand Blocks
        return baseWidth;
    }

    public int getHeight() {
        return baseHeight + getFirstNestingBlockHeight() + getSecondNestingBlockHeight() + 24 + 24 + 32;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawDoubleNestingBlock(position, baseWidth, baseHeight, getFirstNestingBlockHeight(), getSecondNestingBlockHeight());
    }

    private int getFirstNestingBlockHeight() {
        return 25;
    }

    private int getSecondNestingBlockHeight() {
        return 25;
    }
}