#ifndef SPHERO_H
#define SPHERO_H

//#include <Arduino.h> // Include Arduino.h for Arduino-specific types and functions
#include <SpheroRVR.h>

extern SpheroRVR rvr;

class Sphero {
  public:
    Sphero();
    void initialize();
    void moveForward(uint8_t speed, uint8_t length);
    void rotateRight();
    void rotateLeft();
    void setColor(uint8_t colorCode);
    void getSensorData();
    
};

#endif