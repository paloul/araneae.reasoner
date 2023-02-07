package paloul.araneae.cluster.util.serializers

/**
 * Marker trait for serialization with Protobuf.
 * https://doc.akka.io/docs/akka/current/serialization.html
 *
 */
trait ProtoSerializable extends akka.actor.NoSerializationVerificationNeeded
