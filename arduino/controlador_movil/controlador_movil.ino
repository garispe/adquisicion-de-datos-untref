#include <SoftwareSerial.h>

SoftwareSerial BT(10, 11); // Rx Arduino <-> Tx HC-05, Tx Arduino <-> Rx HC-05
int configuracionBT = 9;

void setup(){

  pinMode(13, OUTPUT); // Se utiliza para pruebas.
 
  Serial.begin(9600);
  Serial.println("Ingrese comando AT:");

  BT.begin(9600);
}

void loop() {

  if (BT.available()) {

    procesarIndicacion();
  }
}

void procesarIndicacion(){

    String indicacion = BT.readString();
    Serial.println(indicacion);
}
