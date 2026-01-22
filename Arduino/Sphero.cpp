#include "Sphero.h"

const int buzzerPin = 8;
const int trigPin = 9;
const int echoPin = 10;

void Sphero::initialize() {
  pinMode(buzzerPin, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  rvr.configUART(&Serial);
  rvr.resetYaw();

  setColor(255, 0, 0, 255, 0, 0);
}

void Sphero::moveForward(uint8_t speed, uint8_t length) {
  rvr.rawMotors(RawMotorModes::forward, speed, RawMotorModes::forward, speed);
  delay(1000 * length);
  rvr.rawMotors(RawMotorModes::forward, 0, RawMotorModes::forward, 0);
}

void Sphero::rotateRight() {
  rvr.resetYaw();
  rvr.getDriveControl().rollStart(93, 64);
  delay(950);
  rvr.getDriveControl().rollStop(93);
  rvr.resetYaw();
  delay(150);
}

void Sphero::rotateLeft() {
  rvr.resetYaw();
  rvr.getDriveControl().rollStart(267, 64);
  delay(950);
  rvr.getDriveControl().rollStop(267);
  rvr.resetYaw();
  delay(150);
}

void Sphero::setColor(uint8_t redLeft, uint8_t greenLeft, uint8_t blueLeft, uint8_t redRight, uint8_t greenRight, uint8_t blueRight) {
  uint8_t leds[6] = {redLeft, greenLeft, blueLeft, redRight, greenRight, blueRight};
  rvr.setAllLeds(63, leds, 6);
}

int Sphero::getSensorData() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  return (int)((pulseIn(echoPin, HIGH) * 0.0343) / 2);
}

void Sphero::playTone(int frequency, int duration) {
  tone(buzzerPin, frequency, duration * 1.25);
  delay(duration / 2);
}

void Sphero::stopTone() {
  noTone(buzzerPin);
}