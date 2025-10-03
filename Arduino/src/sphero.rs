use arduino_hal::{port::Pin};

pub struct Sphero {
  
}

impl Sphero {
  pub fn wait() {
    todo!();
  }

  pub fn get_ultrasonic_sensor_value(trig: &mut Pin<arduino_hal::port::mode::Output, arduino_hal::hal::port::PA3>,
    echo: &Pin<arduino_hal::port::mode::Input<arduino_hal::port::mode::Floating>, arduino_hal::hal::port::PA4>,
    ultrasensor_timer: &mut arduino_hal::pac::TC1) -> Option<u8> {

    // Set timer to 0
    ultrasensor_timer.tcnt1.write(|w| w.bits(0));

    // Send out a Wave to Detect Time
    trig.set_high();
    arduino_hal::delay_us(10);
    trig.set_low();

    // Wait 200ms to see if anything is found in front of it. If nothing: return.
    while echo.is_low() {
        if ultrasensor_timer.tcnt1.read().bits() >= 50000 { return None; }
    }

    // Restart Timer
    ultrasensor_timer.tcnt1.write(|w| w.bits(0));

    // Wait For Echo to be Low
    while echo.is_high() {}

    // Overflow Handling
    let temp_timer = ultrasensor_timer.tcnt1.read().bits().saturating_mul(4);
    let mut value = match temp_timer {
        u16::MAX => {
            return None;
        }
        _ => temp_timer / 58,
    };

    // Wait 100ms Before Next Signal
    while ultrasensor_timer.tcnt1.read().bits() < 25000 {}

    if value > 255 {
      value = 255;
    }
    Some(value as u8)
  }
}

pub struct SpheroData {
  sensor_value: u8
}

impl SpheroData {
  pub fn new(sensor_value: u8) -> Self {
    Self { sensor_value }
  }
}