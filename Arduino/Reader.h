#ifndef READER_H
#define READER_H

#include <string.h>
#include "ArduinoJson.h"
#include "Sphero.h"

int runCode(Sphero sphero, char* serializedCode);

#endif