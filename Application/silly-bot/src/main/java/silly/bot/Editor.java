package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

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
            gc.setLineWidth(Block.borderWidth);
            gc.setFill(item.block.category.fill);

            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(blockPath));
            gc.closePath();
            gc.fill();
            gc.stroke();
            
            gc.setFill(Color.WHITE);
            gc.fillText(item.block.label, 100 + item.block.shape.labelOffset.x, item.yPos - blockMenuScroll + item.block.shape.labelOffset.y);
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
    }

    public void mousePressed(double eventXPos, double eventYPos) {
        for(Block block : blocks) {
            if(block.blockType == BlockType.Start) { continue; }
            if(!block.isMouseOnBlock(new Position(eventXPos, eventYPos))) { continue; }
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
            if(Math.abs(surroundingBlock.position.x - block.position.x) <= 15 &&
               Math.abs(surroundingBlock.position.y + surroundingBlock.getHeight() - block.position.y) <= 40)  {
                block.aboveBlock = surroundingBlock.getId();
                surroundingBlock.belowBlock = block.getId();

                block.position = new Position(
                    surroundingBlock.position.x,
                    surroundingBlock.position.y + surroundingBlock.getHeight() + 8 + Block.borderWidth / 2
                );

                if(block.belowBlock == 0) { break; }
                moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
                break;
            }
        }
        System.out.println(block.aboveBlock);
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

        block.position = new Position(eventXPos - 325 - block.mouseOffset.x, eventYPos - block.mouseOffset.y);

        if(block.belowBlock == 0) { return; }
        moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
    }

    private void moveConnectedBlock(int blockId, Position position, int aboveBlockHeight) {
        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == blockId).toArray()[0];

        // Move Block
        block.position = new Position(position.x, position.y + aboveBlockHeight + 8 + Block.borderWidth / 2);

        if(block.belowBlock == 0) { return; }
        moveConnectedBlock(block.belowBlock, block.position, block.getHeight());
    }

    public void mouseClicked(double eventXPos, double eventYPos) {
        int yPos = 25;
        for(BlockCategory c : BlockCategory.values()) {
            if(c == BlockCategory.Start) { continue; }

            if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + yPos), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
                blockMenuScroll = ((MenuItem) menuItems.stream().filter(s -> s.text == c.name()).toArray()[0]).yPos - 50;
                break;
            }
            yPos += 50;
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