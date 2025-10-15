#include "Reader.h"
#include "ArduinoJson.h"
#include "Sphero.h"

int runCode(Sphero sphero, char serializedCode[]) {
  JsonDocument blocks;

  DeserializationError error = deserializeJson(blocks, serializedCode);
  if (error) {
    return 1;
  }
  
  for (JsonObject block : blocks.as<JsonArray>()) {
    runBlock(sphero, block);
  }
  
  return 0;
}

uint8_t runBlock(Sphero sphero, JsonObject block) {
  if (block["block"] == "Wait") {
    delay(1000 * block["parameters"][0].as<long>());
  } else if (block["block"] == "MoveForward") {
    sphero.moveForward(
      block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0]) : block["parameters"][0], 
      block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1]) : block["parameters"][1]);
  } else if (block["block"] == "RotateLeft") {
    sphero.rotateLeft();
  } else if (block["block"] == "RotateRight") {
    sphero.rotateRight();
  } else if (block["block"] == "SetColor") {
    sphero.setColor(
      block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0]) : block["parameters"][0]
    );
  } else if (block["block"] == "If") {
    if(runBlock(sphero, block["parameters"][0]) == 1) {
      for(JsonObject nestedBlock : block["parameters"][1].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    } else {
      for(JsonObject nestedBlock : block["parameters"][2].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    }
  } else if (block["block"] == "Equal") {
    uint8_t firstParameter = block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0]) : block["parameters"][0];
    uint8_t secondParameter = block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1]) : block["parameters"][1];
    return firstParameter == secondParameter;
  } else if (block["block"] == "Less") {
    uint8_t firstParameter = block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0]) : block["parameters"][0];
    uint8_t secondParameter = block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1]) : block["parameters"][1];
    return firstParameter < secondParameter;
  } else if (block["block"] == "Greater") {
    uint8_t firstParameter = block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0]) : block["parameters"][0];
    uint8_t secondParameter = block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1]) : block["parameters"][1];
    return firstParameter > secondParameter;
  }

  return 0;
}