#include "WiFiEsp.h"
#include "Sphero.h"

#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(2, 3); // RX, TX
#endif

char ssid[] = "IT-Shop";
char pass[] = "B0n_J0v!";
int status = WL_IDLE_STATUS;
char server[] = "192.168.0.90";
unsigned long lastConnectionTime = 0;
const unsigned long postingInterval = 60000L; // 1 Min

WiFiEspClient client;
Sphero sphero;

enum Direction { FORWARD, LEFT, BACKWARD, RIGHT, STOPPED };
Direction driving = Direction::STOPPED;
int lastHeading = 0;
uint8_t speed = 64;

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

    if(c.substring(0, 5) == "R_000") {
      driving = Direction::FORWARD;
      speed = c.substring(5, 8).toInt();
    } else if(c.substring(0, 5) == "R_001") {
      driving = Direction::LEFT;
      speed = c.substring(5, 8).toInt();

      lastHeading -= 20;
      if(lastHeading < 0) {
        lastHeading = 340;
      }
    } else if(c.substring(0, 5) == "R_002") {
      driving = Direction::BACKWARD;
      speed = c.substring(5, 8).toInt();
    } else if(c.substring(0, 5) == "R_003") {
      driving = Direction::RIGHT;
      speed = c.substring(5, 8).toInt();
      
      lastHeading += 20;
      if(lastHeading > 360) {
        lastHeading = 20;
      }
    } else if(c.substring(0, 5) == "R_004") {
      driving = Direction::STOPPED;
    } else if(c.substring(0, 5) == "R_006") {
      sphero.partyMode();
    } else {
      client.println("Beep Boop Does not Compoop");
    }
    
    lastConnectionTime = millis();
    c = "";
  }

  sphero.drive(speed, lastHeading);
  if (driving == Direction::STOPPED) {
    sphero.stop(lastHeading);
  } else {
    sphero.drive(speed, lastHeading);
  }
  
  if (millis() - lastConnectionTime > postingInterval) {
    sphero.stop(lastHeading);
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