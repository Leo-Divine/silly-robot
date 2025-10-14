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

uint8_t* runBlock(Sphero sphero, JsonObject block) {
  if (block["block"] == "Wait") {
    delay(1000 * block["parameters"][0].as<long>());
  } else if (block["block"] == "MoveForward") {
    sphero.moveForward(
      block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0])[0] : block["parameters"][0], 
      block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1])[0] : block["parameters"][1]);
  } else if (block["block"] == "RotateLeft") {
    sphero.rotateLeft();
  } else if (block["block"] == "RotateRight") {
    sphero.rotateRight();
  } else if (block["block"] == "SetColor") {
    sphero.setColor(
      block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0])[0] : block["parameters"][0]
    );
  } else if (block["block"] == "GetColor") {
    uint8_t returnParameters[] = {128, 0, 0};
    return returnParameters;
  } else if (block["block"] == "If") {
    uint8_t test = runBlock(sphero, block["parameters"][0])[2];
    Serial.println(test);
    if(runBlock(sphero, block["parameters"][0])[0] == 1) {
      for(JsonObject nestedBlock : block["parameters"][1].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    } else {
      for(JsonObject nestedBlock : block["parameters"][2].as<JsonArray>()) {
        runBlock(sphero, nestedBlock);
      }
    }
  } else if (block["block"] == "Equal") {
    // Get Parameters
    uint8_t firstParameter = block["parameters"][0].is<JsonObject>()? runBlock(sphero, block["parameters"][0])[0] : block["parameters"][0];
    uint8_t secondParameter = block["parameters"][1].is<JsonObject>()? runBlock(sphero, block["parameters"][1])[0] : block["parameters"][1];
    Serial.println(firstParameter == secondParameter);
    Serial.println(secondParameter);

    // Equate and Return
    uint8_t returnParameters[3];
    if(firstParameter == secondParameter) { returnParameters[0] = 1; }
    return returnParameters;
  }
}