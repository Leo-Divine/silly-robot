#ifndef SPHERO_H
#define SPHERO_H

//#include <Arduino.h> // Include Arduino.h for Arduino-specific types and functions
#include <SpheroRVR.h>

extern SpheroRVR rvr;

class Sphero {
  public:
    Sphero();
    void initialize();
    void moveForward(int speed);
    void getSensorData();

  private:
    int sensorDistance;
};

#endif