#include <SpheroRVR.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <stdio.h>

// Colors
static uint32_t ledGroup;
const uint8_t RED[] = {0xFF, 0x00, 0x00};
const uint8_t ORANGE[] = {0xFF, 0xA5, 0x00};
const uint8_t YELLOW[] = {0xFF, 0xFF, 0x00};
const uint8_t GREEN[] = {0x00, 0xFF, 0x00};
const uint8_t BLUE[] = {0x00, 0x00, 0xFF};
const uint8_t PURPLE[] = {0x76, 0x00, 0xBC};
const uint8_t WHITE[] = {0xFF, 0xFF, 0xFF};
uint8_t LEDColors[] = {0x00, 0x00, 0xFF, 0x00, 0x00, 0xFF};

char command[] = "0001001255000000";
uint8_t lineCount;
bool isProgramRunning = true;

void setup() {
    Serial.begin(9600);
    rvr.configUART(&Serial);
    ledGroup = 0;
    
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightRed));
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightGreen));
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::rightHeadlightBlue));
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightRed));
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightGreen));
    ledGroup |= (1 << static_cast<uint8_t>(LEDs::leftHeadlightBlue));

    rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));
}

void loop() {
  if(isProgramRunning) {
    if(lineCount == 0) { setLineCount(); }
    for(int i = 0; i < lineCount; i++) {
      // Declare variables to retrieve
      char substring[4];
      uint8_t block;
      uint8_t firstParameter;
      uint8_t secondParameter;
      uint8_t thirdParameter;

      // Get Block Substring
      strncpy(substring, command + (i * 12 + 4) + 0, 3);
      block = atoi(substring);
      
      // Get Block Substring
      strncpy(substring, command + (i * 12 + 4) + 3, 3);
      firstParameter = atoi(substring);

      // Get Block Substring
      strncpy(substring, command + (i * 12 + 4) + 6, 3);
      secondParameter = atoi(substring);

      // Get Block Substring
      strncpy(substring, command + (i * 12 + 4) + 9, 3);
      thirdParameter = atoi(substring);

      // Run The Retrieved Block Data
      runBlock(block, firstParameter, secondParameter, thirdParameter);
    }
  }
  lineCount = 0;
}

void setLineCount() {
  char substring[4];
  strncpy(substring, command + 1, 3);
  lineCount = atoi(substring);
}

uint8_t* getColorFromCode(uint8_t code) {
  switch(code) {
    case 0:
      return RED;
      break;
    case 1:
      return ORANGE;
      break;
    case 2:
      return YELLOW;
      break;
    case 3:
      return GREEN;
      break;
    case 4:
      return BLUE;
      break;
    case 5:
      return PURPLE;
      break;
    case 6:
      return WHITE;
      break;
    default:
      return RED;
      break;
  }
}

void runBlock(uint8_t block, uint8_t firstParameter, uint8_t secondParameter, uint8_t thirdParameter) {
  switch(block) {
    case 0:
      setSpeed(firstParameter);
      break;
    case 1:
      setRightColor(getColorFromCode(firstParameter));
      break;
  }
}

void setSpeed(uint8_t speed) {
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
}

void setRightColor(uint8_t chosenColor[]) {
  LEDColors[3] = chosenColor[0];
  LEDColors[4] = chosenColor[1];
  LEDColors[5] = chosenColor[2];

  Serial.println(ledGroup);
  Serial.println(LEDColors[0]);
  Serial.println(sizeof(LEDColors) / sizeof(LEDColors[0]));

  rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));
}

void setLeftColor(uint8_t chosenColor[]) {
  LEDColors[0] = chosenColor[0];
  LEDColors[1] = chosenColor[1];
  LEDColors[2] = chosenColor[2];

  rvr.setAllLeds(ledGroup, LEDColors, sizeof(LEDColors) / sizeof(LEDColors[0]));
}