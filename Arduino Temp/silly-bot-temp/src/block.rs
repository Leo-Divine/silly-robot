#[derive(PartialEq, Eq)]
pub enum Action {
  START,
  STOP
}

#[derive(PartialEq, Eq)]
pub enum BlockType {
  Wait,
  SetSpeed,
  Move,
  RotateLeft,
  RotateRight,
  SetLeftColor,
  SetRightColor
}

pub struct Block {
  pub block: BlockType,
  pub params: [u8; 3]
}

impl Block {
  pub fn new(block: BlockType, params: [u8; 3]) -> Self {
    Self { block, params }
  }
}

pub struct Command {
  pub action: Action,
  pub blocks: [Block; 255],
}

impl Command {
  pub fn new(action: Action, blocks: [Block; 255]) -> Self {
    Self { action, blocks }
  }
}