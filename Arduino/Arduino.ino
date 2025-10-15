#include "Sphero.h"
#include "Reader.h"

Sphero sphero;

void setup() {
  Serial.begin(115220);
  sphero.initialize();
  delay(2000);
}

void loop() {
  char* code = R"([{"b":4,p:[3,0,0]},{"b":1,p:[128,1,0]},{"b":6,p:[3,[{"b":4,p:[4,0,0]},{"b":0,p:[1,0,0]},{"b":4,p:[5,0,0]},{"b":0,p:[1,0,0]}],0]}])";
  runCode(sphero, code);
  while(true);
}

/* 
    {"block": "Loop", parameters: [
      3, 
      [
        {"block": "SetColor", parameters: [4, 0, 0]},
        {"block": "Wait", parameters: [1, 0, 0]},
        {"block": "SetColor", parameters: [5, 0, 0]},
        {"block": "Wait", parameters: [1, 0, 0]}
      ], 
      0
    ]
  },*/

    /*
    {"block": "If", parameters: [
    {"block": "Equal", parameters: [4, 3, 0]}, 
    [
      {"block": "SetColor", parameters: [3, 0, 0]}
    ], 
    [
      {"block": "SetColor", parameters: [0, 0, 0]}
    ]
  ]},
  {"block": "Wait", parameters: [2, 0, 0]}
    */
    /**/ 
    /* [{"b":6,"p":[4,[{"b":3,"p":[0,0,0]}],0]}]*/    // Turn 4 Times