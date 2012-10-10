signature
=========
Create a Signature for REST API Request.

Abstract
--------
This source code is Java.  
Input parameters for creating a Signature and you can get easily Signature string. 
It is not a jar file, so you shuld copy this source code.

### How to Use
For Exsample, Please input your key, action and timestamp, after run the program.
```java
public static void main(String[] args) {
  String key = "Your Secret AccessKey";
  String action = "API Name";
  String timestamp = "SSSSS";
  String data = action + timestamp;
  try {
    SignatureCreator.calculateRFC2104HMAC(data, key);
  } catch (SignatureException e) {
    e.printStackTrace();
  }
}
```