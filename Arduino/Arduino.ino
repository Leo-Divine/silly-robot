#include "WiFiEsp.h"
#include "Sphero.h"

#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(2, 3); // RX, TX
#endif

char ssid[] = "Rossini Family"; // WiFi Name
char pass[] = "Ajgabmas171"; // WiFi Password
int status = WL_IDLE_STATUS;
char server[] = "192.168.68.60"; // IP Address of Computer Running The Application
unsigned long lastConnectionTime = 0;
const unsigned long postingInterval = 15000L; // How Long The Robot Waits to Recieve a Command Before it Reconnects 

static void getColorCallback(ColorDetectionNotifyReturn_t *colorReturn);

uint8_t red = 0;

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
  rvr.poll();
  String c = "";
  while (client.available()) {
    c += static_cast<char>(client.read());
    if(client.peek() != 13) { continue; };

    while(client.available()) {
      client.read();
    }

    Serial.println(c.substring(0, 5));
    if(c.substring(0, 5) == "R_000") {
      sphero.moveForward(c.substring(5, 8).toInt(), c.substring(8, 11).toInt());
    } else if(c.substring(0, 5) == "R_001") {
      //sphero.rotateLeft();
      client.println(red);
    } else if(c.substring(0, 5) == "R_002") {
      sphero.rotateRight();
    } else if(c.substring(0, 5) == "R_003") {
      sphero.setColor(
        c.substring(5, 8).toInt(),
        c.substring(8, 11).toInt(),
        c.substring(11, 14).toInt(),
        c.substring(14, 17).toInt(),
        c.substring(17, 20).toInt(),
        c.substring(20, 23).toInt());
    } else if(c.substring(0, 5) == "R_004") {
      client.println(sphero.getSensorData());
      lastConnectionTime = millis();
      c = "";
      return;
    } else if(c.substring(0, 5) == "R_005") {
      sphero.playTone(c.substring(5, 9).toInt(), c.substring(9, 13).toInt());
    } else if(c.substring(0, 5) == "R_006") {
      sphero.stopTone();
    } else if(c.substring(0, 5) == "R_007") {
      delay(1000 * c.substring(5, 8).toInt());
    } else {
      //client.println("Beep Boop Does not Compoop");
    }
    
    client.println("Next Bitch");
    lastConnectionTime = millis();
    c = "";
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

static void getColorCallback(ColorDetectionNotifyReturn_t *colorReturn) {
  red = colorReturn->red;
}