#include "Reader.h"
#include "ArduinoJson.h"
#include "Sphero.h"

int runCode(Sphero sphero, char serializedCode[]) {
  JsonDocument blocks;

  DeserializationError error = deserializeJson(blocks, serializedCode);
  if (error) {
    Serial.println(error.c_str());
    return 1;
  }
  
  for (JsonObject block : blocks.as<JsonArray>()) {
    if (block["block"] == "Wait") {
      delay(1000 * block["parameters"][0].as<long>());
    } else if (block["block"] == "MoveForward") {
      sphero.moveForward(block["parameters"][0], block["parameters"][1]);
    } else if (block["block"] == "RotateLeft") {
      sphero.rotateLeft();
    } else if (block["block"] == "RotateRight") {
      sphero.rotateRight();
    } else if (block["block"] == "SetRightColor") {
      sphero.setRightColor(block["parameters"][0], block["parameters"][1], block["parameters"][2]);
    } else if (block["block"] == "SetLeftColor") {
      sphero.setLeftColor(block["parameters"][0], block["parameters"][1], block["parameters"][2]);
    }
  }
  
  return 0;
}