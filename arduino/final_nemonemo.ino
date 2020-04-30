// 블루투스 통신을 하기 위해 SoftwareSerial 라이브러리를 사용함.
#include <SoftwareSerial.h>
  
  SoftwareSerial BTSerial(2, 3); // SoftwareSerial(RX, TX), 통신을 하기 위한 RX,TX 연결 핀번호
  byte buffer[1024];    // 데이터를 수신 받을 자료를 저장할 버퍼
  int bufferPosition;   // 버퍼에 데이터를 저장할 때 기록할 위치

  #include <Adafruit_NeoPixel.h>
//픽셀 사용하기 위해 불러오는 라이브러리


#define PIN 6

Adafruit_NeoPixel strip = Adafruit_NeoPixel(256, PIN, NEO_GRB + NEO_KHZ800);


 //한바이트 데이터를 임시 저장
char cTemp;

//완성된 명령어
String sCommand = "";

String sTemp = "";


void setup(){
  BTSerial.begin(9600); // 블루투스 모듈 초기화, 블루투스 연결
  Serial.begin(9600);   // 시리얼 모니터 초기화, pc와 연결
  bufferPosition = 0;   // 버퍼 위치 초기화

  strip.begin();
  strip.clear();
  strip.show(); // Initialize all pixels to 'off'

}
 
// loop문 안에서 데이터를 받아올 때는 한번에 한글자씩 받아오게 됨.
// 글자를 하나씩 받아와서 출력하고, 현재 bufferPositon에 맞게 데이터를 버퍼에 저장하고 bufferPositon을 1개 늘려줌.
// 이렇게 계속 반복하여 문자열의 끝('\n') 이 나오게 되면 버퍼의 마지막에 ('\0')을 넣고 버퍼에 저장된 문자열을 
// 다시 스마튼폰으로 전송하고 버퍼를 초기화 해준다. 
 
void loop(){
   sCommand = "";
  
   while(BTSerial.available())
   {
     cTemp = BTSerial.read();
     sCommand.concat(cTemp);
   }
   
   //완성된 데이터가 있는지 확인 한다.
   if(sCommand != "" )
   {
    strip.show();
     //완성된 데이터가 있다.
     
     char cTempData[4];
     
     sCommand.substring(0, 3).toCharArray(cTempData, 4);
     int nR = atoi(cTempData);
     
     sCommand.substring(3, 6).toCharArray(cTempData, 4);
     int nG = atoi(cTempData);
     
     sCommand.substring(6, 9).toCharArray(cTempData, 4);
     int nB = atoi(cTempData); //저장된 문자열을 정수로 변환하는 함수 : atoi

     sCommand.substring(9, 11).toCharArray(cTempData, 4);
     int a = atoi(cTempData);


    //전체 다 같은 색으로 할 때,
     if(a==65)
     {
       BTSerial.println(sCommand + ":" + nR + "," + nG + "," + nB ); 

       for(int z=0; z<64; z++)
         strip.setPixelColor(z, nR, nG, nB);
     }
     
     //저장되었던 것을 불러올 때,
     else if(a==66)
     {
      
     }
     //하나 하나씩 픽셀에 그릴 때,
     else
     {
      BTSerial.println(sCommand + ":" + nR + "," + nG + "," + nB ); 

      strip.setPixelColor(a, nR, nG, nB);
     }
    strip.show();
   }
   
   delay(100);
}


