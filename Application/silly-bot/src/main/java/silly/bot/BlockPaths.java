package silly.bot;

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
        x += 4;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 1, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 52, y));
        x += width - 52;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + height));
        y += height;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 52, y));
        x = x - width + 52;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x -= 4;
        y -= 4;

        return path;
    }

    public static Path drawValueBlock(Position position, int width, int height) {
        double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        int radius = height / 2;

        path.getElements().add(new MoveTo(x + radius, y));

        path.getElements().add(new LineTo(x + radius + width - height, y));
        x = x + radius + width - height;

        path.getElements().add(new ArcTo(radius, radius, 0, x, y + height, false, true));
        y += height;

        path.getElements().add(new LineTo(x - width + height, y));
        x = x - width + height;

        path.getElements().add(new ArcTo(radius, radius, 0, x, y - height, false, true));
        y -= height;

        return path;
    }

    public static Path drawOperandBlock(Position position, int width, int height) {
        double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        int pointLength = height / 2;

        path.getElements().add(new MoveTo(x + pointLength, y));

        path.getElements().add(new LineTo(x + pointLength + width - height, y));
        x = x + pointLength + width - height;

        path.getElements().add(new LineTo(x + pointLength, y + pointLength));
        x += pointLength;
        y += pointLength;

        path.getElements().add(new LineTo(x - pointLength, y + pointLength));
        x -= pointLength;
        y += pointLength;

        path.getElements().add(new LineTo(x - width + height, y));
        x = x - width + height;

        path.getElements().add(new LineTo(x - pointLength, y - pointLength));
        x -= pointLength;
        y -= pointLength;

        path.getElements().add(new LineTo(x + pointLength, y - pointLength));
        x += pointLength;
        y -= pointLength;

        return path;
    }

    public static Path drawNestingBlock(Position position, int width, int baseHeight, int nestedHeight) {
        double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y + 4));

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y, false, true));
        x += 4;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 1, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 52, y));
        x += width - 52;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + baseHeight));
        y += baseHeight;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 64, y));
        x = x - width + 64;
        
        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y));
        x -= 4;

        path.getElements().add(new CubicCurveTo(x - 4, y, x - 4, y, x - 4, y + 4));
        x -= 4;
        y += 4;
     
        path.getElements().add(new LineTo(x, y + nestedHeight));
        y += nestedHeight;

        path.getElements().add(new CubicCurveTo(x, y + 4, x, y + 4, x + 4, y + 4));
        x += 4;
        y += 4;
        
        path.getElements().add(new LineTo(x + 4, y));
        x += 4;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 64, y));
        x += width - 64;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + 24));
        y += 24;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 52, y));
        x = x - width + 52;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x -= 4;
        y -= 4;

        return path;
    }

    public static Path drawDoubleNestingBlock(Position position, int width, int baseHeight, int firstNestedHeight, int secondNestedHeight) {
       double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y + 4));

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y, false, true));
        x += 4;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 1, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 52, y));
        x += width - 52;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + baseHeight));
        y += baseHeight;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 64, y));
        x = x - width + 64;
        
        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y));
        x -= 4;

        path.getElements().add(new CubicCurveTo(x - 4, y, x - 4, y, x - 4, y + 4));
        x -= 4;
        y += 4;
     
        path.getElements().add(new LineTo(x, y + firstNestedHeight));
        y += firstNestedHeight;

        path.getElements().add(new CubicCurveTo(x, y + 4, x, y + 4, x + 4, y + 4));
        x += 4;
        y += 4;
        
        path.getElements().add(new LineTo(x + 4, y));
        x += 4;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 64, y));
        x += width - 64;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + 24));
        y += 24;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;
        
        path.getElements().add(new LineTo(x - width + 64, y));
        x = x - width + 64;
        
        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y));
        x -= 4;

        path.getElements().add(new CubicCurveTo(x - 4, y, x - 4, y, x - 4, y + 4));
        x -= 4;
        y += 4;
     
        path.getElements().add(new LineTo(x, y + secondNestedHeight));
        y += secondNestedHeight;

        path.getElements().add(new CubicCurveTo(x, y + 4, x, y + 4, x + 4, y + 4));
        x += 4;
        y += 4;
        
        path.getElements().add(new LineTo(x + 4, y));
        x += 4;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y + 1, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 4, y + 4));
        x += 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x + 1, y + 1, x + 2, y + 2, x + 4, y + 2));
        x += 4;
        y += 2;

        path.getElements().add(new LineTo(x + 12, y));
        x += 12;

        path.getElements().add(new CubicCurveTo(x + 2, y, x + 3, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + 4, y - 4));
        x += 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x + 1, y - 1, x + 2, y - 2, x + 4, y - 2));
        x += 4;
        y -= 2;

        path.getElements().add(new LineTo(x + width - 64, y));
        x += width - 64;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + 24));
        y += 24;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 52, y));
        x = x - width + 52;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x -= 4;
        y -= 4;

        return path; 
    }

    public static Path drawStartBlock(Position position, int width, int height) {
        double x = position.x + 325;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y + 4));

        path.getElements().add(new CubicCurveTo(x + 25, y - 22, x + 71, y - 22, x + 96, y));
        x += 96;

        path.getElements().add(new LineTo(x + width - 96, y));
        x += width - 96;

        path.getElements().add(new ArcTo(4, 4, 0, x + 4, y + 4, false, true));
        x += 4;
        y += 4;

        path.getElements().add(new LineTo(x, y + height));
        y += height;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - width + 52, y));
        x = x - width + 52;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y + 1, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 4, y + 4));
        x -= 4;
        y += 4;

        path.getElements().add(new CubicCurveTo(x - 1, y + 1, x - 2, y + 2, x - 4, y + 2));
        x -= 4;
        y += 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new CubicCurveTo(x - 2, y, x - 3, y - 1, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 4, y - 4));
        x -= 4;
        y -= 4;

        path.getElements().add(new CubicCurveTo(x - 1, y - 1, x - 2, y - 2, x - 4, y - 2));
        x -= 4;
        y -= 2;

        path.getElements().add(new LineTo(x - 12, y));
        x -= 12;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x -= 4;
        y -= 4;

        return path;
    }
}
