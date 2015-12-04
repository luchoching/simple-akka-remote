# note 

run `TransformationApp`: 

```
[WARN] [12/03/2015 16:07:04.873] [ClusterSystem-akka.remote.default-remote-dispatcher-6] [akka.serialization.Serialization(akka://ClusterSystem)] Using the default Java serializer for class [com.example.transformation.BackendRegistration$] which is not recommended because of performance implications. Use another serializer or disable this warning using the setting 'akka.actor.warn-about-java-serializer-usage'
```

--> use protobuf or kryto 

# note 

```
[info] Running com.example.stats.StatsSampleClient
[ERROR] [12/04/2015 16:19:04.091] [ClusterSystem-akka.actor.default-dispatcher-3] [akka://ClusterSystem/user/client] null
akka.actor.ActorInitializationException: exception during creation
```
