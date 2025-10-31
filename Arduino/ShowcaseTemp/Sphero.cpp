#include "Sphero.h"

void Sphero::initialize() {
  rvr.configUART(&Serial);
  rvr.resetYaw();
}

void Sphero::moveForward(uint8_t speed) {
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
}

void Sphero::moveBackward(uint8_t speed) {
  rvr.rawMotors(RawMotorModes::reverse, speed, RawMotorModes::reverse, speed);
}

void Sphero::stopMoving() {
  rvr.rawMotors(RawMotorModes::forward, 0, RawMotorModes::forward, 0);
}

void Sphero::rotateRight() {
  rvr.resetYaw();
  rvr.getDriveControl().rollStart(93, 64);
  delay(950);
  rvr.getDriveControl().rollStop(93);
  rvr.resetYaw();
  delay(150);
}

void Sphero::rotateLeft() {
  rvr.resetYaw();
  rvr.getDriveControl().rollStart(267, 64);
  delay(950);
  rvr.getDriveControl().rollStop(267);
  rvr.resetYaw();
  delay(150);
}

void Sphero::setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight) {
  uint8_t leds[6] = {redLeft, greenLeft, blueLeft, redRight, greenRight, blueRight};
  rvr.setAllLeds(63, leds, 6);
}

void Sphero::getSensorData() {

}