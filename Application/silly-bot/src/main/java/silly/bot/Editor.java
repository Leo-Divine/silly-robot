package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Vector;

public class Editor extends Canvas {
    final private int MENU_NAVIGATOR_BUTTON_SIZE = 25;

    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<Block> blocks = new Vector<Block>();
    private int blockMenuScroll = 0;

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new Block(
            0,
            BlockType.SetSpeed
        ));
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

        Image setSpeedImage = new Image(getClass().getResource(BlockType.SetSpeed.imagePath).toExternalForm());
        gc.drawImage(setSpeedImage, 100, 75 + blockMenuScroll, 100, 50);

        Image rotateLeftImage = new Image(getClass().getResource(BlockType.RotateLeft.imagePath).toExternalForm());
        gc.drawImage(rotateLeftImage, 100, 150 + blockMenuScroll, 100, 50);

        Image rotateRightImage = new Image(getClass().getResource(BlockType.RotateRight.imagePath).toExternalForm());
        gc.drawImage(rotateRightImage, 100, 225 + blockMenuScroll, 100, 50);

        gc.fillText("Display", 100, 325 + blockMenuScroll);

        Image setLeftColorImage = new Image(getClass().getResource(BlockType.SetLeftColor.imagePath).toExternalForm());
        gc.drawImage(setLeftColorImage, 100, 350 + blockMenuScroll, 100, 50);
    }

    public void drawBlocks() {
        for(Block block : blocks) {
            Image blockImage = new Image(getClass().getResource(block.blockType.imagePath).toExternalForm());
            gc.drawImage(blockImage, block.xPos, block.yPos, 100, 50);
        }
    }

    public void mousePressed(double eventXPos, double eventYPos) {
        
    }

    public void mouseReleased(double eventXPos, double eventYPos) {
        
    }
}
