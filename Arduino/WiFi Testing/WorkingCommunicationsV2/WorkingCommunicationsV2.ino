#include "WiFiEsp.h"
#include <SpheroRVR.h>

#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(2, 3); // RX, TX
#endif

char ssid[] = "Rossini Family";
char pass[] = "Ajgabmas171";
int status = WL_IDLE_STATUS;
char server[] = "192.168.68.50";
unsigned long lastConnectionTime = 0;
const unsigned long postingInterval = 100000L;

WiFiEspClient client;
extern SpheroRVR rvr;



void setup()
{
  // initialize serial for debugging
  Serial.begin(115200);
  rvr.configUART(&Serial);
  rvr.resetYaw();
  // initialize serial for ESP module
  Serial1.begin(9600);
  // initialize ESP module
  WiFi.init(&Serial1);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }

  Serial.println("You're connected to the network");
  
  printWifiStatus();

  if (client.connect(server, 9090)) {
    Serial.println("Connected");
    
    client.println("Howdy!");

    // note the time that the connection was made
    lastConnectionTime = millis();
  }
  else {
    // if you couldn't make a connection
    Serial.println("Connection failed");
  }
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

    Serial.print(c);
    if(c == "Hello") {
      client.println("Wassup");
      
    } else {
      client.println("Other");
      rotateRight();
    }
    
    
    lastConnectionTime = millis();
    Serial.println();
    c = "";
    delay(2000);
  }

  
  if (millis() - lastConnectionTime > postingInterval) {
    client.println("FCKOFF");
    httpRequest();
  }
  
}

void rotateRight() {
  Serial.println("test");
  rvr.getDriveControl().rollStart(93, 64);
  delay(950);
  rvr.getDriveControl().rollStop(93);
  rvr.resetYaw();
  delay(150);
}

// this method makes a HTTP connection to the server
void httpRequest()
{
  Serial.println();
    
  // close any connection before send a new request
  // this will free the socket on the WiFi shield
  client.stop();

  // if there's a successful connection
  if (client.connect(server, 9090)) {
    Serial.println("Connected");
    
    client.println("Howdy!");

    // note the time that the connection was made
    lastConnectionTime = millis();
  }
  else {
    // if you couldn't make a connection
    Serial.println("Connection failed");
  }
}

void printWifiStatus()
{
  // print the SSID of the network you're attached to
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
