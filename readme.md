# 項目

`hello`: typesafe akka-sample-main-scala

`simple`: typesafe akka-sample-cluster-scala simple 

`transformation`: typesafe akka-sample-cluster-scala Worker Dial-in

`calculator`: typesafe akka-sample-remote-scala remote search (calculator)

# TODO  

## 1

run `TransformationApp`: 

```
[WARN] [12/03/2015 16:07:04.873] [ClusterSystem-akka.remote.default-remote-dispatcher-6] [akka.serialization.Serialization(akka://ClusterSystem)] Using the default Java serializer for class [com.example.transformation.BackendRegistration$] which is not recommended because of performance implications. Use another serializer or disable this warning using the setting 'akka.actor.warn-about-java-serializer-usage'
```

--> use protobuf or kryto 

## 2  

```
[info] Running com.example.stats.StatsSampleClient
[ERROR] [12/04/2015 16:19:04.091] [ClusterSystem-akka.actor.default-dispatcher-3] [akka://ClusterSystem/user/client] null
akka.actor.ActorInitializationException: exception during creation
```
