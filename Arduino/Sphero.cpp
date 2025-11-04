#include "Sphero.h"

void Sphero::initialize() {
  rvr.configUART(&Serial);
  rvr.resetYaw();
}

void Sphero::drive(uint8_t speed, int heading) {
  rvr.getDriveControl().rollStart(heading, speed);
}

void Sphero::stop(int lastHeading) {
  rvr.getDriveControl().rollStop(lastHeading);
}

void Sphero::partyMode() {
  uint8_t leds[6] = {255, 0, 0, 255, 0, 0};
  rvr.setAllLeds(63, leds, 6);
  delay(400);

  leds[1] = 127;
  leds[4] = 127;
  rvr.setAllLeds(63, leds, 6);
  delay(400);

  leds[1] = 255;
  leds[4] = 255;
  rvr.setAllLeds(63, leds, 6);
  delay(400);

  leds[0] = 0;
  leds[3] = 0;
  rvr.setAllLeds(63, leds, 6);
  delay(400);

  leds[1] = 0;
  leds[4] = 0;
  leds[2] = 255;
  leds[5] = 255;
  rvr.setAllLeds(63, leds, 6);
  delay(400);

  leds[0] = 148;
  leds[3] = 148;
  leds[2] = 233;
  leds[5] = 233;
  rvr.setAllLeds(63, leds, 6);
  delay(200);
}

void Sphero::setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight) {
  uint8_t leds[6] = {redLeft, greenLeft, blueLeft, redRight, greenRight, blueRight};
  rvr.setAllLeds(63, leds, 6);
}

void Sphero::getSensorData() {

}