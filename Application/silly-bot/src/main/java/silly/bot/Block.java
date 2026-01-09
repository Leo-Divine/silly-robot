package silly.bot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

/**
 * <h3>BlockCategory</h3>
 * <h4>The category of a block.</h4>
 * <p>The blocks are seperated into categories based on what they make the robot actually do.</p>
 */
enum BlockCategory {
    /**
     * Contains blocks that execute a series of blocks.
     */
    Start(Color.rgb(76, 151, 255), Color.rgb(51, 115, 204)),
    /**
     * Contains blocks that physically move the robot.
     */
    Movement(Color.rgb(255, 102, 128), Color.rgb(255, 51, 85)),
    /**
     * Contains blocks that changes the appearance of the robot.
     */
    Display(Color.rgb(89, 192, 89), Color.rgb(56, 148, 56)),
    /**
     * Contains blocks that produces sound.
     */
    Sound(Color.rgb(255, 140, 26), Color.rgb(219, 110, 0)),
    /**
     * Contains blocks that return values from the robot's sensors.
     */
    Sensors(Color.rgb(92, 177, 214), Color.rgb(46, 142, 184)),
    /**
     * Contains blocks that control the flow of code.
     */
    Control(Color.rgb(153, 102, 255), Color.rgb(119, 77, 203)),
    /**
     * Contains blocks that take values and perform calculations.
     */
    Operands(Color.rgb(255, 171, 25), Color.rgb(207, 139, 23));

    /**
     * The fill color of the blocks.
     */
    public final Color fill;
    /**
     * The stroke color of the blocks.
     */
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
    MoveForward(BlockShape.Default, BlockCategory.Movement, 326, 42, "Move At α Speed For α Seconds", new Parameter[]{new Parameter<Integer>(null, 128), new Parameter<Integer>(null, 1)}),
    RotateLeft(BlockShape.Default, BlockCategory.Movement, 98, 42, "Turn Left", null),
    RotateRight(BlockShape.Default, BlockCategory.Movement, 107, 42, "Turn Right", null),
    SetLeftColor(BlockShape.Default, BlockCategory.Display, 233, 42, "Set The Left Color To α ", new Parameter[]{new Parameter<Color>(null, Color.RED)}),
    SetRightColor(BlockShape.Default, BlockCategory.Display, 242, 42, "Set The Right Color To α ", new Parameter[]{new Parameter<Color>(null, Color.RED)}),
    PlayNote(BlockShape.Default, BlockCategory.Sound, 236, 42, "Play α For α Seconds", new Parameter[]{new Parameter<Notes>(null, Notes.NOTE_C4), new Parameter<Integer>(null, 1)}),
    StopPlaying(BlockShape.Default, BlockCategory.Sound, 173, 42, "Stop Playing Note", null),
    GetSensorValue(BlockShape.Value, BlockCategory.Sensors, 179, 30, "Get Front Distance", null),
    Wait(BlockShape.Default, BlockCategory.Control, 161, 42, "Wait α Seconds", new Parameter[]{new Parameter<Integer>(null, 1)}),
    If(BlockShape.Nesting, BlockCategory.Control, 116, 52, "If α Then", new Parameter[]{new Parameter<Block>(null, null)}),
    IfEl(BlockShape.DoubleNesting, BlockCategory.Control, 116, 52, "If α Then", new Parameter[]{new Parameter<Block>(null, null)}),
    Loop(BlockShape.Nesting, BlockCategory.Control, 161, 47, "Repeat α Times", new Parameter[]{new Parameter<Integer>(null, 10)}),
    Equal(BlockShape.Operand, BlockCategory.Operands, 98, 35, "α = α ", new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)}),
    Less(BlockShape.Operand, BlockCategory.Operands, 96, 35, "α < α ", new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)}),
    Greater(BlockShape.Operand, BlockCategory.Operands, 96, 35, "α > α ", new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)}),
    Start(BlockShape.Start, BlockCategory.Start, 167, 38, "On Program Start", null);

    public final BlockShape shape;
    public final BlockCategory category;
    public final int startWidth;
    public final int startHeight;
    public final String label;
    public final Parameter[] parameters;

    private BlockType(BlockShape shape, BlockCategory category, int startWidth, int startHeight, String label, Parameter[] parameters) {
        Text labelWidthCheck = new Text(label);
        labelWidthCheck.setFont(Editor.COOL_FONT);
        
        this.shape = shape;
        this.category = category;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.label = label;
        this.parameters = parameters;
    }
}

public abstract class Block {
    final static double BORDER_WIDTH = 1.15;

    static int nextBlockId = 1;
    private int id;
    BlockType blockType;
    Block aboveBlock = null;
    Block belowBlock = null;
    Block parentBlock = null;
    Position position = new Position(0, 0);
    boolean isDragging = false;
    Position mouseOffset = new Position(0, 0);
    protected int width;
    protected int height;
    Parameter[] parameters = new Parameter[]{};

    public Block(BlockType type, Position position) {
        this.id = nextBlockId;
        nextBlockId++;
        this.blockType = type;
        this.position = position;
        position.x -= Editor.MENU_WIDTH;
        this.width = blockType.startWidth;
        this.height = blockType.startHeight;

        switch(this.blockType) {
            case Equal: 
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)};
                break;
            case Greater:
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)};
                break;
            case If:
                this.parameters = new Parameter[]{new Parameter<Block>(null, null)};
                break;
            case IfEl:
                this.parameters = new Parameter[]{new Parameter<Block>(null, null)};
                break;
            case Less:
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 0), new Parameter<Integer>(null, 0)};
                break;
            case Loop:
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 10)};
                break;
            case MoveForward:
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 128), new Parameter<Integer>(null, 1)};
                break;
            case PlayNote:
                this.parameters = new Parameter[]{new Parameter<Notes>(null, Notes.NOTE_C4), new Parameter<Integer>(null, 1)};
                break;
            case SetLeftColor:
                this.parameters = new Parameter[]{new Parameter<Color>(null, Color.RED)};
                break;
            case SetRightColor:
                this.parameters = new Parameter[]{new Parameter<Color>(null, Color.BLUE)};
                break;
            case Wait:
                this.parameters = new Parameter[]{new Parameter<Integer>(null, 1)};
                break;
            default:
                break;
        }
    }

    public int getId() {
        return id;
    }

    abstract int getWidth();
    abstract int getHeight();
    abstract Path getPath();

    public int getConnectedBlockHeights() {
        if(belowBlock == null) {
            return getHeight();
        }
        return getHeight() + belowBlock.getConnectedBlockHeights();
    }

    public boolean isMouseOnBlock(Position mousePosition) {
        return getPath().contains(mousePosition.x, mousePosition.y);
    }

    public GraphicsContext drawBlock(GraphicsContext gc, int[] selectedParameter) {
        gc.setFont(Editor.COOL_FONT);
        gc.setStroke(blockType.category.border);
        gc.setLineWidth(BORDER_WIDTH);
        gc.setFill(blockType.category.fill);

        gc.beginPath();
        gc.appendSVGPath(BlockPaths.pathToString(getPath()));
        gc.closePath();
        gc.fill();
        gc.stroke();
        
        drawBlockText(gc, selectedParameter);
        return gc;
    }

    private GraphicsContext drawBlockText(GraphicsContext gc, int[] selectedParameter) {
        Text widthCheck = new Text();
        widthCheck.setFont(Editor.COOL_FONT);
        gc.setFill(Color.WHITE);
        gc.setStroke(blockType.category.border);

        // Display Only Text For no Parameters
        if(blockType.label.indexOf("α") == -1) {
            gc.fillText(blockType.label, position.x + Editor.MENU_WIDTH + blockType.shape.labelOffset.x, position.y + blockType.shape.labelOffset.y);
            return gc;
        }

        String[] stringParts = blockType.label.split("α");
        double xPos = position.x + Editor.MENU_WIDTH + blockType.shape.labelOffset.x;
        for(int i = 0; i < stringParts.length - 1; i++) {
            // Draw Block Text
            gc.setFill(Color.WHITE);
            gc.fillText(stringParts[i], xPos, position.y + blockType.shape.labelOffset.y);
            widthCheck.setText(stringParts[i]);
            xPos += widthCheck.getLayoutBounds().getWidth();

            // Draw Parameter Circle
            parameters[i].labelPosition = new Position(xPos, position.y + height / 2 - Parameter.PARAMETER_SIZE / 2);
            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(parameters[i].getPath()));
            gc.closePath();

            // Give The Parameter a Selected Color if Selected
            if(id == selectedParameter[0] && i == selectedParameter[1]) {
                gc.setFill(Color.rgb(57, 155, 247));
            }
            gc.fill();
            gc.stroke();

            // Draw Parameter Value
            if(parameters[i].value == null) {
                gc.setFill(blockType.category.border);
                gc.setStroke(blockType.category.border);

                gc.beginPath();
                gc.appendSVGPath(BlockPaths.pathToString(parameters[i].getPath()));
                gc.closePath();
                gc.fill();
                gc.stroke();
            } else if(parameters[i].value.getClass() == Integer.class) {
                if(id == selectedParameter[0] && i == selectedParameter[1]) {
                    gc.setFill(Color.WHITE);
                } else {
                    gc.setFill(Color.BLACK);
                }
                gc.fillText(parameters[i].value.toString(), xPos + 5, position.y + blockType.shape.labelOffset.y);
            } else if(parameters[i].value.getClass() == Color.class) {
                gc.setFill((Color) parameters[i].value);
                gc.setStroke(Color.WHITE);

                gc.beginPath();
                gc.appendSVGPath(BlockPaths.pathToString(parameters[i].getPath()));
                gc.closePath();
                gc.fill();
                gc.stroke();
            } else if(parameters[i].value.getClass() == Notes.class) {
                // Redraw Circle
                gc.setFill(blockType.category.border);
                gc.setStroke(blockType.category.border);

                gc.beginPath();
                gc.appendSVGPath(BlockPaths.pathToString(parameters[i].getPath()));
                gc.closePath();
                gc.fill();
                gc.stroke();

                // Draw Text
                gc.setFill(Color.WHITE);
                gc.fillText(parameters[i].value.toString(), xPos + 5, position.y + blockType.shape.labelOffset.y);
            }
            xPos += parameters[i].getWidth();
        }
        gc.setFill(Color.WHITE);
        gc.fillText(stringParts[stringParts.length - 1], xPos, position.y + blockType.shape.labelOffset.y);

        // Set Block Width
        widthCheck.setText(stringParts[stringParts.length - 1]);
        xPos += widthCheck.getLayoutBounds().getWidth();
        if(blockType.shape == BlockShape.Operand) {
            width = (int) (xPos - (position.x + Editor.MENU_WIDTH + blockType.shape.labelOffset.x) + 27);
        } else {
            width = (int) (xPos - (position.x + Editor.MENU_WIDTH + blockType.shape.labelOffset.x) + 10);
        }
        
        return gc;
    }

    public void updateParameterPositions() {
        if(blockType.label.indexOf("α") == -1) { return; }
        Text widthCheck = new Text();
        widthCheck.setFont(Editor.COOL_FONT);

        String[] stringParts = blockType.label.split("α");
        double xPos = position.x + Editor.MENU_WIDTH + blockType.shape.labelOffset.x;
        for(int i = 0; i < stringParts.length - 1; i++) {
            // Draw Block Text
            widthCheck.setText(stringParts[i]);
            xPos += widthCheck.getLayoutBounds().getWidth();
            parameters[i].labelPosition = new Position(xPos, position.y + height / 2 - Parameter.PARAMETER_SIZE / 2);
            xPos += parameters[i].getWidth();
        }
    }

    protected void checkForConnectedBlocks() {
        if(belowBlock != null) {
            System.out.println(belowBlock.blockType.name());
            belowBlock.checkForConnectedBlocks();
        }
    }
}

class DefaultBlock extends Block {
    public DefaultBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawDefaultBlock(position, width, height);
    }
}

class ValueBlock extends Block {
    int parentParameter = -1;

    public ValueBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawValueBlock(position, width, height);
    }
}

class OperandBlock extends Block {
    int parentParameter = -1;

    public OperandBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawOperandBlock(position, width, height);
    }
}

class StartBlock extends Block {
    public StartBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawStartBlock(position, width, height);
    }
}

class NestingBlock extends Block {
    Block nestedBlock;

    public NestingBlock(BlockType type, Position position) {
        super(type, position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height + getTotalNestingBlockHeight() + 24 + 8;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawNestingBlock(position, width, height, getTotalNestingBlockHeight());
    }

    private int getTotalNestingBlockHeight() {
        if(nestedBlock == null) { return 25; }
        return nestedBlock.getConnectedBlockHeights();
    }
}

class DoubleNestingBlock extends NestingBlock {
    Block secondNestedBlock;

    public DoubleNestingBlock(BlockType type, Position position) {
        super(type, position);
    }

    @Override
    public int getHeight() {
        return height + getFirstNestingBlockHeight() + getSecondNestingBlockHeight() + 24 + 24 + 16;
    }

    @Override
    public Path getPath() {
        return BlockPaths.drawDoubleNestingBlock(position, width, height, getFirstNestingBlockHeight(), getSecondNestingBlockHeight());
    }

    public int getFirstNestingBlockHeight() {
        if(nestedBlock == null) { return 25; }
        return nestedBlock.getConnectedBlockHeights();
    }

    private int getSecondNestingBlockHeight() {
        if(secondNestedBlock == null) { return 25; }
        return secondNestedBlock.getConnectedBlockHeights();
    }
}

class Parameter<T> {
    final static int PARAMETER_SIZE = 25;
    Position labelPosition;
    T value;
    Block childBlock = null;

    public Parameter(Position labelPosition, T value) {
        this.labelPosition = labelPosition;
        this.value = value;
    }
    
    public double getWidth() {
        if(value == null) {
            return PARAMETER_SIZE + 10;
        } else if(childBlock != null) {
            return childBlock.getWidth();
        } else if(value.getClass() == Integer.class || value.getClass() == Notes.class) {
            Text widthCheck = new Text(value.toString());
            widthCheck.setFont(Editor.COOL_FONT);
            return Math.max(PARAMETER_SIZE, widthCheck.getLayoutBounds().getWidth() + 10);
        }
        return PARAMETER_SIZE;
    }

    public Path getPath() {
        double x = labelPosition.x;
        double y = labelPosition.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        int radius = PARAMETER_SIZE / 2;
        double width = getWidth();
        if(value == null) {
            path.getElements().add(new MoveTo(x + radius, y));

            path.getElements().add(new LineTo(x + radius + width - PARAMETER_SIZE, y));
            x = x + radius + width - PARAMETER_SIZE;

            path.getElements().add(new LineTo(x + radius, y + radius));
            x += radius;
            y += radius;

            path.getElements().add(new LineTo(x - radius, y + radius));
            x -= radius;
            y += radius;

            path.getElements().add(new LineTo(x - width + PARAMETER_SIZE, y));
            x = x - width + PARAMETER_SIZE;

            path.getElements().add(new LineTo(x - radius, y - radius));
            x -= radius;
            y -= radius;

            path.getElements().add(new LineTo(x + radius, y - radius));
            x += radius;
            y -= radius;

            return path;
        } else if(Block.class.isAssignableFrom(value.getClass())) {
            return path;
        }

        path.getElements().add(new MoveTo(x + radius, y));

        path.getElements().add(new LineTo(x + radius + width - PARAMETER_SIZE, y));
        x = x + radius + width - PARAMETER_SIZE;

        path.getElements().add(new ArcTo(radius, radius, 0, x, y + PARAMETER_SIZE, false, true));
        y += PARAMETER_SIZE;

        path.getElements().add(new LineTo(x - width + PARAMETER_SIZE, y));
        x = x - width + PARAMETER_SIZE;

        path.getElements().add(new ArcTo(radius, radius, 0, x, y - PARAMETER_SIZE, false, true));
        y -= PARAMETER_SIZE;

        return path;
    }
}