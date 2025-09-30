package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Vector;

public class Editor extends Canvas {
    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<Block> blocks = new Vector<Block>();

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new Block(
            0,
            BlockType.SetSpeed
        ));
    }

    public void drawBackground(GraphicsContext gc, double height, double width) {
        /*
         * Everything drawn on the canvas is done with gc. To draw there is fill and stroke. 
         * Stroke is drawing only the border, and fill is drawing the border and inside.
         * gc.setStroke() or gc.setFill() sets the color for which technique you want to use.
         * For starters, gc.strokeLine() makes lines, and gc.fillRect() makes rectangles.
         * All methods can be found here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html
         * Make the background how you think it should look, I can give pointers.
         */
        gc.setFill(javafx.scene.paint.Color.SKYBLUE); // chooses color
        gc.fillRect(0,0, height, width); // makes a rectangle
        }


    public void drawBlocks() {
        for(Block block : blocks) {
            Image blockImage = new Image(getClass().getResource(block.blockType.imagePath).toExternalForm());
            gc.drawImage(blockImage, block.xPos, block.yPos, 100, 50);
        }
    }
}
