package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Vector;

public class Editor extends Canvas {
    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<Block> blocks = new Vector<Block>();

    public Editor(int i, int j) {
        super(i, j);
    }

    public void drawBackground() {

    }

    public void drawBlocks() {
        
    }

    public void test() {
        gc.setFill(Color.BLUE);
        gc.fillRect(75,75,100,100);
    }
}
