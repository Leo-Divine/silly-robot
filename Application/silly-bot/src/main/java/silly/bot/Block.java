package silly.bot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

enum BlockCategory {
    Start(Color.rgb(76, 151, 255), Color.rgb(51, 115, 204)),
    Movement(Color.rgb(255, 102, 128), Color.rgb(255, 51, 85)),
    Display(Color.rgb(89, 192, 89), Color.rgb(56, 148, 56)),
    Sound(Color.rgb(255, 140, 26), Color.rgb(219, 110, 0)),
    Sensors(Color.rgb(92, 177, 214), Color.rgb(46, 142, 184)),
    Control(Color.rgb(153, 102, 255), Color.rgb(119, 77, 203)),
    Operands(Color.rgb(255, 171, 25), Color.rgb(207, 139, 23));

    public final Color fill;
    public final Color border;

    private BlockCategory(Color fill, Color border) {
        this.fill = fill;
        this.border = border;
    }
}

enum BlockShape {
    Default(new Position(5, 30)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDefaultBlock(position, width, height);
        }
    },
    Operand(new Position(15, 25)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawOperandBlock(position, width, height);
        }
    },
    Value(new Position(5, 22)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawValueBlock(position, width, height);
        }
    },
    Nesting(new Position(5, 30)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawNestingBlock(position, width, height, 25);
        }
    },
    DoubleNesting(new Position(5, 30)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawDoubleNestingBlock(position, width, height, 25, 25);
        }
    },
    Start(new Position(5, 27)) {
        @Override
        public Path getPath(Position position, int width, int height) {
            return BlockPaths.drawStartBlock(position, width, height);
        }
    };

    public final Position labelOffset;

    private BlockShape(Position labelOffset) {
        this.labelOffset = labelOffset;
    }

    abstract Path getPath(Position position, int width, int height);
}

enum BlockType {
    MoveForward(BlockShape.Default, BlockCategory.Movement, 135, 35, "Move At [  ] Speed For [  ] Seconds"),
    RotateLeft(BlockShape.Default, BlockCategory.Movement, 95, 35, "Turn Left"),
    RotateRight(BlockShape.Default, BlockCategory.Movement, 105, 35, "Turn Right"),
    SetLeftColor(BlockShape.Default, BlockCategory.Display, 100, 35, "Set The Left Color To [  ]"),
    SetRightColor(BlockShape.Default, BlockCategory.Display, 100, 35, "Set The Right Color To [  ]"),
    PlayNote(BlockShape.Default, BlockCategory.Sound, 100, 35, "Play [  ] For [  ] Seconds"),
    StopPlaying(BlockShape.Default, BlockCategory.Sound, 100, 35, "Stop Playing Note"),
    GetSensorValue(BlockShape.Value, BlockCategory.Sensors, 100, 30, "Get Front Distance"),
    Wait(BlockShape.Default, BlockCategory.Control, 100, 35, "Wait [  ] Seconds"),
    If(BlockShape.Nesting, BlockCategory.Control, 150, 35, "If {  } Then"),
    IfEl(BlockShape.DoubleNesting, BlockCategory.Control, 150, 35, "If {  } Then"),
    Loop(BlockShape.Nesting, BlockCategory.Control, 150, 35, "Repeat [  ] Times"),
    Equal(BlockShape.Operand, BlockCategory.Operands, 100, 35, "[  ] = [  ]"),
    Less(BlockShape.Operand, BlockCategory.Operands, 100, 35, "[  ] < [  ]"),
    Greater(BlockShape.Operand, BlockCategory.Operands, 100, 35, "[  ] > [  ]"),
    Start(BlockShape.Start, BlockCategory.Start, 167, 30, "On Program Start");

    public final BlockShape shape;
    public final BlockCategory category;
    public final int startWidth;
    public final int startHeight;
    public final String label;

    private BlockType(BlockShape shape, BlockCategory category, int startWidth, int startHeight, String label) {
        Text labelWidthCheck = new Text(label);
        labelWidthCheck.setFont(Editor.COOL_FONT);
        
        this.shape = shape;
        this.category = category;
        this.startWidth = (int)(Math.round(labelWidthCheck.getLayoutBounds().getWidth()) + shape.labelOffset.x * 2);
        this.startHeight = startHeight;
        this.label = label;
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
    Parameter parameters[] = new Parameter[3];

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
        gc.setFont(Editor.COOL_FONT);
        gc.setStroke(blockType.category.border);
        gc.setLineWidth(borderWidth);
        gc.setFill(blockType.category.fill);

        gc.beginPath();
        gc.appendSVGPath(BlockPaths.pathToString(getPath()));
        gc.closePath();
        gc.fill();
        gc.stroke();

        gc.setFill(Color.WHITE);
        gc.fillText(blockType.label, position.x + 325 + blockType.shape.labelOffset.x, position.y + blockType.shape.labelOffset.y);
        
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

class BlockLabel {
    String baseLabel;
    int parameterCount;
    Parameter parameters[] = new Parameter[3];

    public BlockLabel(String label) {
        this.baseLabel = label;
        this.parameterCount = label.split("[]").length - 1;
    }

    public int getWidth() {
        return -1;
    }

    public GraphicsContext drawBlockLabel(GraphicsContext gc) {
        return gc;
    }
}