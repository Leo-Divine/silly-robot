#![no_std]
#![no_main]

//extern crate alloc;

use arduino_hal::{Peripherals, Pins, port::Pin};
use panic_halt as _;
//use crate::block::{Command, Action, Block, BlockType};

//mod block;

#[arduino_hal::entry]
fn main() -> ! {
    let dp: Peripherals = arduino_hal::Peripherals::take().unwrap();
    let pins: Pins = arduino_hal::pins!(dp);

    // set up serial interface for text output
    let mut serial = arduino_hal::default_serial!(dp, pins, 57600);
    let mut light = pins.a0.into_output().downgrade();

    loop {
        light.set_high();
        arduino_hal::delay_ms(1000);
    }
}

fn get_ultrasonic_sensor_value(trig: &mut Pin<arduino_hal::port::mode::Output, arduino_hal::hal::port::PA3>,
        echo: &Pin<arduino_hal::port::mode::Input<arduino_hal::port::mode::Floating>, arduino_hal::hal::port::PA4>,
        timer: &mut arduino_hal::pac::TC1) -> Option<u16> {
            
        // Set timer to 0
        timer.tcnt1.write(|w| w.bits(0));

        // Send out a Wave to Detect Time
        trig.set_high();
        arduino_hal::delay_us(10);
        trig.set_low();

        // Wait 200ms to see if anything is found in front of it. If nothing: return.
        while echo.is_low() {
            if timer.tcnt1.read().bits() >= 50000 { return None; }
        }

        // Restart Timer
        timer.tcnt1.write(|w| w.bits(0));

        // Wait For Echo to be Low
        while echo.is_high() {}

        // Overflow Handling
        let temp_timer = timer.tcnt1.read().bits().saturating_mul(4);
        let value = match temp_timer {
            u16::MAX => {
                return None;
            }
            _ => temp_timer / 58,
        };

        // Wait 100ms Before Next Signal
        while timer.tcnt1.read().bits() < 25000 {}

        Some(value)
}