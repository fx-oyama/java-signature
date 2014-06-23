java-signature
=========
Create a Signature for REST API Request.  
And, call AWS REST API.

Version
-------

* Java jdk_1.7.0_40
* Maven 3.1.1
* Eclipse Kepler 4.3

Abstract
--------

* This source code is Java 
* Input parameters for creating a Signature and you can get easily Signature string 
* You can call aws rest api, if create jar file using maven command

How to Use
----------

### How to install

* [Import eclipse project](http://kakakikikeke.blogspot.jp/2012/12/githubandroideclipse.html)
* Add to the configuration of build path
* As options, please install the maven plugin for eclipse

### To create a signature for v1

* for eclipse
  1. Right click src/main/java/com/kakakikikeke/sample/v1/SignatureCreator.java
  1. Run As -> Java Application

### To create a signature for v2

* for eclipse
  1. Right click src/main/java/com/kakakikikeke/sample/v2/Signature2Creator.java
  1. Run As -> Java Application

### To execute aws base rest API using signature v2

* for maven command
  1. `mvn clean compile package`
  2. `java -jar target/CallAws-jar-with-dependencies.jar -a ListQueues -e mq.jp-east-1.api.cloud.nifty.com` 

#### jar options

* -e ･･･ Endpoint
* -a ･･･ ActionName
* -b ･･･ RequestBody formatted is json ex) '{"QueueNamePrefix":"test"}'
* -u ･･･ to use http protocol instead of https ex) -u
  * This parameter is only specify a parameter name
* -r ･･･ RequestUri ex) -r 'abc12345/testQueue'
  * Did not write the srash in first
* -p ･･･ Proxy ex) -p proxy_host_name:8080
* -v ･･･ API Version
* -nv ･･･ Not contain Version Parameter (-v)
* --accesskey ･･･ Please input your accesskey or write to key.properties
* --secretkey ･･･ Please input your secretkey or write to key.properties
* --only-xml ･･･ Show a result only xml, exclude http request and response code in stdout

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
  * And after run the `SignatureCreator.java`

```java
private String endpoint = "mq.jp-east-1.api.cloud.nifty.com";
private String accessKeyId = "Your AccessKey";
private String secretKey = "Your Secret AccessKey";
private String action = "API name";
```

* When you execute jar file
  * Please input your access key and secret key in src/main/resources/com/kakakikikeke/sample/utils/key.properties
  * Or, to use options「--accesskey」and「--secretkey」
  * And after run the maven command and to execute jar

Tips
----
