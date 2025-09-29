#![no_std]
#![no_main]

extern crate alloc;

use arduino_hal:: {Peripherals, Pins};
use panic_halt as _;
//use crate::block::{Command, Action, Block, BlockType};
#[cfg(not(target_env = "msvc"))]
use tikv_jemallocator::Jemalloc;

#[cfg(not(target_env = "msvc"))]
#[global_allocator]
static GLOBAL: Jemalloc = Jemalloc;

mod block;

#[arduino_hal::entry]
fn main() -> ! {
    let dp: Peripherals = arduino_hal::Peripherals::take().unwrap();
    let pins: Pins = arduino_hal::pins!(dp);

    /* 
    let command: Command = Command::new(
        Action::START,
        &mut [
            Block::new(
                BlockType::Move,
                [ 255, 0, 0 ]
            )
        ]
    );

    let mut is_program_running = false;
    let mut lineCount: u32 = 0;
    */

    // set up serial interface for text output
    let mut serial = arduino_hal::default_serial!(dp, pins, 57600);
    let mut light = pins.a0.into_output().downgrade();

    loop {
        /*
        // Check if Command is START or STOP and act Accordingly
        if command.action == Action::STOP { lineCount = 0; }
        is_program_running = command.action == Action::START;

        // Continue if Program isn't Running
        if !is_program_running { continue; }

        // Set lineCount if 0

        // Loop Through Blocks
        for block in command.blocks.into_iter() {
            block.run_block();
        }
        */

        light.toggle();
        arduino_hal::delay_ms(1000);
    }
}
