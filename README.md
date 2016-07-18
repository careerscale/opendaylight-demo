# opendaylight-demo
Opendaylight sample
```java
mvn archetype:generate -DarchetypeGroupId=org.opendaylight.controller -DarchetypeArtifactId=opendaylight-startup-archetype -DarchetypeRepository=https://nexus.opendaylight.org/content/repositories/public/ -DarchetypeCatalog=https://nexus.opendaylight.org/content/repositories/public/archetype-catalog.xml -DarchetypeVersion=1.2.0-SNAPSHOT
```
Please check the archetype version to be latest archetypeVersion

refer the pom.xml files in the following locations to validate it.
[https://github.com/opendaylight/controller/tree/master/opendaylight/archetypes/opendaylight-startup]
[https://github.com/opendaylight/odlparent/tree/master/odlparent]

http://www.netconfcentral.org/modulereport/toaster 


Testing the notification
POST [http://localhost:8181/restconf/operations/sal-remote:create-notification-stream]
```javascript
{
"input": {
"notifications": [
"(http://company.com/shop?revision=2016-07-15)restockedInventory"
],
"notification-output-type":"JSON"
}
}
```

now let us subscribe to the stream

GET [http://localhost:8181/restconf/streams/stream/create-notification-stream/shop:restockedInventory]

check the headers in the response

Location ws://loccalhost:8185/create-notification-stream/shop:restockedInventory


now you can use the simple websocket client plugin of chrome to listen to the stream

this is the websocket URL
[ws://loccalhost:8185/create-notification-stream/shop:restockedInventory]


now, using the yang ui you can generate the shop restock notification by invoking the RPC.

