java-signature
=========
Create a Signature for REST API Request.

Version
-------

* Java jdk_1.7.0_40
* Maven 3.1.1
* Eclipse Kepler 4.3

Abstract
--------

* This source code is Java 
* Input parameters for creating a Signature and you can get easily Signature string 
* It is not a jar file, so you shuld copy this source code

How to Use
----------

### How to install

* Import eclipse project
  * http://kakakikikeke.blogspot.jp/2012/05/githubpubliceclipse.html
* Add to the configuration of build path
* As options, please install the maven plugin for eclipse

### To create a signature for v1

* To Execute src/main/java/com/kakakikikeke/sample/SignatureCreator.java

### To create a signature for v2

* To Execute src/main/java/com/kakakikikeke/sample/Signature2Creator.java

### To execute aws base rest API using signature v2

* `mvn clean package`
* `java -jar target/CallAws-jar-with-dependencies.jar -a ListQueues -e mq.jp-east-1.api.cloud.nifty.com` 

#### jar options

* -a ･･･ ActionName
* -e ･･･ Endpoint
* -b ･･･ RequestBody
* -u ･･･ to use http protocol instead of https

Configuration
-------------

* When you create signature v1, please input your key and action name and timestamp
* And after run the `SignatureCreator.java`

```java
public static void main(String[] args) {
  String key = "Your Secret AccessKey";
  String action = "API name";
  String timestamp = getFormattedDateForSignature();
  try {
    System.out.println("Timestamp:\t" + URLEncoder.encode(timestamp, "UTF-8"));
    String signature = new SignatureCreator().createSignature(action + timestamp, key);
    System.out.println("Signature:\t" + signature);
  } catch (SignatureException e) {
    e.printStackTrace();
  } catch (UnsupportedEncodingException e) {
    e.printStackTrace();
  }
}
```

* When you create signature v2, please input your access key, secret access key, action name and endpoint
* And after run the `SignatureCreator.java`.

```java
private String endpoint = "mq.jp-east-1.api.cloud.nifty.com";
private String accessKeyId = "Your AccessKey";
private String secretKey = "Your Secret AccessKey";
private String action = "API name";
```

* When you execute jar file
  * Please input your access key and secret key in src/main/resources/com/kakakikikeke/sample/utils/key.properties

Tips
----