#include "Sphero.h"
#include "Reader.h"

Sphero sphero;

char* test = R"([
    {"block": "SetRightColor", parameters: [255, 0, 0]},
    {"block": "SetLeftColor", parameters: [0, 0, 255]}
  ])";

void setup() {
  Serial.begin(115200);
  sphero.initialize();
}

void loop() {
  runCode(sphero, test);
  delay(4000);
}

/*
{"block": "RotateLeft", parameters: [0, 0, 0]},
    {"block": "Wait", parameters: [4, 0, 0]},
    {"block": "RotateRight", parameters: [0, 0, 0]}
*/