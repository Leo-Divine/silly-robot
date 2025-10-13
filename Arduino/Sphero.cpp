#include "Sphero.h"

Sphero::Sphero() {

}

void Sphero::initialize() {
  rvr.configUART(&Serial);
  rvr.resetYaw();
}

void Sphero::moveForward(uint8_t speed, uint8_t length) {
  Serial.println("test");
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
  delay(1000 * length);
}

void Sphero::rotateRight() {
  rvr.getDriveControl().rollStart(93, 64);
  delay(950);
  rvr.getDriveControl().rollStop(93);
  rvr.resetYaw();
}

void Sphero::rotateLeft() {
  rvr.getDriveControl().rollStart(267, 64);
  delay(950);
  rvr.getDriveControl().rollStop(267);
  rvr.resetYaw();
}

void Sphero::setColor(uint8_t colorCode) {
  uint8_t leds[6] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
  switch(colorCode) {
    case 0:
      leds[0] = 0xFF;
      leds[3] = 0xFF;
      break;
    default:
      break;
  }
  rvr.setAllLeds(63, leds, 6);
}

void Sphero::getSensorData() {

}