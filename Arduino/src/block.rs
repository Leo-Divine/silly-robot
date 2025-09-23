use alloc::{boxed::Box, vec::Vec};

pub enum Action {
  START,
  STOP
}

#[derive(PartialEq, Eq)]
pub enum BlockType {
  SetSpeed,
  Move,
  RotateLeft,
  RotateRight,
  SetLeftColor,
  SetRightColor
}

pub struct Block {
  block: BlockType,
  params: [u8; 3]
}

pub struct Command<'a> {
  action: Action,
  blocks: &'a mut [Block]
}

impl<'a> Command<'a> {
  pub fn new(action: Action, blocks: &'a mut [Block]) -> Self {
        Self { action, blocks }
    }
}