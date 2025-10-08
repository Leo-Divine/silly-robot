#![no_std]
#![no_main]

use arduino_hal::{Peripherals, Pins};
use panic_halt as _;

#[arduino_hal::entry]
fn main() -> ! {
    let dp: Peripherals = arduino_hal::Peripherals::take().unwrap();
    let _pins: Pins = arduino_hal::pins!(dp);

    loop {
      arduino_hal::delay_ms(100);
    }
}