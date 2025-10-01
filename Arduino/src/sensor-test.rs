#![no_std]
#![no_main]

use arduino_hal::port::Pin;
use panic_halt as _;

#[arduino_hal::entry]
fn main() -> ! {
    let dp = arduino_hal::Peripherals::take().unwrap();
    let pins = arduino_hal::pins!(dp);

    let mut light = pins.a0.into_output().downgrade();

    // Ultrasensor Variables
    let mut trig = pins.d25.into_output();
    let echo = pins.d26; // pin is input by default
    let mut ultrasensor_timer: arduino_hal::pac::TC1 = dp.TC1;
    ultrasensor_timer.tccr1b.write(|w| w.cs1().prescale_64());
    let mut sensor_value: u16;

    loop {
        match get_ultrasonic_sensor_value(&mut trig, &echo, &mut ultrasensor_timer) {
            Some(value) => sensor_value = value,
            None => sensor_value = 1000,
        };

        if sensor_value < 15 {
            light.set_high();
        } else {
            light.set_low();
        }
    }
}

fn get_ultrasonic_sensor_value(trig: &mut Pin<arduino_hal::port::mode::Output, arduino_hal::hal::port::PA3>,
        echo: &Pin<arduino_hal::port::mode::Input<arduino_hal::port::mode::Floating>, arduino_hal::hal::port::PA4>,
        timer: &mut arduino_hal::pac::TC1) -> Option<u16> {
    // the timer is reinitialized with value 0.
        timer.tcnt1.write(|w| w.bits(0));

        // the trigger must be set to high under 10 µs as per the HC-SR04 datasheet
        trig.set_high();
        arduino_hal::delay_us(10);
        trig.set_low();

        while echo.is_low() {
            // exiting the loop if the timer has reached 200 ms.
            // 0.2s/4µs = 50000
            if timer.tcnt1.read().bits() >= 50000 {
                // jump to the beginning of the outer loop if no obstacle is detected
                return None;
            }
        }
        // Restarting the timer
        timer.tcnt1.write(|w| w.bits(0));

        // Wait for the echo to get low again
        while echo.is_high() {}

        // 1 count == 4 µs, so the value is multiplied by 4.
        // 1/58 ≈ (34000 cm/s) * 1µs / 2
        // when no object is detected, instead of keeping the echo pin completely low,
        // some HC-SR04 labeled sensor holds the echo pin in high state for very long time,
        // thus overflowing the u16 value when multiplying the timer1 value with 4.
        // overflow during runtime causes panic! so it must be handled
        let temp_timer = timer.tcnt1.read().bits().saturating_mul(4);
        let value = match temp_timer {
            u16::MAX => {
                return None;
            }
            _ => temp_timer / 58,
        };

        // Await 100 ms before sending the next trig
        // 0.1s/4µs = 25000
        while timer.tcnt1.read().bits() < 25000 {}

        return Some(value);
}