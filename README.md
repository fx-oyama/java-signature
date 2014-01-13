java-signature
=========
Create a Signature for REST API Request.

Abstract
--------
This source code is Java.  
Input parameters for creating a Signature and you can get easily Signature string. 
It is not a jar file, so you shuld copy this source code.

How to Use
----------
For Exsample, Please input your key, action and timestamp, after run the program.
```java
public static void main(String[] args) {
  String key = "Your Secret AccessKey";
  String action = "API name";
  String timestamp = getFormattedDateForSignature();
  try {
    String signature = new SignatureCreator().createSignature(action + timestamp, key);
    System.out.println("Signature:\t" + signature);
  } catch (SignatureException e) {
    e.printStackTrace();
  } catch (UnsupportedEncodingException e) {
    e.printStackTrace();
  }
}
```
