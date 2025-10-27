package silly.bot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class BlockPaths {
    public static String pathToString(Path path) {
        StringBuilder svgPathBuilder = new StringBuilder();

        for (PathElement element : path.getElements()) {
            if (element instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) element;
                svgPathBuilder.append("M").append(" ").append(moveTo.getX()).append(",").append(moveTo.getY()).append(" ");
            } else if (element instanceof LineTo) {
                LineTo lineTo = (LineTo) element;
                svgPathBuilder.append("L").append(" ").append(lineTo.getX()).append(",").append(lineTo.getY()).append(" ");
            } else if (element instanceof ArcTo) {
                ArcTo arcTo = (ArcTo) element;
                svgPathBuilder.append("A").append(" ")
                .append(arcTo.getRadiusX()).append(",").append(arcTo.getRadiusY()).append(" ")
                .append(arcTo.getXAxisRotation()).append(" ")
                .append(arcTo.isLargeArcFlag() ? 1 : 0).append(",").append(arcTo.isSweepFlag() ? 1 : 0).append(" ")
                .append(arcTo.getX()).append(",").append(arcTo.getY()).append(" ");
            } else if (element instanceof CubicCurveTo) {
                CubicCurveTo cubicCurveTo = (CubicCurveTo) element;
                svgPathBuilder.append("C").append(" ")
                .append(cubicCurveTo.getControlX1()).append(",").append(cubicCurveTo.getControlY1()).append(" ")
                .append(cubicCurveTo.getControlX2()).append(",").append(cubicCurveTo.getControlY2()).append(" ")
                .append(cubicCurveTo.getX()).append(",").append(cubicCurveTo.getY()).append(" ");
            }
        }
        return svgPathBuilder.toString().trim();
    }

    public static Path drawDefaultBlock(Position position, int width, int height) {
        double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y + 4));

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y, false, true));
        x = x + 4;

        path.getElements().add(new LineTo(x + 12, y));
        x = x + 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x = x + 4;
        y = y + 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x = x + 4;
        y = y + 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x = x + 4;
        y = y + 2;

        path.getElements().add(new LineTo(x + 12, y));
        x = x + 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 1, x + 4, y - 2));
        x = x + 4;
        y = y - 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x = x + 4;
        y = y - 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x = x + 4;
        y = y - 2;

        path.getElements().add(new LineTo(x + width - 52, y));
        x = x + width - 52;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x = x + 4;
        y = y + 4;

        path.getElements().add(new LineTo(x, y + height));
        y = y + height;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x = x - 4;
        y = y + 4;

        path.getElements().add(new LineTo(x - width + 52, y));
        x = x - width + 52;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x = x - 4;
        y = y + 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x = x - 4;
        y = y + 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x = x - 4;
        y = y + 2;

        path.getElements().add(new LineTo(x - 12, y));
        x = x - 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x = x - 4;
        y = y - 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x = x - 4;
        y = y - 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x = x - 4;
        y = y - 2;

        path.getElements().add(new LineTo(x - 12, y));
        x = x - 12;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x = x - 4;
        y = y - 4;

        return path;
    }

    //public static GraphicsContext drawValueBlock(GraphicsContext gc, Block block) {}

    /* 
    public static GraphicsContext drawOperandBlock(GraphicsContext gc, Block block) {
        double x = block.position.x + 325 + 20;
        double y = block.position.y;
        gc.setFill(block.blockType.category.fill);
        gc.setStroke(block.blockType.category.border);
        gc.beginPath();
        gc.moveTo(x, y);

        gc.lineTo(x + block.width - 40, y);
        x = x + block.width - 40;

        gc.lineTo(x + 20, y + 20);
        x = x + 20;
        y = y + 20;

        
    }
        */

    public static GraphicsContext drawNestingBlock(GraphicsContext gc, Block block) {
        double x = block.position.x + 325;
        double y = block.position.y;
        gc.setFill(block.blockType.category.fill);
        gc.setStroke(block.blockType.category.border);
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

        gc.appendSVGPath(null);

        gc.lineTo(x - block.width + 64, y);
        x = x - block.width + 64;

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

        gc.lineTo(x - 4, y);
        x = x - 4;

        gc.bezierCurveTo(x - 4, y, x - 4, y, x - 4, y + 4);
        x = x - 4;
        y = y + 4;

        //Nested Block Heights
        gc.lineTo(x, y + 25);
        y = y + 25;

        gc.bezierCurveTo(x, y + 4, x, y + 4, x + 4, y + 4);
        x = x + 4;
        y = y + 4;

        gc.lineTo(x + 4, y);
        x = x + 4;

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

        gc.lineTo(x + block.width - 64, y);
        x = x + block.width - 64;

        gc.bezierCurveTo(x + 4, y, x + 4, y, x + 4, y + 4);
        x = x + 4;
        y = y + 4;

        gc.lineTo(x, y + 24);
        y = y + 24;

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

    //public static GraphicsContext drawDoubleNestingBlock(GraphicsContext gc, Block block) {}

    public static GraphicsContext drawStartBlock(GraphicsContext gc, Block block) {
        double x = block.position.x + 325;
        double y = block.position.y;
        gc.setFill(block.blockType.category.fill);
        gc.setStroke(block.blockType.category.border);
        gc.beginPath();
        gc.moveTo(x, y);

        gc.bezierCurveTo(x + 25, y - 22, x + 71, y - 22, x + 96, y);
        x = x + 96;

        gc.lineTo(x + block.width - 96, y);
        x = x + block.width - 96;

        gc.bezierCurveTo(x + 4, y, x + 4, y, x + 4, y + 4);
        x = x + 4;
        y = y + 4;

        gc.lineTo(x, y + block.height);
        y = y + block.height;

        gc.bezierCurveTo(x, y + 4, x, y + 4, x - 4, y + 4);
        x = x - 4;
        y = y + 4;

        gc.lineTo(x - block.width + 48, y);
        x = x - block.width + 48;

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
