#include "Sphero.h"
#include "Reader.h"

Sphero sphero;

char* test = R"([
    {"block": "SetColor", parameters: [0, 0, 0]},
    {"block": "MoveForward", parameters: [128, 1, 0]}
  ])";

void setup() {
  Serial.begin(115220);
  sphero.initialize();
  
}

void loop() {
  runCode(sphero, test);
  delay(1000);
}