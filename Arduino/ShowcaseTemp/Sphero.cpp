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

void Sphero::setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight) {
  uint8_t leds[6] = {redLeft, greenLeft, blueLeft, redRight, greenRight, blueRight};
  rvr.setAllLeds(63, leds, 6);
}

void Sphero::getSensorData() {

}