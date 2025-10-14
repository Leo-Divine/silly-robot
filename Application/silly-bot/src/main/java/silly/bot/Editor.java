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
    private int blockMenuScroll = 0;
    private int currentDraggingBlock = 0;

    public Editor(int i, int j) {
        super(i, j);
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

        gc.setFill(Color.rgb(89, 192, 89));
        gc.fillOval(25, 75, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(Color.GREY);
        gc.fillOval(25, 125, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        gc.setFill(Color.rgb(153, 102, 255));
        gc.fillOval(25, 175, MENU_NAVIGATOR_BUTTON_SIZE, MENU_NAVIGATOR_BUTTON_SIZE);

        // Draw Blocks and Text
        gc.setFill(Color.BLACK);
        gc.fillText("Movement", 100, 50 + blockMenuScroll);

        gc.drawImage(BlockType.SetSpeed.image, 100, 75 - blockMenuScroll, 100, 50);

        gc.drawImage(BlockType.RotateLeft.image, 100, 150 - blockMenuScroll, 100, 50);

        gc.drawImage(BlockType.RotateRight.image, 100, 225 - blockMenuScroll, 100, 50);

        gc.fillText("Display", 100, 325 - blockMenuScroll);

        gc.drawImage(BlockType.SetColor.image, 100, 350 - blockMenuScroll, 100, 50);
    }

    public void drawBlocks() {
        for(Block block : blocks) {
            gc.drawImage(block.blockType.image, block.xPos + 325, block.yPos, 100, 50);
        }
    }

    public void mousePressed(double eventXPos, double eventYPos) {
        for(Block block : blocks) {
            if(!block.isMouseOnBlock(eventXPos, eventYPos)) { continue; }
            block.isDragging = true;
            block.mouseOffsetX = eventXPos - 325 - block.xPos;
            block.mouseOffsetY = eventYPos - block.yPos;
            currentDraggingBlock = block.getId();
            break;
        }

        //Check if a Menu Block was Selected
        if(eventXPos >= 100 && 
        eventXPos <= 200 &&
        eventYPos >= 75 - blockMenuScroll &&
        eventYPos <= 75 - blockMenuScroll + 50) {
            Block block = new Block(
                BlockType.SetSpeed,
                100,
                75 - blockMenuScroll
            );
            block.isDragging = true;
            block.mouseOffsetX = eventXPos - 325 - block.xPos;
            block.mouseOffsetY = eventYPos - block.yPos;
            currentDraggingBlock = block.getId();

            blocks.add(block);

            return;
        }
    }

    public void mouseReleased(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }

        // Get block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];
        block.isDragging = false;
        block.mouseOffsetX = 0;
        block.mouseOffsetY = 0;
        currentDraggingBlock = 0;
    }

    public void mouseMoved(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }

        // Get block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];

        block.xPos = eventXPos - 325 - block.mouseOffsetX;
        block.yPos = eventYPos - block.mouseOffsetY;
    }
}
