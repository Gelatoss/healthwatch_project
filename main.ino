#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include "BLE2902.h"
#include <WiFi.h>
#include <HTTPClient.h>
#include <sstream>
#include <vector>
#include <ArduinoJson.h>
#include "MAX30105.h"
#include "heartRate.h"
#include <Adafruit_MPU6050.h>
#include "Audio.h" // Include the Audio library

// Definitions and Constants
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define SDA_PIN 5
#define SCL_PIN 18
#define BUTTON_PIN 4
#define DISPLAY_DURATION 10000 // 10 seconds
#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define RATE_SIZE 4
#define STEP_THRESHOLD 2.0
#define MIN_STEP_ACCELERATION 1.0
#define STEP_INTERVAL 300
#define SEND_INTERVAL 60000
#define HEART_RATE_INTERVAL 1000
#define MAX_RETRY_ATTEMPTS 3
#define RETRY_INTERVAL 5000
#define I2S_DOUT 26
#define I2S_BCLK 27
#define I2S_LRC 13

// Variables
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);
Adafruit_MPU6050 mpu;
MAX30105 particleSensor;
BLECharacteristic *pCharacteristic = NULL;
BLEServer *pServer = NULL;
BLE2902 *pDescriptor = NULL;
char ssid[32] = {0};
char password[64] = {0};
char email[64] = {0};
char userPassword[64] = {0};
String authToken = "";
unsigned long displayTimer = 0;
unsigned long lastStepTime = 0;
unsigned long lastSendTime = 0;
unsigned long lastHeartRateTime = 0;
float filteredAccel = 0;
int steps = 0;
int averageBPM = 0;
long totalSteps = 0;
byte rates[RATE_SIZE];
byte rateSpot = 0;
long lastBeat = 0;
float beatsPerMinute;
int beatAvg;
bool deviceConnected = false;
bool oldDeviceConnected = false;
bool ssidReceived = false;
bool disconnectBLE = false;
bool wifiConnected = false;
int retryAttempt = 0;

// Audio Object
Audio audio;
bool isPlaying = false; // Track audio state

// Endpoint URLs
const char* authenticateEndpoint = "http://172.20.10.2:8091/api/v1/auth/authenticate";
const char* dataManagementEndpoint = "http://172.20.10.2:8091/api/v1/data_management";
const char* dailyReportEndpoint = "http://172.20.10.2:8091/api/v1/daily_report";

// Function Declarations
void connectToWiFi();
void authenticateUser();
void sendRecordedData();
void getSteps();
void updateDisplay(int _bpm, int _steps);
void readHeartRate();
void receiveDaily();
void initializeBluetooth();
bool initializeDisplay();
bool initializeSensors();
void handleBleConnection();
void playAudio(); // Function to handle audio playback

// BLE Callbacks
class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
      pDescriptor->setNotifications(true);
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

class MyCharacteristicCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
        std::string value = pCharacteristic->getValue();
        if (!value.empty()) {
            Serial.println(String(value.c_str()));

            // Split the string by comma delimiter
            std::vector<std::string> tokens;
            std::string token;
            std::istringstream tokenStream(value);
            while (std::getline(tokenStream, token, ',')) {
                tokens.push_back(token);
            }

            if (tokens.size() == 4) {
                strncpy(ssid, tokens[0].c_str(), sizeof(ssid) - 1);
                strncpy(password, tokens[1].c_str(), sizeof(password) - 1);
                strncpy(email, tokens[2].c_str(), sizeof(email) - 1);
                strncpy(userPassword, tokens[3].c_str(), sizeof(userPassword) - 1);
                ssidReceived = true;
                disconnectBLE = true;
            } else {
                Serial.println("Error: Received value does not contain four comma-separated values");
            }
        }
    }
};

void setup() {
  Serial.begin(115200);
  Wire.begin(SDA_PIN, SCL_PIN);
  //Wire.begin();

  pinMode(BUTTON_PIN, INPUT_PULLUP);

  if (!initializeDisplay()) return;
  if (!initializeSensors()) return;
  initializeBluetooth();

  // Initialize Audio
  audio.setPinout(I2S_BCLK, I2S_LRC, I2S_DOUT);
  audio.setVolume(10); // Set initial volume

  delay(2000);
}

bool initializeDisplay() {
    if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
        Serial.println(F("SSD1306 allocation failed"));
        return false;
    }
    display.display();
    display.setTextSize(1);
    display.setTextColor(WHITE);
    return true;
}

bool initializeSensors() {
    if (!mpu.begin()) {
        display.println("No MPU6050 detected");
        display.display();
        while (1);
    }

    if (!particleSensor.begin(Wire, I2C_SPEED_FAST)) {
        Serial.println(F("MAX30105 was not found. Please check wiring/power."));
        while (1);
    }
    particleSensor.setup();
    particleSensor.setPulseAmplitudeRed(0x0A);
    particleSensor.setPulseAmplitudeGreen(0);
    return true;
}

void initializeBluetooth() {
    BLEDevice::init("PrimePulse");
    pServer = BLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());
    BLEService *pService = pServer->createService(SERVICE_UUID);
    pCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_UUID,
        BLECharacteristic::PROPERTY_NOTIFY | BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE
    );
    pCharacteristic->setCallbacks(new MyCharacteristicCallbacks());
    pDescriptor = new BLE2902();
    pCharacteristic->addDescriptor(pDescriptor);
    pService->start();
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    pAdvertising->setScanResponse(true);
    pAdvertising->setMinPreferred(0x12);
    BLEDevice::startAdvertising();
    Serial.println(F("Characteristic defined! Now you can read it on your phone!"));
}

void loop() {
  handleBleConnection();

  if (ssidReceived) {
    connectToWiFi();
    ssidReceived = false;
  }

  if (wifiConnected) {
    if (authToken.isEmpty() && retryAttempt < MAX_RETRY_ATTEMPTS) {
      authenticateUser();
    }

    readHeartRate();
    getSteps();

    unsigned long currentTime = millis();
    if (currentTime - lastSendTime >= SEND_INTERVAL) {
      sendRecordedData();
      lastSendTime = currentTime;
      steps = 0;
    }

    if (digitalRead(BUTTON_PIN) == LOW) {
      receiveDaily();
      playAudio(); // Play audio when the button is pressed
      updateDisplay(averageBPM, totalSteps);
      displayTimer = millis();
    }

    if (millis() - displayTimer >= DISPLAY_DURATION) {
      display.clearDisplay();
      display.display();
    } else {
      updateDisplay(averageBPM, totalSteps);
    }
  }

  audio.loop(); // Call audio.loop() continuously
}

void handleBleConnection() {
    // Handle Bluetooth connection
    if (deviceConnected && !oldDeviceConnected) {
        delay(1000);
        oldDeviceConnected = deviceConnected;
    } else if (!deviceConnected && oldDeviceConnected) {
        pServer->startAdvertising();
        Serial.println("Advertising started");
        oldDeviceConnected = deviceConnected;
    }
    // Disconnect BLE if requested
    if (disconnectBLE) {
        BLEDevice::stopAdvertising();
        delay(1000);
        pServer->removeService(pServer->getServiceByUUID(SERVICE_UUID));
        BLEDevice::deinit();
        delay(1000);
        disconnectBLE = false;
    }
}

void readHeartRate() {
  long irValue = particleSensor.getIR();
  if (irValue < 50000) {
    beatsPerMinute = 0;
    beatAvg = 0;
  } else if (checkForBeat(irValue)) {
    long delta = millis() - lastBeat;
    lastBeat = millis();
    beatsPerMinute = 60 / (delta / 1000.0);
    if (beatsPerMinute < 255 && beatsPerMinute > 20) {
      rates[rateSpot++] = (byte)beatsPerMinute;
      rateSpot %= RATE_SIZE;
      beatAvg = 0;
      for (byte x = 0; x < RATE_SIZE; x++) {
        beatAvg += rates[x];
      }
      beatAvg /= RATE_SIZE;
    }
  }
}

void getSteps() {
    sensors_event_t a, g, temp;
    mpu.getEvent(&a, &g, &temp);
    float xcur = a.acceleration.x;
    float ycur = a.acceleration.y;
    float zcur = a.acceleration.z;
    float currentAccel = sqrt(xcur * xcur + ycur * ycur + zcur * zcur);
    filteredAccel = 0.1 * currentAccel + 0.9 * filteredAccel;

    if ((millis() - lastStepTime > STEP_INTERVAL) && (abs(currentAccel - filteredAccel) > STEP_THRESHOLD) && (currentAccel > MIN_STEP_ACCELERATION)) {
      steps++;
      lastStepTime = millis();
    }
}

void updateDisplay(int _bpm, int _steps) {
  display.clearDisplay();
  display.setCursor(0, 0);
  display.setTextSize(1);

  if (wifiConnected) {
    display.println(F("PrimePulse Monitor"));
    display.println(F("-------------------"));
    display.setCursor(0, 16);
    display.print(F("Heart Rate: "));
    display.print(_bpm);
    display.println(F(" BPM"));
    display.setCursor(0, 32);
    display.print(F("Steps Today: "));
    display.print(_steps);
  } else {
    display.println(F("No WiFi connection"));
  }
  
  display.display();
}

void connectToWiFi() {
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(F("Connected to WiFi"));
  wifiConnected = true;
}

void authenticateUser() {
  retryAttempt++;
  Serial.print("Attempt #");
  Serial.println(retryAttempt);
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(authenticateEndpoint);
    http.addHeader("Content-Type", "application/json");

    String payload = "{\"email\":\"" + String(email) + "\",\"password\":\"" + String(userPassword) + "\"}";
    int httpResponseCode = http.POST(payload);
    if (httpResponseCode == HTTP_CODE_OK) {
      String response = http.getString();
      DynamicJsonDocument doc(1024);
      deserializeJson(doc, response);
      authToken = doc["token"].as<String>();
      Serial.println(F("Authentication successful"));
    } else {
      Serial.print(F("Authentication failed, code: "));
      Serial.println(httpResponseCode);
      delay(RETRY_INTERVAL);
    }
    http.end();
  }
}

void sendRecordedData() {
  if (WiFi.status() == WL_CONNECTED && !authToken.isEmpty()) {
    HTTPClient http;
    http.begin(dataManagementEndpoint);
    http.addHeader("Authorization", "Bearer " + authToken);
    http.addHeader("Content-Type", "application/json");

    Serial.println(beatAvg);
    Serial.println(steps);

    String payload = "{\"heartRate\":" + String(beatAvg) + ",\"steps\":" + String(steps) + "}";
    int httpResponseCode = http.POST(payload);

    if (httpResponseCode == HTTP_CODE_OK) {
      Serial.println(F("Data sent successfully"));
    } else {
      Serial.print(F("Failed to send data, code: "));
      Serial.println(httpResponseCode);
    }

    http.end();
  }
}

void receiveDaily() {
  if (WiFi.status() == WL_CONNECTED && !authToken.isEmpty()) {
    HTTPClient http;
    http.begin(dailyReportEndpoint);
    http.addHeader("Authorization", "Bearer " + authToken);
    http.addHeader("Content-Type", "application/json");
    int httpResponseCode = http.GET();

    if (httpResponseCode == HTTP_CODE_OK) {
      String response = http.getString();

      DynamicJsonDocument doc(1024);
      deserializeJson(doc, response);

      averageBPM = doc["averageBPM"].as<int>();
      totalSteps = doc["totalSteps"].as<long>();
    } else {
      Serial.print(F("Failed to retrieve data, code: "));
      Serial.println(httpResponseCode);
    }

    http.end();
  }
}

void playAudio() {
  if (!isPlaying) {
    audio.connecttohost("https://codeskulptor-demos.commondatastorage.googleapis.com/pang/pop.mp3");
    isPlaying = true;
  } else {
    audio.stopSong();
    isPlaying = false;
  }
}
