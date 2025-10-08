#include "Sphero.h"

Sphero::Sphero() {
  sensorDistance = 0;
}

void Sphero::initialize() {
  rvr.configUART(&Serial);
  rvr.resetYaw();
}

void Sphero::moveForward(int speed) {
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
}

void Sphero::getSensorData() {

}