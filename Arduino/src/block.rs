use alloc::{boxed::Box, vec::Vec};

#[derive(PartialEq, Eq)]
pub enum Action {
  START,
  STOP
}

#[derive(Clone, Copy, PartialEq, Eq)]
pub enum BlockType {
  Wait,
  SetSpeed,
  Move,
  RotateLeft,
  RotateRight,
  SetLeftColor,
  SetRightColor
}

#[derive(Clone, Copy)]
pub struct Block {
  block: BlockType,
  params: [u8; 3]
}

impl Block {
  pub fn new(block: BlockType, params: [u8; 3]) -> Self {
    Self { block, params }
  }

  pub fn run_block(&mut self) {
    match self.block {
        BlockType::Wait => todo!(),
        BlockType::SetSpeed => todo!(),
        BlockType::Move => todo!(),
        BlockType::RotateLeft => todo!(),
        BlockType::RotateRight => todo!(),
        BlockType::SetLeftColor => todo!(),
        BlockType::SetRightColor => todo!(),
    }
  }
}

pub struct Command<'a> {
  pub action: Action,
  pub blocks: &'a mut [Block]
}

impl<'a> Command<'a> {
  pub fn new(action: Action, blocks: &'a mut [Block]) -> Self {
    Self { action, blocks }
  }
}