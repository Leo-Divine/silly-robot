package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Vector;

enum MouseState {
    Default,
    Dragging
}

public class Editor extends Canvas {
    final private int MENU_NAVIGATOR_BUTTON_SIZE = 25;

    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<Block> blocks = new Vector<Block>();
    private double blockMenuScroll = 0;
    private int currentDraggingBlock = 0;

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new Start(425, 25));
        blocks.add(new MoveForward(700, 25));
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, 800, 800);
    }

    public void drawBackground() {
        gc.setStroke(javafx.scene.paint.Color.rgb(198, 198, 198));
        gc.setLineWidth(2);

        gc.strokeLine(75, 15, 75, 1985);

        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(325, 0, 1675, 2000);
    }

    public void drawMenus() {
        gc.setFont(new Font("Arial", 20));

        // Draw Navigation Buttons
        gc.setFill(Color.rgb(255, 102, 128));
        gc.fillOval(25, 25, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(BlockCategory.Display.fill);
        gc.fillOval(25, 75, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(Color.GREY);
        gc.fillOval(25, 125, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(Color.rgb(153, 102, 255));
        gc.fillOval(25, 175, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(Color.rgb(255, 171, 25));
        gc.fillOval(25, 225, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        // Draw Text
        gc.setFill(Color.BLACK);
        gc.fillText("Movement", 100, 50 - blockMenuScroll);
        gc.fillText("Display", 100, 325 - blockMenuScroll);
        gc.fillText("Sensors", 100, 450 - blockMenuScroll);
        gc.fillText("Control", 100, 575 - blockMenuScroll);
        gc.fillText("Operands", 100, 925 - blockMenuScroll);

        //Draw Blocks
        for(BlockType blockType : BlockType.values()) {
            if(blockType == BlockType.Start) { continue; }
            
        }
    }

    public void drawBlocks() {
        for(Block block : blocks) {
            block.drawBlock(gc);
        }
    }

    public void mousePressed(double eventXPos, double eventYPos) {
        for(Block block : blocks) {
            if(block.blockType == BlockType.Start) { continue; }
            if(!block.isMouseOnBlock(eventXPos, eventYPos)) { continue; }
            block.isDragging = true;
            block.mouseOffset = new Position(eventXPos - 325 - block.position.x, eventYPos - block.position.y);
            currentDraggingBlock = block.getId();
            break;
        }

        // TODO Check if a Menu Block was Selected
        /* 
        for(BlockType blockType : BlockType.values()) {
            if(blockType == BlockType.Start) { continue; }
            if(eventXPos >= 100 && 
            eventXPos <= 100 + blockType.startWidth &&
            eventYPos >= blockType.menuPositionY - blockMenuScroll &&
            eventYPos <= blockType.menuPositionY - blockMenuScroll + blockType.startHeight) {
                Block block = new Block(
                    blockType,
                    100,
                    blockType.menuPositionY - blockMenuScroll
                );
                block.isDragging = true;
                block.mouseOffsetX = eventXPos - 325 - block.xPos;
                block.mouseOffsetY = eventYPos - block.yPos;
                currentDraggingBlock = block.getId();

                blocks.add(block);

                return;
            }
        }
            */
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
               Math.abs(surroundingBlock.position.y + surroundingBlock.blockType.startHeight - block.position.y) <= 40)  {
                block.aboveBlock = surroundingBlock.getId();
                surroundingBlock.belowBlock = block.getId();

                block.position = new Position(
                    surroundingBlock.position.x,
                    surroundingBlock.position.y + surroundingBlock.blockType.startHeight + 8
                );

                if(block.belowBlock == 0) { break; }
                moveConnectedBlock(block.belowBlock, block.position, block.blockType.startHeight);
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
        moveConnectedBlock(block.belowBlock, block.position, block.height);
    }

    private void moveConnectedBlock(int blockId, Position position, int aboveBlockHeight) {
        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == blockId).toArray()[0];

        // Move Block
        block.position = new Position(position.x, position.y + aboveBlockHeight + 8);

        if(block.belowBlock == 0) { return; }
        moveConnectedBlock(block.belowBlock, block.position, block.height);
    }

    public void mouseClicked(double eventXPos, double eventYPos) {
        if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
            blockMenuScroll = 0;
        }
        if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 75), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
            blockMenuScroll = 275;
        }
        if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 125), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
            blockMenuScroll = 400;
        }
        if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 175), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
            blockMenuScroll = 525;
        }
        if(Math.pow(eventXPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 25), 2) + Math.pow(eventYPos - (MENU_NAVIGATOR_BUTTON_SIZE / 2 + 225), 2) <= Math.pow(MENU_NAVIGATOR_BUTTON_SIZE / 2, 2)) {
            blockMenuScroll = 525;
        }
    }

    public void mouseScroll(double eventXPos, double scrollAmount) {
        if(eventXPos > 75 && eventXPos < 325) {
            blockMenuScroll = Math.max(0, Math.min(525, blockMenuScroll - scrollAmount));
        }
    }
}
