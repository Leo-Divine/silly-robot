#[derive(PartialEq, Eq)]
pub enum BlockType {
  Wait,
  SetSpeed,
  RotateLeft,
  RotateRight,
  SetLeftColor,
  SetRightColor,
  GetSensorDistance,
  Number,
  Equal,
}

impl BlockType {
  pub fn from_string(string: &str) -> Option<Self> {
    match string {
      "Wait" => return Some(BlockType::Wait),
      "SetSpeed" => return Some(BlockType::SetSpeed),
      "RotateLeft" => return Some(BlockType::RotateLeft),
      "RotateRight" => return Some(BlockType::RotateRight),
      "SetLeftColor" => return Some(BlockType::SetLeftColor),
      "SetRightColor" => return Some(BlockType::SetRightColor),
      "GetSensorValue" => return Some(BlockType::GetSensorDistance),
      "Number" => return Some(BlockType::Number),
      "Equal" => return Some(BlockType::Equal),
      _ => None,
    }
  }
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
