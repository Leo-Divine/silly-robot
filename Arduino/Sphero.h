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
    void setRightColor(uint8_t red, uint8_t green, uint8_t blue);
    void setLeftColor(uint8_t red, uint8_t green, uint8_t blue);
    void getSensorData();

  private:
    uint8_t sensorDistance;
    uint32_t ledGroup;
    uint8_t LEDColors[6];
};

#endif