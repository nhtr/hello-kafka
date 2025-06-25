+------------------+       Kafka       +-----------------------+
|  Price Feed      |  ------------->   |  Kafka Topics (1/topic|
|  Service         |    (Per symbol)   |  per symbol)          |
+------------------+                   +-----------------------+
|        |       |
v        v       v
+---------------------------------------------+
|        Streaming Service (Multiple Pods)    |
|---------------------------------------------|
| WebSocket Server                            |
|                                             |
| Kafka Consumer (Local, Dynamic Subscription)|
| In-Memory Session Manager                   |
+---------------------------------------------+
|               |              |
v               v              v
+-----------+   +-----------+  +-----------+
|  Client A |   |  Client B |  | Client C  |
+-----------+   +-----------+  +-----------+

↕ Redis Channel ↕ (Cluster-wide symbol events)
