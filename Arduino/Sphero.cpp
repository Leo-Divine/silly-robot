#include "Sphero.h"

void Sphero::initialize() {
  rvr.configUART(&Serial);
  rvr.resetYaw();
}

void Sphero::moveForward(uint8_t speed, uint8_t length) {
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
  delay(1000 * length);
  rvr.rawMotors(RawMotorModes::forward, 0, RawMotorModes::forward, 0);
}

void Sphero::rotateRight() {
  rvr.getDriveControl().rollStart(93, 64);
  delay(950);
  rvr.getDriveControl().rollStop(93);
  rvr.resetYaw();
  delay(150);
}

void Sphero::rotateLeft() {
  rvr.getDriveControl().rollStart(267, 64);
  delay(950);
  rvr.getDriveControl().rollStop(267);
  rvr.resetYaw();
  delay(150);
}

void Sphero::setColor(uint8_t colorCode) {
  uint8_t leds[6] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
  switch(colorCode) {
    case 0:
      leds[0] = 0xFF;
      leds[3] = 0xFF;
      break;
    case 1:
      leds[0] = 0xFF;
      leds[1] = 0xA5;
      leds[3] = 0xFF;
      leds[4] = 0xA5;
      break;
    case 2:
      leds[0] = 0xFF;
      leds[1] = 0xFF;
      leds[3] = 0xFF;
      leds[4] = 0xFF;
      break;
    case 3:
      leds[1] = 0xFF;
      leds[4] = 0xFF;
      break;
    case 4:
      leds[2] = 0xFF;
      leds[5] = 0xFF;
      break;
    case 5:
      leds[0] = 0x76;
      leds[2] = 0xBC;
      leds[3] = 0x76;
      leds[5] = 0xBC;
      break;
    case 6:
      leds[0] = 0xFF;
      leds[1] = 0xFF;
      leds[2] = 0xFF;
      leds[3] = 0xFF;
      leds[4] = 0xFF;
      leds[5] = 0xFF;
      break;
    default:
      break;
  }
  rvr.setAllLeds(63, leds, 6);
}

void Sphero::getSensorData() {

}