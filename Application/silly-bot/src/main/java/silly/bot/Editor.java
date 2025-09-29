package silly.bot;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
import java.util.Vector;

public class Editor extends Canvas {
    private GraphicsContext gc = getGraphicsContext2D();
    private Vector<Block> blocks = new Vector<Block>();

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new Block(
            BlockType.SetSpeed
        ));
    }

    public void drawBackground() {

    }

    public void drawBlocks() {
        File file = new File("C:/Users/isido/Documents/GitHub/silly-robot/resources/temp.png");
        Image test = new Image(file.toURI().toString());
        System.out.println(imageUrl.toExternalForm());
        gc.drawImage(test, 75, 75, 300, 400);
    }

    public void test() {
        gc.setFill(Color.BLUE);
        gc.fillRect(75,75,100,100);
    }
}
