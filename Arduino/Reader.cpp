#include "Reader.h"
#include "ArduinoJson.h"
#include "Sphero.h"

uint8_t runCode(Sphero sphero, char serializedCode[]) {
  JsonDocument blocks;

  DeserializationError error = deserializeJson(blocks, serializedCode);
  if (error) { return 1;}
  
  for (JsonObject block : blocks.as<JsonArray>()) {
    runBlock(sphero, block);
  }
  return 0;
}

uint8_t runBlock(Sphero sphero, JsonObject block) {
  if (block["b"] == 0) {
    delay(1000 * block["p"][0].as<long>());
  } else if (block["b"] == 1) {
    sphero.moveForward(
      block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0], 
      block["p"][1].is<JsonObject>()? runBlock(sphero, block["p"][1]) : block["p"][1]);
  } else if (block["b"] == 2) {
    sphero.rotateLeft();
  } else if (block["b"] == 3) {
    sphero.rotateRight();
  } else if (block["b"] == 4) {
    sphero.setColor(
      block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0]
    );
  } else if (block["b"] == 5) {
    if(runBlock(sphero, block["p"][0]) == 1) {
      for(JsonObject nestedBlock : block["p"][1].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    } else {
      for(JsonObject nestedBlock : block["p"][2].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    }
  } else if (block["b"] == 6) {
    uint8_t firstParameter = block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0];

    for(uint8_t i = 0; i < firstParameter; i++) {
      for(JsonObject nestedBlock : block["p"][1].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    }
  } else if (block["b"] == 7) {
    uint8_t firstParameter = block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0];
    uint8_t secondParameter = block["p"][1].is<JsonObject>()? runBlock(sphero, block["p"][1]) : block["p"][1];
    return firstParameter == secondParameter;
  } else if (block["b"] == 8) {
    uint8_t firstParameter = block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0];
    uint8_t secondParameter = block["p"][1].is<JsonObject>()? runBlock(sphero, block["p"][1]) : block["p"][1];
    return firstParameter < secondParameter;
  } else if (block["b"] == 9) {
    uint8_t firstParameter = block["p"][0].is<JsonObject>()? runBlock(sphero, block["p"][0]) : block["p"][0];
    uint8_t secondParameter = block["p"][1].is<JsonObject>()? runBlock(sphero, block["p"][1]) : block["p"][1];
    return firstParameter > secondParameter;
  } 

  return 0;
}