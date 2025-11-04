#ifndef SPHERO_H
#define SPHERO_H

//#include <Arduino.h> // Include Arduino.h for Arduino-specific types and functions
#include <SpheroRVR.h>

extern SpheroRVR rvr;

class Sphero {
  public:
    void initialize();
    void drive(uint8_t speed, int heading);
    void stop(int lastHeading);
    void partyMode();
    void setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight);
    void getSensorData();
};

#endif