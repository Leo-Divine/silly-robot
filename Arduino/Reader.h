#ifndef READER_H
#define READER_H

#include <string.h>
#include "ArduinoJson.h"
#include "Sphero.h"

uint8_t runCode(Sphero sphero, char* serializedCode);
uint8_t runBlock(Sphero sphero, JsonObject block);

#endif