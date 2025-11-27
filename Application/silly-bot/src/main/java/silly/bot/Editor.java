package silly.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

enum MouseState {
    Default,
    Dragging
}

public class Editor extends Canvas {
    final private int MENU_NAVIGATOR_BUTTON_SIZE = 25;
    final static public Font COOL_FONT = getCocoFont();

    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<MenuItem> menuItems = createMenu();
    private Vector<Block> blocks = new Vector<Block>();
    private double blockMenuScroll = 0;
    private int currentDraggingBlock = 0;
    private int[] selectedParameter = new int[]{0, 0};
    private boolean isColorPickerShowing = false;

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new StartBlock(BlockType.Start, new Position(425, 40)));
    }

    private static Font getCocoFont() {
        InputStream fontStream = Editor.class.getResourceAsStream("/Coco Chamel.ttf");
        if (fontStream != null) {
            Font font = Font.loadFont(fontStream, 17);
            try {
                fontStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return font;
        }
        return new Font("Arial", 18);
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, 2000, 2000);
    }

    public void drawBackground() {
        gc.setStroke(javafx.scene.paint.Color.rgb(198, 198, 198));
        gc.setLineWidth(2);

        gc.strokeLine(75, 15, 75, 1985);

        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(325, 0, 1675, 2000);
    }

    @SuppressWarnings("unchecked")
    public void drawMenus() {
        // Draw Navigation Buttons
        int buttonYPos = 25;
        for(BlockCategory c : BlockCategory.values()) {
            if(c == BlockCategory.Start) { continue; }
            gc.setFill(c.fill);
            gc.fillOval(25, buttonYPos, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);
            buttonYPos += 50;
        }

        // Draw Text and Blocks
        for(MenuItem item : menuItems) {
            if(!item.isBlock()) {
                gc.setFont(new Font("Arial", 20));
                gc.setFill(Color.BLACK);
                gc.fillText(item.text, 100, item.yPos - blockMenuScroll);
                continue;
            }
            gc.setFont(COOL_FONT);
            Path blockPath = item.block.shape.getPath(
                new Position(-225, item.yPos - blockMenuScroll),
                item.block.startWidth,
                item.block.startHeight
            );

            gc.setStroke(item.block.category.border);
            gc.setLineWidth(Block.BORDER_WIDTH);
            gc.setFill(item.block.category.fill);

            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(blockPath));
            gc.closePath();
            gc.fill();
            gc.stroke();
            
            Text widthCheck = new Text();
            widthCheck.setFont(Editor.COOL_FONT);

            gc.setFill(Color.WHITE);
            gc.setStroke(item.block.category.border);
            // Display Only Text For no Parameters
            if(item.block.label.indexOf("α") == -1) {
                gc.fillText(item.block.label, 100 + item.block.shape.labelOffset.x, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
                continue;
            }

            String[] stringParts = item.block.label.split("α");
            double xPos = 100 + item.block.shape.labelOffset.x;
            for(int i = 0; i < stringParts.length - 1; i++) {
                gc.fillText(stringParts[i], xPos, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
                widthCheck.setText(stringParts[i]);
                xPos += widthCheck.getLayoutBounds().getWidth();

                item.block.parameters[i].labelPosition = new Position(xPos, item.yPos + item.block.shape.labelOffset.y);
                gc.fillOval(xPos, item.yPos + (item.block.startHeight / 2) - (Block.PARAMETER_SIZE / 2) - blockMenuScroll, Block.PARAMETER_SIZE, Block.PARAMETER_SIZE);
                gc.strokeOval(xPos, item.yPos + (item.block.startHeight / 2) - (Block.PARAMETER_SIZE / 2) - blockMenuScroll, Block.PARAMETER_SIZE, Block.PARAMETER_SIZE);
                xPos += Block.PARAMETER_SIZE;
            }
            gc.fillText(stringParts[stringParts.length - 1], xPos, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
        }
    }

    private static Vector<MenuItem> createMenu() {
        Vector<MenuItem> menuItems = new Vector<MenuItem>();
        int yPos = 50;
        for(BlockCategory category : BlockCategory.values()) {
            if(category == BlockCategory.Start) { continue; }

            // Add The Category Header Label
            menuItems.add(new MenuItem(category.name(), yPos));
            yPos += 25;

            // Add All Blocks From The Category
            for(BlockType type : BlockType.values()) {
                if(type == BlockType.Start) { continue; }
                if(type.category != category) { continue; }

                menuItems.add(new MenuItem(type, yPos));
                if(type.shape == BlockShape.Nesting) {
                    yPos += type.startHeight + 102;
                } else if(type.shape == BlockShape.DoubleNesting) {
                    yPos += type.startHeight + 167;
                } else {
                    yPos += type.startHeight + 32;
                }
            }
            yPos += 25;
        }
        return menuItems;
    }

    public void drawBlocks() {
        for(Block block : blocks) {
            block.drawBlock(gc);
        }

        // Color Picker
        if(!isColorPickerShowing) { return; }
        Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
        Parameter parameter = block.parameters[selectedParameter[1]];

        for (int x = (int) parameter.labelPosition.x - 33; x < parameter.labelPosition.x + 217; x++) {
            double hue = Math.floor((x - parameter.labelPosition.x + 33) / 0.694);
            for (int y = (int)parameter.labelPosition.y + 40; y < parameter.labelPosition.y + 190; y++) {
                double saturation = Math.min((y - parameter.labelPosition.y - 40) / 0.75, 100) / 100;
                double brightness = Math.min((parameter.labelPosition.y + 190 - y) / 0.75, 100) / 100;
                Color color = Color.hsb(hue, saturation, brightness);
                gc.setFill(color);
                gc.fillRect(x, y, 1, 1);
            }
        }
        gc.setStroke(block.blockType.category.border);
        gc.strokeRect(parameter.labelPosition.x - 33, parameter.labelPosition.y + 40, 250, 150);
    }

    /**
     * Handles all keypresses on the editor. Responds to hotkeys and types values into parameters.
     * @param event The event of the keypress.
     */
    @SuppressWarnings("rawtypes")
    public void keyPressed(KeyEvent event) {
        // TODO: Handle Hotkeys

        if(selectedParameter[0] == 0) { return; }
        Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
        Parameter parameter = block.parameters[selectedParameter[1]];

        if(parameter.value.getClass() == Integer.class) {
            String value = parameter.value.toString();
            if(event.getCode().isDigitKey()) {
                value += event.getCode().getChar();
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                value = value.substring(0, value.length() - 1);
            }

            if(value.length() == 0) {
                parameter.value = 0;
                return;
            }
            
            parameter.value = Integer.parseInt(value);
            if((int)parameter.value > 255) {
                parameter.value = 255;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void mousePressed(double eventXPos, double eventYPos) {
        if(isColorPickerShowing) {
            Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
            Parameter parameter = block.parameters[selectedParameter[1]];
            if(parameter.labelPosition.x - 33 <= eventXPos
                && parameter.labelPosition.x + 217 >= eventXPos
                && parameter.labelPosition.y + 40 <= eventYPos
                && parameter.labelPosition.y + 190 >= eventYPos
            ) {
                double hue = Math.floor((eventXPos - parameter.labelPosition.x + 33) / 0.694);
                double sat = Math.min((eventYPos - parameter.labelPosition.y - 40) / 0.75, 100) / 100;
                double bri = Math.min((parameter.labelPosition.y + 190 - eventYPos) / 0.75, 100) / 100;
                parameter.value = Color.hsb(hue, sat, bri);

                selectedParameter[0] = 0;
                isColorPickerShowing = false;
                return;
            }
        }
        selectedParameter[0] = 0;
        isColorPickerShowing = false;

        for(Block block : blocks) {
            if(block.blockType == BlockType.Start) { continue; }
            if(!block.isMouseOnBlock(new Position(eventXPos, eventYPos))) { continue; }

            if(block.parameters != null) {
                for(int i = 0; i < block.parameters.length; i++) {
                    Parameter parameter = block.parameters[i];
                    if(parameter.value.getClass() == String.class) { continue; }
                    if(parameter.getPath().contains(eventXPos, eventYPos)) {
                        selectedParameter[0] = block.getId();
                        selectedParameter[1] = i;
                        if(parameter.value.getClass() == Color.class) { isColorPickerShowing = true; }
                        return;
                    }
                }
            }
            
            block.isDragging = true;
            block.mouseOffset = new Position(eventXPos - 325 - block.position.x, eventYPos - block.position.y);
            currentDraggingBlock = block.getId();
            return;
        }
         
        for(MenuItem item : menuItems) {
            if(!item.isBlock()) { continue; }
            if(item.block.shape.getPath(
                new Position(-225, item.yPos - blockMenuScroll),
                item.block.startWidth,
                item.block.startHeight
            ).contains(eventXPos, eventYPos)) {
                Block block;
                switch(item.block.shape) {
                    case Default:
                        block = new DefaultBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                    case Value:
                        block = new ValueBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                    case Operand:
                        block = new OperandBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                    case Nesting:
                        block = new NestingBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                    case DoubleNesting:
                        block = new DoubleNestingBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                    default: 
                        block = new DefaultBlock(item.block, new Position(100, item.yPos - blockMenuScroll));
                        break;
                }

                block.isDragging = true;
                block.mouseOffset.x = eventXPos - 325 - block.position.x;
                block.mouseOffset.y = eventYPos - block.position.y;
                currentDraggingBlock = block.getId();

                blocks.add(block);
                return;
            }
        }
    }

    public void mouseReleased(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }

        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];
        block.isDragging = false;
        block.mouseOffset = new Position(0, 0);
        currentDraggingBlock = 0;

        //Remove Previous Connection
        if(block.aboveBlock != 0) {
            Block aboveBlock = (Block) blocks.stream().filter(s -> s.getId() == block.aboveBlock).toArray()[0];
            aboveBlock.belowBlock = 0;
            block.aboveBlock = 0;
        }

        // Check if Dragged to Block Menu
        if(block.position.x < 0) {
            if(block.belowBlock != 0) {
                deleteConnectedBlock(block.belowBlock);
            }
            blocks.remove(block);
        }

        // Check if Block Can Connect to Another Block
        for(Block surroundingBlock : blocks) {
            if(surroundingBlock.getId() == block.getId()) { continue; }
            if(surroundingBlock.belowBlock != 0) { continue; }

            // Check if Block is Close Enough
            if(Math.abs(surroundingBlock.position.x - block.position.x) <= 25 &&
               Math.abs(surroundingBlock.position.y + surroundingBlock.getHeight() - block.position.y) <= 30)  {
                block.aboveBlock = surroundingBlock.getId();
                surroundingBlock.belowBlock = block.getId();

                block.position = new Position(
                    surroundingBlock.position.x,
                    surroundingBlock.position.y + surroundingBlock.getHeight() + Block.BORDER_WIDTH / 2
                );

                if(block.belowBlock == 0) { break; }
                moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
                break;
            }
        }
    }

    private void deleteConnectedBlock(int blockId) {
        Block block = (Block) blocks.stream().filter(s -> s.getId() == blockId).toArray()[0];

        if(block.belowBlock != 0) {
            deleteConnectedBlock(block.belowBlock);
        }
        blocks.remove(block);
    }

    public void mouseMoved(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }

        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];

        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        block.position = new Position(eventXPos - 325 - block.mouseOffset.x, eventYPos - block.mouseOffset.y);

        if(block.belowBlock == 0) { return; }
        moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
    }

    private void moveConnectedBlock(int blockId, Position position, int aboveBlockHeight) {
        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == blockId).toArray()[0];

        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        // Move Block
        block.position = new Position(position.x, position.y + aboveBlockHeight + Block.BORDER_WIDTH / 2);

        if(block.belowBlock == 0) { return; }
        moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
    }

    @SuppressWarnings("rawtypes")
    public void mouseClicked(double eventXPos, double eventYPos) {
        // Menu Buttons
        int yPos = 25;
        for(BlockCategory c : BlockCategory.values()) {
            if(c == BlockCategory.Start) { continue; }

            if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + yPos), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
                blockMenuScroll = ((MenuItem) menuItems.stream().filter(s -> s.text == c.name()).toArray()[0]).yPos - 50;
                break;
            }
            yPos += 50;
        }

        // Parameters
        for(Block block : blocks) {
            if(block.parameters == null) { continue; }
            for(Parameter parameter : block.parameters) {
                if(parameter.value.getClass() == Block.class) { continue; }
                if(Math.pow(eventXPos - (Block.PARAMETER_SIZE / 2 + parameter.labelPosition.x), 2) + Math.pow(eventYPos - (Block.PARAMETER_SIZE / 2 + parameter.labelPosition.y), 2) <= Math.pow(Block.PARAMETER_SIZE / 2, 2)) {
                    
                }
            }
        }
    }

    public void mouseScroll(double eventXPos, double scrollAmount) {
        if(eventXPos > 75 && eventXPos < 325) {
            blockMenuScroll = Math.max(0, blockMenuScroll - scrollAmount);
        }
    }
}

class MenuItem {
    BlockType block = null;
    String text = "";
    double yPos;
    
    public MenuItem(BlockType block, double yPos) {
        this.block = block;
        this.yPos = yPos;
    }

    public MenuItem(String text, double yPos) {
        this.text = text;
        this.yPos = yPos;
    }

    public boolean isBlock() {
        return block != null;
    }
}