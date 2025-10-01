use regex::Regex;

use crate::block::{Block, BlockType};
use crate::sphero::Sphero;

mod block;
mod sphero;

fn main() {
  // Declare Global Variables
  //let mut current_command: Command;
  let command: &str = "
  <If>
    <Statement>
      <Equal>
        <GetSensorValue/>
        <Number>7</Number>
      </Equal>
    <Statement>
    <True>
      <SetRightColor>
        <Number>255</Number>
      </SetRightColor>
    </True>
  </If>
  <If></If>
  ";

  println!("{}", get_block("
    <SetRightColor>
      <Number>
        255
      </Number>
    </SetRightColor>
  ")[0]);
}

fn get_block(block: &str) -> [u8; 3] {
  let tag_pattern = Regex::new("<.*>").unwrap();
  if let Some(mat) = tag_pattern.find(block) {
    //Get the Number if Block is Number
    if mat.as_str() == "<Number>" {
      let str_num = block.replace("<Number>", "").replace("</Number>", "").trim().to_string();
      return [str_num.parse::<u8>().expect("Error: Number's Value is not a valid Number"), 0, 0];
    }

    // Run the Block if Already Complete
    if mat.as_str().contains("/>") {
      return run_block(Block::new(
        BlockType::from_string(&mat.as_str()[1..(mat.as_str().len() - 2)]).unwrap_or(BlockType::Wait),
        [0, 0, 0]
      ));
    }

    // Save the Block Type for Later
    let block_type_string = &mat.as_str()[1..(mat.as_str().len() - 1)];

    // Go Through Each Subsequent Block Inside to Get Parameters
    let mut current_slice: &str = &block.replacen(mat.as_str(), "", 1);
    let mut params: [u8; 3] = [0, 0, 0];
    let mut block_count = 0;

    while current_slice.trim() != "</".to_owned() + block_type_string + ">" {
      if let Some(start_match) = tag_pattern.find(&current_slice) {
        // Save the Current Start Tag
        let mut tag = start_match.as_str();

        //Get the Full Block
        if !tag.contains("/>") {
          let end_tag_pattern = Regex::new(&tag.replace("<", "</")).unwrap();
          if let Some(end_match) = end_tag_pattern.find(&current_slice) {
            tag = &current_slice[start_match.start()..end_match.end()];

            // Remove Previous Command
            current_slice = &current_slice[end_match.end()..];
          }
        } else {
          // Remove Previous Command
          current_slice = &current_slice[start_match.end()..];
        }

        // Add Block's Value to Parameters
        params[block_count] = get_block(tag)[0];
        block_count += 1;
      }
    }

    // Run Block
    return run_block(Block::new(
        BlockType::from_string(block_type_string).unwrap_or(BlockType::Wait),
        params
    ));
  }
  [0, 0, 0]
}

fn run_block(block: Block) -> [u8; 3] {
  match block.block {
    BlockType::Wait => {
      Sphero::wait();
    },
    BlockType::SetSpeed => {
      todo!();
    },
    BlockType::RotateLeft => {
      todo!();
    },
    BlockType::RotateRight => {
      todo!();
    },
    BlockType::SetLeftColor => {
      todo!();
    },
    BlockType::SetRightColor => {
      println!("test");
    },
    BlockType::GetSensorDistance => {
      return [Sphero::get_ultrasonic_sensor_value().unwrap_or(255), 0, 0];
    },
    BlockType::Number => {
      return [block.params[0], 0, 0];
    },
    BlockType::Equal => {
      return[(block.params[0] == block.params[1]) as u8, 0, 0];
    },
    }
  return [0, 0, 0];
}

