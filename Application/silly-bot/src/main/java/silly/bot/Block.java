package silly.bot;

enum BlockType {
    Wait,
    SetSpeed,
    Move,
    RotateLeft,
    RotateRight,
    SetLeftColor,
    SetRightColor
}

public class Block {
    BlockType blockType;

    public Block(BlockType b) {
        blockType = b;
    }
}
