use crate::block::{Action, Block, BlockType, Command};

mod block;
mod sphero;

fn main() {
  // Declare Global Variables
  let mut command: Command;
  let mut is_program_running: bool = false;

  loop {
    // Check for a New Command and Set it
    todo!();

    // Check The Action And Act Accordingly
    is_program_running = (command.action == Action::START);

    // Continue Loop if Action is Stop
    if !is_program_running { continue; }

    // Run Each Block
    for block in command.blocks {
      match block.block {
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
}
