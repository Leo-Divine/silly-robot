package silly.bot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

enum NoteColor {
    WHITE,
    BLACK;
}

public class NotePicker {
    public static double WIDTH = 300;
    public static double HEIGHT = 100;

    private int selectedOctave = 4;
    private int selectedKey = 0;
    private NoteColor selectedColor = NoteColor.WHITE;
    public Position position;

    public NotePicker(Position position) {
        this.position = position;
    }

    public void drawMenu(GraphicsContext gc, BlockCategory category) {
        gc.setFill(category.fill);
        gc.setStroke(category.border);
        gc.fillRect(position.x, position.y, NotePicker.WIDTH, NotePicker.HEIGHT);
        gc.strokeRect(position.x, position.y, NotePicker.WIDTH, NotePicker.HEIGHT);

        // Draw White Piano Keys
        gc.setFill(Color.WHITE);
        double xPos = 5;
        for(int i = 0; i < 7; i++) {
            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(
                getNotePath(
                    new Position(position.x + xPos, position.y + 30),
                    NoteColor.WHITE
                )
            ));
            gc.closePath();
            if(selectedKey == i && selectedColor == NoteColor.WHITE) { gc.setFill(Color.rgb(57, 155, 247)); }
            gc.fill();
            
            gc.setFill(Color.WHITE);
            xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH;
        }

        // TODO Note Text
        
        // Draw Black Piano Keys
        gc.setFill(Color.BLACK);
        xPos = 5 + getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH - (getNoteWidth(NoteColor.BLACK) / 2);
        for(int i = 0; i < 6; i++) {
            if(i == 2) { xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH; continue; }
            gc.beginPath();
            gc.appendSVGPath(BlockPaths.pathToString(
                getNotePath(
                    new Position(position.x + xPos, position.y + 30),
                    NoteColor.BLACK
                )
            ));
            gc.closePath();
            if(selectedKey == i && selectedColor == NoteColor.BLACK) { gc.setFill(Color.rgb(57, 155, 247)); }
            gc.fill();
            
            gc.setFill(Color.BLACK);
            xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH;
        }

        // Draw Octave Arrows
        gc.setFill(Color.BLACK);
        gc.beginPath();
        gc.appendSVGPath(BlockPaths.pathToString(getRightArrowPath(position)));
        gc.closePath();
        gc.fill();
        
    }

    private double getNoteWidth(NoteColor color) {
        double noteWidth = (WIDTH - 10 - Block.BORDER_WIDTH * 6) / 7;
        if(color == NoteColor.BLACK) {
            noteWidth *= 0.75;
        }
        return noteWidth;
    }

    private double getNoteHeight(NoteColor color) {
        double noteHeight = HEIGHT - 30 - 10;
        if(color == NoteColor.BLACK) {
            noteHeight /= 1.75;

        }
        return noteHeight;
    }

    private Path getNotePath(Position position, NoteColor color) {
        double noteWidth = getNoteWidth(color);
        double noteHeight = getNoteHeight(color);
        double x = position.x;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y));

        path.getElements().add(new LineTo(x + noteWidth, y));
        x += noteWidth;

        path.getElements().add(new LineTo(x, y + noteHeight - 4));
        y = y + noteHeight - 4;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y + 4, false, true));
        x -= 4;
        y += 4;

        path.getElements().add(new LineTo(x - noteWidth + 8, y));
        x = x - noteWidth + 8;

        path.getElements().add(new ArcTo(4, 4, 0, x - 4, y - 4, false, true));
        x -= 4;
        y -= 4;

        return path;
    }

    private Path getLeftArrowPath(Position position) {
        double x = position.x;
        double y = position.y;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        return path;
    }

    private Path getRightArrowPath(Position position) {
        double x = position.x + 16.2;
        double y = position.y + 19;
        Path path = new Path();
        path.setFill(Color.TRANSPARENT);

        path.getElements().add(new MoveTo(x, y));

        path.getElements().add(new LineTo(x - 5.6, y - 0.8));
        x -= 5.6;
        y -= 0.8;

        path.getElements().add(new ArcTo(2.5, 2.5, 0, x - 2.1, y - 2.4, false, true));

        return path;
    }

    public Notes isMouseOnNote(double mouseX, double mouseY) {
        // Check All Black Notes
        double xPos = 5 + getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH - (getNoteWidth(NoteColor.BLACK) / 2);
        for(int i = 0; i < 6; i++) {
            if(i == 2) { xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH; continue; }
            if(getNotePath(new Position(position.x + xPos, position.y + 30), NoteColor.BLACK).contains(mouseX, mouseY)) {
                selectedKey = i;
                selectedColor = NoteColor.BLACK;
                return getNoteFromKeys(NoteColor.BLACK, i);
            }
            xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH;
        }

        // Check All White Notes
        xPos = 5;
        for(int i = 0; i < 7; i++) {
            if(getNotePath(new Position(position.x + xPos, position.y + 30), NoteColor.WHITE).contains(mouseX, mouseY)) {
                selectedKey = i;
                selectedColor = NoteColor.WHITE;
                return getNoteFromKeys(NoteColor.WHITE, i);
            }
            xPos += getNoteWidth(NoteColor.WHITE) + Block.BORDER_WIDTH;
        }

        return null;
    }

    private Notes getNoteFromKeys(NoteColor color, int key) {
        if(color == NoteColor.BLACK) {
            switch(key) {
                case 0: switch(selectedOctave) {
                    case 1: return Notes.NOTE_CS1;
                    case 2: return Notes.NOTE_CS2;
                    case 3: return Notes.NOTE_CS3;
                    case 4: return Notes.NOTE_CS4;
                    case 5: return Notes.NOTE_CS5;
                    case 6: return Notes.NOTE_CS6;
                    case 7: return Notes.NOTE_CS7;
                    default: return Notes.NOTE_CS4;
                }
                case 1: switch(selectedOctave) {
                    case 1: return Notes.NOTE_DS1;
                    case 2: return Notes.NOTE_DS2;
                    case 3: return Notes.NOTE_DS3;
                    case 4: return Notes.NOTE_DS4;
                    case 5: return Notes.NOTE_DS5;
                    case 6: return Notes.NOTE_DS6;
                    case 7: return Notes.NOTE_DS7;
                    default: return Notes.NOTE_DS4;
                }
                case 3: switch(selectedOctave) {
                    case 1: return Notes.NOTE_FS1;
                    case 2: return Notes.NOTE_FS2;
                    case 3: return Notes.NOTE_FS3;
                    case 4: return Notes.NOTE_FS4;
                    case 5: return Notes.NOTE_FS5;
                    case 6: return Notes.NOTE_FS6;
                    case 7: return Notes.NOTE_FS7;
                    default: return Notes.NOTE_FS4;
                }
                case 4: switch(selectedOctave) {
                    case 1: return Notes.NOTE_GS1;
                    case 2: return Notes.NOTE_GS2;
                    case 3: return Notes.NOTE_GS3;
                    case 4: return Notes.NOTE_GS4;
                    case 5: return Notes.NOTE_GS5;
                    case 6: return Notes.NOTE_GS6;
                    case 7: return Notes.NOTE_GS7;
                    default: return Notes.NOTE_GS4;
                }
                case 5: switch(selectedOctave) {
                    case 1: return Notes.NOTE_AS1;
                    case 2: return Notes.NOTE_AS2;
                    case 3: return Notes.NOTE_AS3;
                    case 4: return Notes.NOTE_AS4;
                    case 5: return Notes.NOTE_AS5;
                    case 6: return Notes.NOTE_AS6;
                    case 7: return Notes.NOTE_AS7;
                    default: return Notes.NOTE_AS4;
                }
                default: return Notes.NOTE_CS4;
            }
        }

        switch(key) {
            case 0: switch(selectedOctave) {
                case 1: return Notes.NOTE_C1;
                case 2: return Notes.NOTE_C2;
                case 3: return Notes.NOTE_C3;
                case 4: return Notes.NOTE_C4;
                case 5: return Notes.NOTE_C5;
                case 6: return Notes.NOTE_C6;
                case 7: return Notes.NOTE_C7;
                default: return Notes.NOTE_C4;
            }
            case 1: switch(selectedOctave) {
                case 1: return Notes.NOTE_D1;
                case 2: return Notes.NOTE_D2;
                case 3: return Notes.NOTE_D3;
                case 4: return Notes.NOTE_D4;
                case 5: return Notes.NOTE_D5;
                case 6: return Notes.NOTE_D6;
                case 7: return Notes.NOTE_D7;
                default: return Notes.NOTE_D4;
            }
            case 2: switch(selectedOctave) {
                case 1: return Notes.NOTE_E1;
                case 2: return Notes.NOTE_E2;
                case 3: return Notes.NOTE_E3;
                case 4: return Notes.NOTE_E4;
                case 5: return Notes.NOTE_E5;
                case 6: return Notes.NOTE_E6;
                case 7: return Notes.NOTE_E7;
                default: return Notes.NOTE_E4;
            }
            case 3: switch(selectedOctave) {
                case 1: return Notes.NOTE_F1;
                case 2: return Notes.NOTE_F2;
                case 3: return Notes.NOTE_F3;
                case 4: return Notes.NOTE_F4;
                case 5: return Notes.NOTE_F5;
                case 6: return Notes.NOTE_F6;
                case 7: return Notes.NOTE_F7;
                default: return Notes.NOTE_F4;
            }
            case 4: switch(selectedOctave) {
                case 1: return Notes.NOTE_G1;
                case 2: return Notes.NOTE_G2;
                case 3: return Notes.NOTE_G3;
                case 4: return Notes.NOTE_G4;
                case 5: return Notes.NOTE_G5;
                case 6: return Notes.NOTE_G6;
                case 7: return Notes.NOTE_G7;
                default: return Notes.NOTE_G4;
            }
            case 5: switch(selectedOctave) {
                case 1: return Notes.NOTE_A1;
                case 2: return Notes.NOTE_A2;
                case 3: return Notes.NOTE_A3;
                case 4: return Notes.NOTE_A4;
                case 5: return Notes.NOTE_A5;
                case 6: return Notes.NOTE_A6;
                case 7: return Notes.NOTE_A7;
                default: return Notes.NOTE_A4;
            }
            case 6: switch(selectedOctave) {
                case 1: return Notes.NOTE_B1;
                case 2: return Notes.NOTE_B2;
                case 3: return Notes.NOTE_B3;
                case 4: return Notes.NOTE_B4;
                case 5: return Notes.NOTE_B5;
                case 6: return Notes.NOTE_B6;
                case 7: return Notes.NOTE_B7;
                default: return Notes.NOTE_B4;
            }
            default: return Notes.NOTE_C4;
        }
        
    }
}

class Note {
  public Notes pitch;
  public int duration;

  public Note(Notes pitch, int duration) {
    this.pitch = pitch;
    this.duration = duration;
  }
}

enum Notes {
  NOTE_C1(33),
  NOTE_CS1(35),
  NOTE_D1(37),
  NOTE_DS1(39),
  NOTE_E1(41),
  NOTE_F1(44),
  NOTE_FS1(46),
  NOTE_G1(49),
  NOTE_GS1(52),
  NOTE_A1(55),
  NOTE_AS1(58),
  NOTE_B1(62),
  NOTE_C2(65),
  NOTE_CS2(69),
  NOTE_D2(73),
  NOTE_DS2(78),
  NOTE_E2(82),
  NOTE_F2(87),
  NOTE_FS2(93),
  NOTE_G2(98),
  NOTE_GS2(104),
  NOTE_A2(110),
  NOTE_AS2(117),
  NOTE_B2(123),
  NOTE_C3(131),
  NOTE_CS3(139),
  NOTE_D3(147),
  NOTE_DS3(156),
  NOTE_E3(165),
  NOTE_F3(175),
  NOTE_FS3(185),
  NOTE_G3(196),
  NOTE_GS3(208),
  NOTE_A3(220),
  NOTE_AS3(233),
  NOTE_B3(247),
  NOTE_C4(262),
  NOTE_CS4(277),
  NOTE_D4(294),
  NOTE_DS4(311),
  NOTE_E4(330),
  NOTE_F4(349),
  NOTE_FS4(370),
  NOTE_G4(392),
  NOTE_GS4(415),
  NOTE_A4(440),
  NOTE_AS4(466),
  NOTE_B4(494),
  NOTE_C5(523),
  NOTE_CS5(554),
  NOTE_D5(587),
  NOTE_DS5(622),
  NOTE_E5(659),
  NOTE_F5(698),
  NOTE_FS5(740),
  NOTE_G5(784),
  NOTE_GS5(831),
  NOTE_A5(880),
  NOTE_AS5(932),
  NOTE_B5(988),
  NOTE_C6(1047),
  NOTE_CS6(1109),
  NOTE_D6(1175),
  NOTE_DS6(1245),
  NOTE_E6(1319),
  NOTE_F6(1397),
  NOTE_FS6(1480),
  NOTE_G6(1568),
  NOTE_GS6(1661),
  NOTE_A6(1760),
  NOTE_AS6(1865),
  NOTE_B6(1976),
  NOTE_C7(2093),
  NOTE_CS7(2217),
  NOTE_D7(2349),
  NOTE_DS7(2489),
  NOTE_E7(2637),
  NOTE_F7(2794),
  NOTE_FS7(2960),
  NOTE_G7(3136),
  NOTE_GS7(3322),
  NOTE_A7(3520),
  NOTE_AS7(3729),
  NOTE_B7(3951);
  
  final int frequency;
  private Notes(int frequency) {
    this.frequency = frequency;
  }
  public String toString() {
    return this.name().substring(5);
  }
}

