#include "Sphero.h"

Sphero sphero;

void setup() {
  Serial.begin(115200);
  sphero.initialize();
}

void loop() {
  sphero.moveForward(128);
  delay(10000);
}