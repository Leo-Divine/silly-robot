#![no_std]
#![no_main]

extern crate alloc;

use arduino_hal::{prelude::*, spi, Peripherals, Pins};
use panic_halt as _;
use crate::block::Command;

mod block;

#[arduino_hal::entry]
fn main() -> ! {
    let dp: Peripherals = arduino_hal::Peripherals::take().unwrap();
    let pins: Pins = arduino_hal::pins!(dp);

    let command: Command = Command::new() ;
    let is_program_running = false;

    // set up serial interface for text output
    let mut serial = arduino_hal::default_serial!(dp, pins, 57600);

    let mut buzzer = pins.a0.into_output();
    

    loop {
        buzzer.toggle();
        arduino_hal::delay_ms(1000);
    }
}
