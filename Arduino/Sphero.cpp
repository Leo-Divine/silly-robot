#include "Sphero.h"

Sphero::Sphero() {
  ledGroup = 0;
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightRed));
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightGreen));
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightBlue));
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightRed));
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightGreen));
  ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightBlue));

  sensorDistance = 0;

  LEDColors[0] = 0x00;
  LEDColors[1] = 0x00;
  LEDColors[2] = 0xFF;
  LEDColors[3] = 0x00;
  LEDColors[4] = 0x00;
  LEDColors[5] = 0xFF;
}

void Sphero::initialize() {
  rvr.poll();
  rvr.configUART(&Serial);

  rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));

  rvr.resetYaw();
}

void Sphero::moveForward(uint8_t speed, uint8_t length) {
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

void Sphero::setRightColor(uint8_t red, uint8_t green, uint8_t blue) {
  LEDColors[3] = red;
  LEDColors[4] = green;
  LEDColors[5] = blue;

  rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));
}

void Sphero::setLeftColor(uint8_t red, uint8_t green, uint8_t blue) {
  LEDColors[0] = red;
  LEDColors[1] = green;
  LEDColors[2] = blue;

  rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));
}

void Sphero::getSensorData() {

}