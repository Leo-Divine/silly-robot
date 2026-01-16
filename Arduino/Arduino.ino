#include "WiFiEsp.h"
#include "Sphero.h"

#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(2, 3); // RX, TX
#endif

char ssid[] = "Rossini Family";
char pass[] = "Ajgabmas171";
int status = WL_IDLE_STATUS;
char server[] = "192.168.68.68";
unsigned long lastConnectionTime = 0;
const unsigned long postingInterval = 15000L; // 1 Min

WiFiEspClient client;
Sphero sphero;

void setup()
{
  // Set up Robot
  Serial.begin(115200);
  sphero.initialize();

  // Set up WiFi
  Serial1.begin(9600);
  WiFi.init(&Serial1);

  // Check for WiFi Shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);
  }

  // Connect to WiFi
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }
  Serial.println("You're connected to the network");
  printWifiStatus();

  connectToServer();
}

void loop()
{
  String c = "";
  while (client.available()) {
    c += static_cast<char>(client.read());
    if(client.peek() != 13) { continue; };

    while(client.available()) {
      client.read();
    }

    Serial.print(c.substring(0, 5));
    if(c.substring(0, 5) == "R_000") {
      sphero.moveForward(c.substring(5, 8).toInt(), c.substring(8, 11).toInt());
    } else if(c.substring(0, 5) == "R_001") {
      sphero.rotateLeft();
    } else if(c.substring(0, 5) == "R_002") {
      sphero.rotateRight();
    } else if(c.substring(0, 5) == "R_003") {
      int* leftColor = hexToRGB(c.substring(5, 11));
      int* rightColor = hexToRGB(c.substring(11, 17));
      sphero.setColor(
        leftColor[0],
        leftColor[1],
        leftColor[2],
        rightColor[0],
        rightColor[1],
        rightColor[2]
      );
    } else if(c.substring(0, 5) == "R_004") {
      client.println(sphero.getSensorData());
    } else if(c.substring(0, 5) == "R_005") {
      client.println("Next Bitch");
      sphero.playTone(c.substring(5, 9).toInt(), c.substring(9, 13).toInt());
    } else {
      client.println("Beep Boop Does not Compoop");
    }
    
    lastConnectionTime = millis();
    c = "";
    delay(2000);
  }
  
  if (millis() - lastConnectionTime > postingInterval) {
    client.println("FCKOFF");
    resetConnection();
  }
}

void connectToServer() {
  if (client.connect(server, 9090)) {
    Serial.println("Connected");
    lastConnectionTime = millis();
    
    // Greet the Server Politely
    client.println("Howdy!");
  }
  else {
    Serial.println("Connection failed");
  }
}

void resetConnection()
{
  Serial.println();
  client.stop();
  connectToServer();
}

void printWifiStatus()
{
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

int hexCharToInt(char c) {
    if (c >= '0' && c <= '9') {
        return c - '0';
    } else if (c >= 'A' && c <= 'F') {
        return c - 'A' + 10;
    } else if (c >= 'a' && c <= 'f') {
        return c - 'a' + 10;
    }
    return 0;
}

int* hexToRGB(const String hexString) {
    int color[] = {0, 0, 0};
    if (hexString[0] != '\0' && hexString[1] != '\0') {
      color[0] = (hexCharToInt(hexString[0]) << 4) | hexCharToInt(hexString[1]);
    }
    if (hexString[2] != '\0' && hexString[3] != '\0') {
      color[1] = (hexCharToInt(hexString[2]) << 4) | hexCharToInt(hexString[3]);
    }
    if (hexString[4] != '\0' && hexString[5] != '\0') {
      color[2] = (hexCharToInt(hexString[4]) << 4) | hexCharToInt(hexString[5]);
    }
    return color;
}
