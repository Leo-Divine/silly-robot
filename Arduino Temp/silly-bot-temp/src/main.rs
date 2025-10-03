use no_std_strings::{str32, str_format};
use crate::block::{Block, BlockType};
use crate::sphero::Sphero;

mod block;
mod string;
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
    <Equal>
      <GetSensorValue/>
      <Number>
        40
      </Number>
    </Equal>
  ")[0]);
  
}

fn get_block(block: &str) -> [u8; 3] {
  // Find First Tag And End Index
  let mat_end_index = string::find(block, ">").expect("oof").end_index;
  let mat = &block[string::find(block, "<").expect("oof").start_index..mat_end_index];

  //Get the Number if Block is Number
  if mat == "<Number>" {
    let str_num = block[mat_end_index..string::find(block, "</").expect("oof").start_index].trim();
    return [str_num.parse::<u8>().expect("Error: Number's Value is not a valid Number"), 0, 0];
  }

  // Run the Block if Already Complete
  if mat.contains("/>") {
    return run_block(Block::new(
      BlockType::from_string(&mat[1..(mat.len() - 2)]).unwrap_or(BlockType::Wait),
      [0, 0, 0]
    ));
  }

  // Save the Block Type for Later
  let block_type_string = &mat[1..(mat.len() - 1)];

  // Go Through Each Subsequent Block Inside to Get Parameters
  let mut current_slice: &str = &block[mat_end_index..];
  let mut params: [u8; 3] = [0, 0, 0];
  let mut block_count = 0;

 
  while current_slice.trim() != str_format!(str32, "</{}>", str32::from(block_type_string)) {
    // Get The Start Tag And Its Indecies
    let start_match_start_index = string::find(current_slice, "<").expect("oof").start_index;
    let start_match_end_index = string::find(current_slice, ">").expect("oof").end_index;
    let start_match = &current_slice[start_match_start_index..start_match_end_index];

    // Save the Current Start Tag
    let mut tag = start_match;

    //Get the Full Block
    if !tag.contains("/>") {
      // Get End Tag Indecies
      let end_match_start_index = string::find(current_slice, "</").expect("oof").start_index;
      let end_match_end_index = string::find_after(current_slice, ">", end_match_start_index).expect("oof").end_index;

      // Get the Full Block
      tag = &current_slice[start_match_start_index..end_match_end_index];
      
      // Remove Previous Command
      current_slice = &current_slice[end_match_end_index..];
      
    } else {
      // Remove Previous Command
      current_slice = &current_slice[start_match_end_index..];
    }

    // Add Block's Value to Parameters
    params[block_count] = get_block(tag)[0];
    block_count += 1;
  }

  // Run Block
  return run_block(Block::new(
      BlockType::from_string(block_type_string).unwrap_or(BlockType::Wait),
      params
  ));
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
      todo!();
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