package paloul.araneae.cluster.util.serializers

/**
 * Marker trait for serialization with Jackson JSON.
 * Ordinary text-based JSON Serialization
 * https://doc.akka.io/docs/akka/current/serialization-jackson.html
 *
 * Use for internal messages. External message via gRPC/Kafka use protobuf
 */
trait ProtoSerializable extends akka.actor.NoSerializationVerificationNeeded
