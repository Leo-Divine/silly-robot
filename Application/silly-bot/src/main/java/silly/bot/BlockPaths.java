package silly.bot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BlockPaths {
    public static GraphicsContext drawDefaultBlock(GraphicsContext gc, Block block) {
        double x = block.position.x + 325;
        double y = block.position.y;
        gc.setFill(block.blockType.blockCategory.fill);
        gc.setStroke(block.blockType.blockCategory.border);
        gc.beginPath();
        gc.moveTo(x, y + 4);

        gc.bezierCurveTo(x, y, x, y, x + 4, y);
        x = x + 4;

        gc.lineTo(x + 8, y);
        x = x + 8;

        gc.bezierCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2);
        x = x + 4;
        y = y + 2;

        gc.lineTo(x + 4, y + 4);
        x = x + 4;
        y = y + 4;

        gc.bezierCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2);
        x = x + 4;
        y = y + 2;

        gc.lineTo(x + 12, y);
        x = x + 12;

        gc.bezierCurveTo(x + 2, y, x + 3, y - 2, x + 4, y - 2);
        x = x + 4;
        y = y - 2;

        gc.lineTo(x + 4, y - 4);
        x = x + 4;
        y = y - 4;

        gc.bezierCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2);
        x = x + 4;
        y = y - 2;

        gc.lineTo(x + block.width - 52, y);
        x = x + block.width - 52;

        gc.bezierCurveTo(x + 4, y, x + 4, y, x + 4, y + 4);
        x = x + 4;
        y = y + 4;

        gc.lineTo(x, y + block.height);
        y = y + block.height;

        gc.bezierCurveTo(x, y + 4, x, y + 4, x - 4, y + 4);
        x = x - 4;
        y = y + 4;

        gc.lineTo(x - block.width + 52, y);
        x = x - block.width + 52;

        gc.bezierCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2);
        x = x - 4;
        y = y + 2;

        gc.lineTo(x - 4, y + 4);
        x = x - 4;
        y = y + 4;

        gc.bezierCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2);
        x = x - 4;
        y = y + 2;

        gc.lineTo(x - 12, y);
        x = x - 12;

        gc.bezierCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2);
        x = x - 4;
        y = y - 2;

        gc.lineTo(x - 4, y - 4);
        x = x - 4;
        y = y - 4;

        gc.bezierCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2);
        x = x - 4;
        y = y - 2;

        gc.lineTo(x - 8, y);
        x = x - 8;

        gc.bezierCurveTo(x - 4, y, x - 4, y, x - 4, y - 4);
        x = x - 4;
        y = y - 4;

        gc.closePath();
        gc.stroke();
        gc.fill();

        return gc;
    }
}
