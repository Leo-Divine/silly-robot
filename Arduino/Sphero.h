#ifndef SPHERO_H
#define SPHERO_H

//#include <Arduino.h> // Include Arduino.h for Arduino-specific types and functions
#include <SpheroRVR.h>

extern SpheroRVR rvr;

class Sphero {
  public:
    void initialize();
    void moveForward(uint8_t speed, uint8_t length);
    void rotateRight();
    void rotateLeft();
    void setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight);
    float getSensorData();
    void playTone(int frequency, int duration);
    void stopTone();
};

#endif