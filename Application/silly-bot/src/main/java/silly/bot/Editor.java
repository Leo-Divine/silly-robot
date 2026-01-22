package silly.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * <h2>Editor</h2>
 * <h4>The code editor that manages the interface and runs the code.</h4>
 */
public class Editor extends Canvas {
    /**
     * The size of the navagation buttons in the menu.
     */
    final private int MENU_NAVIGATOR_BUTTON_SIZE = 25;
    /**
     * The width of the editor's menu.
     */
    final public static double MENU_WIDTH = 450;
    /**
     * The main font used on the blocks.
     */
    final static public Font COOL_FONT = getCocoFont();

    /**
     * The graphics context of the editor.
     */
    private GraphicsContext gc = getGraphicsContext2D();
    /**
     * A list of all the category labels and blocks present in the menu.
     */
    private Vector<MenuItem> menuItems = createMenu();
    /**
     * A collection of all the blocks that are being used in the editor.
     */
    private Vector<Block> blocks = new Vector<Block>();
    /**
     * The current amount scrolled in the block menu. Determines how far down the menu displays.
     */
    private double blockMenuScroll = 0;
    /**
     * <p>The block id of the block currently being dragged.</p>
     * <p>A value of 0 means no block is being dragged.</p>
     */
    private int currentDraggingBlock = 0;
    /**
     * <p>The block id and parameter index of the selected parameter.</p>
     * <p>The first index is the id of the block that has the parameter.
     * The second index is the index of the parameter.</p>
     * <p>If the first index has a value of 0, it means no parameter is selected.</p>
     */
    private int[] selectedParameter = new int[]{0, 0};
    /**
     * Determines if the color picker sub-menu is displayed.
     */
    private boolean isColorPickerShowing = false;
    /**
     * Determines if the note picker sub-menu is displayed.
     */
    private boolean isNotePickerShowing = false;
    /**
     * The note picker sub-menu.
     */
    private NotePicker notePickerMenu;

    private boolean isProgramRunning = false;

    public Editor(int i, int j) {
        super(i, j);
        blocks.add(new StartBlock(BlockType.Start, new Position(MENU_WIDTH + 100, 40)));
        notePickerMenu = new NotePicker(new Position(0, 0));
    }

    public boolean getIsProgramRunning() {
        return isProgramRunning;
    }

    public void setIsProgramRunning(boolean isProgramRunning) {
        this.isProgramRunning = isProgramRunning;
    }

    /**
     * Gets the Coco Chamel font.
     * @return the Coco font, or the Arial font if an error occurs.
     */
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

    /**
     * Clears the editor.
     */
    public void clearCanvas() {
        gc.clearRect(0, 0, 2000, 2000);
    }

    /**
     * Draws the backgrouns on the editor.
     */
    public void drawBackground() {
        gc.setStroke(javafx.scene.paint.Color.rgb(198, 198, 198));
        gc.setLineWidth(2);

        gc.strokeLine(75, 15, 75, 1985);

        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(MENU_WIDTH, 0, 1675, 2000);
    }

    /**
     * Draws all the menu icons and blocks.
     */
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
                new Position((MENU_WIDTH * -1) + 100, item.yPos - blockMenuScroll),
                item.block.startWidth,
                item.block.startHeight
            );

            gc.setStroke(item.block.category.border);
            gc.setLineWidth(Block.BORDER_WIDTH);
            gc.setFill(item.block.category.fill);

            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(blockPath));
            gc.closePath();
            gc.fill();
            gc.stroke();
            
            Text widthCheck = new Text();
            widthCheck.setFont(Editor.COOL_FONT);

            gc.setFill(Color.WHITE);
            gc.setStroke(item.block.category.border);
            // Display Only Text For no Parameters
            if(item.block.label.indexOf("α") == -1) {
                gc.fillText(item.block.label, 100 + item.block.shape.labelOffset.x, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
                continue;
            }

            String[] stringParts = item.block.label.split("α");
            double xPos = 100 + item.block.shape.labelOffset.x;
            for(int i = 0; i < stringParts.length - 1; i++) {
                gc.fillText(stringParts[i], xPos, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
                widthCheck.setText(stringParts[i]);
                xPos += widthCheck.getLayoutBounds().getWidth();

                item.block.parameters[i].labelPosition = new Position(xPos, item.yPos + (item.block.startHeight / 2) - (Parameter.PARAMETER_SIZE / 2) - blockMenuScroll);
                
                if(item.block.parameters[i].value == null) {
                    gc.setFill(item.block.category.border);
                    gc.setStroke(item.block.category.border);
                }
                gc.beginPath();
                gc.appendSVGPath(BlockPaths.pathToString(item.block.parameters[i].getPath()));
                gc.closePath();
                gc.fill();
                gc.stroke();

                xPos += item.block.parameters[i].getWidth();
            }
            gc.setFill(Color.WHITE);
            gc.fillText(stringParts[stringParts.length - 1], xPos, item.yPos + item.block.shape.labelOffset.y - blockMenuScroll);
        }
    }

    /**
     * Creates a list of all the category labels and blocks present in the block menu, along with their positions.
     * @return a list of the labels and blocks in the block menu.
     */
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

    /**
     * Draws all the blocks on the editor and any sub-menus on the blocks.
     */
    public void drawBlocks() {
        for(Block block : blocks) {
            block.drawBlock(gc, selectedParameter);
            //System.out.println(block.getWidth());
        }

        // Draw Color Picker
        if(isColorPickerShowing) {
            Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
            Parameter parameter = block.parameters[selectedParameter[1]];

            for (int x = (int) parameter.labelPosition.x - 33; x < parameter.labelPosition.x + 217; x++) {
                double hue = Math.floor((x - parameter.labelPosition.x + 33) / 0.694);
                for (int y = (int)parameter.labelPosition.y + 40; y < parameter.labelPosition.y + 190; y++) {
                    double saturation = Math.max(Math.min((y - parameter.labelPosition.y - 40) / 0.75, 100),0) / 100;
                    double brightness = Math.max(Math.min((parameter.labelPosition.y + 190 - y) / 0.75, 100),0) / 100;
                    Color color = Color.hsb(hue, saturation, brightness);
                    gc.setFill(color);
                    gc.fillRect(x, y, 1, 1);
                }
            }
            gc.setStroke(block.blockType.category.border);
            gc.strokeRect(parameter.labelPosition.x - 33, parameter.labelPosition.y + 40, 250, 150);
        }
        
        // Draw Note Picker
        if(isNotePickerShowing) { 
            Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
            Parameter parameter = block.parameters[selectedParameter[1]];

            // Draw Menu
            notePickerMenu.position = new Position(parameter.labelPosition.x - 50, parameter.labelPosition.y + 50);
            gc = notePickerMenu.drawMenu(gc, block.blockType.category);
        }
    }

    public void drawStartButton(double screenWidth) {
        // Draw Button
        if(isProgramRunning) { gc.setFill(BlockCategory.Movement.fill); gc.setStroke(BlockCategory.Movement.border); }
        else { gc.setFill(BlockCategory.Display.fill); gc.setStroke(BlockCategory.Display.border); }
        gc.fillOval(screenWidth - 100, 25, 50, 50);
        gc.strokeOval(screenWidth - 100, 25, 50, 50);

        // Draw Symbol
        gc.setFill(Color.WHITE);
        if(isProgramRunning) {
            gc.fillRect(screenWidth - 87, 38, 25, 25);
        } else {
            gc.beginPath();
            gc.moveTo(screenWidth - 82, 38);
            gc.lineTo(screenWidth - 64, 50.5);
            gc.lineTo(screenWidth - 82, 63);
            gc.closePath();
            gc.fill();
        }
    }

    public void drawPopup(PopupType type) {
        gc.setStroke(Color.rgb(255, 51, 85));
        gc.setLineWidth(2);
        gc.setFill(Color.rgb(255, 102, 128));

        switch(type) {
            case FINDING_CLIENT:
                gc.fillRect(25, 25, 205, 40);
                gc.strokeRect(25, 25, 205, 40);
                gc.setFill(Color.WHITE);
                gc.fillText("Searching for Robot...", 30, 50);
                break;
            case PROGRAM_RUNNING:
                gc.fillRect(25, 25, 155, 40);
                gc.strokeRect(25, 25, 155, 40);
                gc.setFill(Color.WHITE);
                gc.fillText("Running Code...", 30, 50);
                break;
        }
    }

    public StartBlock getStartBlock() {
        try {
            return (StartBlock)((StartBlock)blocks.get(0)).clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Handles all keypresses on the editor. Responds to hotkeys and types values into parameters.
     * @param event The event of the keypress.
     */
    public void keyPressed(KeyEvent event) {
        // TODO: Handle Hotkeys
        if(event.getCode() == KeyCode.ENTER) {
            
        }

        if(selectedParameter[0] == 0) { return; }
        Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
        Parameter parameter = block.parameters[selectedParameter[1]];

        if(parameter.value.getClass() == Integer.class) {
            String value = parameter.value.toString();
            if(event.getCode().isDigitKey()) {
                value += event.getCode().getChar();
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                value = value.substring(0, value.length() - 1);
            }

            if(value.length() == 0) {
                parameter.value = 0;
                return;
            }
            
            parameter.value = Integer.parseInt(value);
            if((int)parameter.value > 255) {
                parameter.value = 255;
            }
        }
    }

    public void mousePressed(double eventXPos, double eventYPos) {
        // Select a Color From the Color Picker if it's Open
        if(isColorPickerShowing) {
            Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
            Parameter parameter = block.parameters[selectedParameter[1]];
            if(parameter.labelPosition.x - 33 <= eventXPos
                && parameter.labelPosition.x + 217 >= eventXPos
                && parameter.labelPosition.y + 40 <= eventYPos
                && parameter.labelPosition.y + 190 >= eventYPos
            ) {
                double hue = Math.floor((eventXPos - parameter.labelPosition.x + 33) / 0.694);
                double sat = Math.min((eventYPos - parameter.labelPosition.y - 40) / 0.75, 100) / 100;
                double bri = Math.min((parameter.labelPosition.y + 190 - eventYPos) / 0.75, 100) / 100;
                parameter.value = Color.hsb(hue, sat, bri);

                return;
            }
        }

        // Select a Note From the Note Picker if it's Open
        if(isNotePickerShowing) {
            Block block = (Block) blocks.stream().filter(s -> s.getId() == selectedParameter[0]).toArray()[0];
            Parameter parameter = block.parameters[selectedParameter[1]];
            if(parameter.labelPosition.x - 50 <= eventXPos
                && parameter.labelPosition.x + (NotePicker.WIDTH - 50) >= eventXPos
                && parameter.labelPosition.y + 50 <= eventYPos
                && parameter.labelPosition.y + (NotePicker.HEIGHT + 50) >= eventYPos
            ) {
                notePickerMenu.handleMouseClick(eventXPos, eventYPos);
                parameter.value = notePickerMenu.getCurrentNote();

                return;
            }
        }

        // Remove Selected Parameter
        selectedParameter[0] = 0;
        isColorPickerShowing = false;
        isNotePickerShowing = false;

        // Check all Blocks for Dragging
        for(int blockId = blocks.size() - 1; blockId > 0; blockId--) {
            Block block = blocks.get(blockId);
            if(block.blockType == BlockType.Start) { continue; }
            if(!block.isMouseOnBlock(new Position(eventXPos, eventYPos))) { continue; }

            if(block.parameters != null) {
                for(int i = 0; i < block.parameters.length; i++) {
                    Parameter parameter = block.parameters[i];
                    if(parameter.value == null) { continue; } // Isn't a Block Parameter
                    if(Block.class.isAssignableFrom(parameter.value.getClass())) { continue; } // Isn't a Block Parameter
                    if(!parameter.getPath().contains(eventXPos, eventYPos)) { continue; } 
                    
                    selectedParameter[0] = block.getId();
                    selectedParameter[1] = i;
                    if(parameter.value.getClass() == Color.class) { isColorPickerShowing = true; }
                    if(parameter.value.getClass() == Notes.class) { 
                        isNotePickerShowing = true;
                        notePickerMenu.setNote((Notes)parameter.value);
                    }
                    return;
                }
            }

            block.isDragging = true;
            block.mouseOffset = new Position(eventXPos - MENU_WIDTH - block.position.x, eventYPos - block.position.y);
            currentDraggingBlock = block.getId();
            return;
        }
        
        // Check all Menu Items for Dragging
        for(MenuItem item : menuItems) {
            if(!item.isBlock()) { continue; }
            if(item.block.shape.getPath(
                new Position((MENU_WIDTH * -1) + 100, item.yPos - blockMenuScroll),
                item.block.startWidth,
                item.block.startHeight
            ).contains(eventXPos, eventYPos)) {
                Block block;
                switch(item.block.shape) {
                    case Default: block = new DefaultBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                    case Value: block = new ValueBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                    case Operand: block = new OperandBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                    case Nesting: block = new NestingBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                    case DoubleNesting: block = new DoubleNestingBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                    default: block = new DefaultBlock(item.block, new Position(100, item.yPos - blockMenuScroll)); break;
                }

                block.isDragging = true;
                block.mouseOffset.x = eventXPos - MENU_WIDTH - block.position.x;
                block.mouseOffset.y = eventYPos - block.position.y;
                currentDraggingBlock = block.getId();

                blocks.add(block);
                return;
            }
        }
    }

    public void mouseReleased(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }
        NestingBlock parentBlock = null;

        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];
        block.isDragging = false;
        block.mouseOffset = new Position(0, 0);
        currentDraggingBlock = 0;

        // Remove Previous Connection
        if(block.aboveBlock != null) {
            block.aboveBlock.belowBlock = null;
            block.aboveBlock = null;
        }

        // Remove Previous Value Connection
        if(block.blockType.shape == BlockShape.Value && block.parentBlock != null) {
            ValueBlock childBlock = (ValueBlock)block;
            
            childBlock.parentBlock.parameters[childBlock.parentParameter].childBlock = null;

            // Update Parameter Positions on Parent
            childBlock.parentBlock.updateParameterPositions();
            checkForConnectedBlocks(childBlock.parentBlock);

            childBlock.parentBlock = null;
            childBlock.parentParameter = -1;
        }

        // Remove Previous Operand Connection
        if(block.blockType.shape == BlockShape.Operand && block.parentBlock != null) {
            OperandBlock childBlock = (OperandBlock)block;
            
            childBlock.parentBlock.parameters[0].value = null;
            childBlock.parentBlock.parameters[0].childBlock = null;
            childBlock.parentBlock = null;
            childBlock.parentParameter = -1;
        }

        // Remove Previous Nesting Connection
        if(block.parentBlock != null) {
            if(block.parentBlock.blockType.shape == BlockShape.Nesting) {
                parentBlock = (NestingBlock) block.parentBlock;
                if(parentBlock.nestedBlock.getId() == block.getId()) {
                    parentBlock.nestedBlock = null;
                }
            }
            if(block.parentBlock.blockType.shape == BlockShape.DoubleNesting) {
                DoubleNestingBlock doubleParentBlock = (DoubleNestingBlock) block.parentBlock;
                parentBlock = doubleParentBlock;
                if(parentBlock.nestedBlock != null) {
                    if(parentBlock.nestedBlock.getId() == block.getId()) {
                        parentBlock.nestedBlock = null;
                    }
                }
                if(doubleParentBlock.secondNestedBlock != null) {
                    if(doubleParentBlock.secondNestedBlock.getId() == block.getId()) {
                        doubleParentBlock.secondNestedBlock = null;
                    }
                }
            }
            removeParent(block);
        }

        // Delete Block if Dragged to Menu
        if(block.position.x < 0) {
            deleteConnectedBlock(block);
        }

        // Check if a Value Block Can Connect to Another Block
        if(block.blockType.shape == BlockShape.Value) {
            for(Block surroundingBlock : blocks) {
                if(surroundingBlock.getId() == block.getId()) { continue; } // Same Block
                for(int i = 0; i < surroundingBlock.parameters.length; i++) {
                    if(surroundingBlock.parameters[i].value == null) { continue; } // Is an Operand Parameter
                    if(surroundingBlock.parameters[i].childBlock != null) { continue; } // Already has a Block
                    if(!surroundingBlock.parameters[i].getPath().getBoundsInParent().intersects(block.getPath().getBoundsInParent())) { continue; } // Block Isn't Close Enough

                    surroundingBlock.parameters[i].childBlock = block;
                    block.parentBlock = surroundingBlock;
                    ((ValueBlock)block).parentParameter = i;

                    surroundingBlock.updateParameterPositions();
                    checkForConnectedBlocks(surroundingBlock);

                    try {
                        moveConnectedChildBlock(block, surroundingBlock.parameters[i].labelPosition);
                    } catch(ArrayIndexOutOfBoundsException e) {
                        surroundingBlock.parameters[i].childBlock = null;
                    }
                    return;
                }
            }
            return;
        }

        // Check if an Operand Block Can Connect to Another Block
        if(block.blockType.shape == BlockShape.Operand) {
            for(Block surroundingBlock : blocks) {
                if(surroundingBlock.parameters.length <= 0) { continue; } // Has no Parameters
                if(surroundingBlock.parameters[0].value != null) { continue; } // Already has a Block
                if(!surroundingBlock.parameters[0].getPath().getBoundsInParent().intersects(block.getPath().getBoundsInParent())) { continue; } // Block Isn't Close Enough
                
                surroundingBlock.parameters[0].value = block;
                surroundingBlock.parameters[0].childBlock = block;
                block.parentBlock = surroundingBlock;
                ((OperandBlock)block).parentParameter = 0;

                surroundingBlock.updateParameterPositions();
                try {
                    moveConnectedChildBlock(block, surroundingBlock.parameters[0].labelPosition);
                } catch(ArrayIndexOutOfBoundsException e) {
                    surroundingBlock.parameters[0].value = null;
                    surroundingBlock.parameters[0].childBlock = null;
                }
                return;
            }
            return;
        }

        // Check if Block Can Connect to Another Block
        for(Block surroundingBlock : blocks) {
            if(surroundingBlock.getId() == block.getId()) { continue; } // Same Block
            if(surroundingBlock.blockType.shape == BlockShape.Operand 
                || surroundingBlock.blockType.shape == BlockShape.Value) { continue; } // Non-Connecting Shape
            
            // Check if the Block can be Nested
            if(surroundingBlock.blockType.shape == BlockShape.Nesting) {
                NestingBlock nestingBlock = (NestingBlock) surroundingBlock;
                if(nestingBlock.nestedBlock == null
                    && block.position.x - nestingBlock.position.x < nestingBlock.getWidth()
                    && block.position.x > nestingBlock.position.x 
                    && nestingBlock.position.y + nestingBlock.height < block.position.y + block.height
                    && nestingBlock.position.y + nestingBlock.height + 25 > block.position.y
                ) {
                    nestingBlock.nestedBlock = block;
                    block.parentBlock = nestingBlock;

                    // Move Connected Blocks
                    Block childBlock = nestingBlock;
                    while(childBlock.parentBlock != null) {
                        childBlock = childBlock.parentBlock;
                    }
                    checkForConnectedBlocks(childBlock);
                    return;
                }
            }

            // Check if Block can be Nested on a DoubleNested Block
            if(surroundingBlock.blockType.shape == BlockShape.DoubleNesting) {
                DoubleNestingBlock nestingBlock = (DoubleNestingBlock) surroundingBlock;

                // First Nesting Space
                if(nestingBlock.nestedBlock == null
                    && block.position.x - nestingBlock.position.x < nestingBlock.getWidth()
                    && block.position.x > nestingBlock.position.x 
                    && nestingBlock.position.y + nestingBlock.height < block.position.y + block.height
                    && nestingBlock.position.y + nestingBlock.height + 25 > block.position.y
                ) {
                    nestingBlock.nestedBlock = block;
                    block.parentBlock = nestingBlock;

                    // Move Connected Blocks
                    Block childBlock = nestingBlock;
                    while(childBlock.parentBlock != null) {
                        childBlock = childBlock.parentBlock;
                    }
                    checkForConnectedBlocks(childBlock);
                    return;
                }

                // Second Nesting Space
                if(nestingBlock.secondNestedBlock == null
                    && block.position.x - nestingBlock.position.x < nestingBlock.getWidth()
                    && block.position.x > nestingBlock.position.x 
                    && nestingBlock.position.y + nestingBlock.height + nestingBlock.getFirstNestingBlockHeight() + 32 < block.position.y + block.height
                    && nestingBlock.position.y + nestingBlock.height + nestingBlock.getFirstNestingBlockHeight() + 32 + 25 > block.position.y
                ) {
                    nestingBlock.secondNestedBlock = block;
                    block.parentBlock = nestingBlock;

                    // Move Connected Blocks
                    Block childBlock = nestingBlock;
                    while(childBlock.parentBlock != null) {
                        childBlock = childBlock.parentBlock;
                    }
                    checkForConnectedBlocks(childBlock);
                    return;
                }
            }

            if(surroundingBlock.belowBlock != null) { continue; } // Already has a Block

            // Check if Block is Close Enough
            if(Math.abs(surroundingBlock.position.x - block.position.x) <= 25 &&
               Math.abs(surroundingBlock.position.y + surroundingBlock.getHeight() - block.position.y) <= 30)  {
                block.aboveBlock = surroundingBlock;
                surroundingBlock.belowBlock = block;

                if(surroundingBlock.parentBlock != null) {
                    block.parentBlock = surroundingBlock.parentBlock;
                }

                block.position = new Position(
                    surroundingBlock.position.x,
                    surroundingBlock.position.y + surroundingBlock.getHeight() + Block.BORDER_WIDTH / 2
                );
                block.updateParameterPositions();

                // Move Connected Blocks
                Block childBlock = block;
                while(childBlock.parentBlock != null) {
                    childBlock = childBlock.parentBlock;
                }
                checkForConnectedBlocks(childBlock);
                break;
            }
        }
        
        // Change Nesting Blocks Width if a Block Was Removed
        if(block.parentBlock == null && parentBlock != null) {
            Block childBlock = parentBlock;
            while(childBlock.parentBlock != null) {
                childBlock = childBlock.parentBlock;
            }
            checkForConnectedBlocks(childBlock);
        }
    }

    private void removeParent(Block block) {
        block.parentBlock = null;
        if(block.belowBlock != null) { removeParent(block.belowBlock); }
    }

    private void deleteConnectedBlock(Block block) {
        if(block.belowBlock != null) {
            deleteConnectedBlock(block.belowBlock);
        }
        for(Parameter parameter : block.parameters) {
            if(parameter.childBlock != null) {
                deleteConnectedBlock(parameter.childBlock);
            }
        }
        if(block.blockType.shape == BlockShape.Nesting) {
            NestingBlock nestingBlock = (NestingBlock) block;
            if(nestingBlock.nestedBlock != null) {
                deleteConnectedBlock(nestingBlock.nestedBlock);
            }
        }
        if(block.blockType.shape == BlockShape.DoubleNesting) {
            DoubleNestingBlock doubleNestingBlock = (DoubleNestingBlock) block;
            if(doubleNestingBlock.nestedBlock != null) {
                deleteConnectedBlock(doubleNestingBlock.nestedBlock);
            }
            if(doubleNestingBlock.secondNestedBlock != null) {
                deleteConnectedBlock(doubleNestingBlock.secondNestedBlock);
            }
        } 
        blocks.remove(block);
    }

    public void mouseMoved(double eventXPos, double eventYPos) {
        if(currentDraggingBlock == 0) { return; }

        // Get Block
        Block block = (Block) blocks.stream().filter(s -> s.getId() == currentDraggingBlock).toArray()[0];

        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        block.position = new Position(eventXPos - MENU_WIDTH - block.mouseOffset.x, eventYPos - block.mouseOffset.y);
        block.updateParameterPositions();

        // Move Connected Blocks
        checkForConnectedBlocks(block);
    }

    private void checkForConnectedBlocks(Block block) {
        if(block.belowBlock != null) {
            moveConnectedBelowBlock(block.belowBlock, block.position, block.getHeight());
        }
        for(Parameter parameter : block.parameters) {
            if(parameter.childBlock != null) {
                moveConnectedChildBlock(parameter.childBlock, parameter.labelPosition);
            }
        }
        if(block.blockType.shape == BlockShape.Nesting || block.blockType.shape == BlockShape.DoubleNesting) {
            NestingBlock nestingBlock = (NestingBlock) block;
            if(nestingBlock.nestedBlock != null) {
                moveConnectedNestedBlock(nestingBlock.nestedBlock, nestingBlock.position, nestingBlock.height);
            }
        }
        if(block.blockType.shape == BlockShape.DoubleNesting) {
            DoubleNestingBlock doubleNestingBlock = (DoubleNestingBlock) block;
            if(doubleNestingBlock.secondNestedBlock != null) {
                moveConnectedNestedBlock(doubleNestingBlock.secondNestedBlock, doubleNestingBlock.position, doubleNestingBlock.height + doubleNestingBlock.getFirstNestingBlockHeight() + 32);
            }
        } 
    }

    private void moveConnectedChildBlock(Block block, Position parameterPosition) {
        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        // Update Position
        block.position = new Position(parameterPosition.x - MENU_WIDTH, parameterPosition.y);
        block.updateParameterPositions();

        // Move Connected Blocks
        for(Parameter parameter : block.parameters) {
            if(parameter.childBlock != null) {
                moveConnectedChildBlock(parameter.childBlock, parameter.labelPosition);
            }
        }
    }

    private void moveConnectedBelowBlock(Block block, Position position, double aboveBlockHeight) {
        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        // Move Block
        block.position = new Position(position.x, position.y + aboveBlockHeight + Block.BORDER_WIDTH / 2);
        block.updateParameterPositions();

        // Move Connected Blocks
        checkForConnectedBlocks(block);
    }

    private void moveConnectedNestedBlock(Block block, Position position, double nestingBlockHeight) {
        // Bring Block to Front
        blocks.remove(block);
        blocks.add(block);

        // Move Block
        block.position = new Position(position.x + 16 + Block.BORDER_WIDTH / 2, position.y + nestingBlockHeight + Block.BORDER_WIDTH / 2);
        block.updateParameterPositions();

        // Move Connected Blocks
        checkForConnectedBlocks(block);
    }

    public void mouseClicked(double eventXPos, double eventYPos) {
        // Menu Buttons
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

    public boolean hasProgramStarted(double eventXPos, double eventYPos, double screenWidth) {
        return Math.pow(eventXPos - (50 / 2 + (screenWidth - 100)), 2) + Math.pow(eventYPos - (50 / 2 + 25), 2) <= Math.pow(50 / 2, 2);
    }

    public void mouseScroll(double eventXPos, double scrollAmount) {
        if(eventXPos > 75 && eventXPos < MENU_WIDTH) {
            blockMenuScroll = Math.min(Math.max(0, blockMenuScroll - scrollAmount), menuItems.lastElement().yPos);
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