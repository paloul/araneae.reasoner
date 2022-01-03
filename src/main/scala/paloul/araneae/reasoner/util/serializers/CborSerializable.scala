package paloul.araneae.reasoner.util.serializers

/**
 * Marker trait for serialization with Jackson CBOR.
 * Binary CBOR data format, Concise Binary Object Representation
 * https://github.com/FasterXML/jackson-dataformats-binary/tree/master/cbor
 * https://doc.akka.io/docs/akka/current/serialization-jackson.html
 *
 * Use for internal messages. External message via gRPC/Kafka use protobuf
 */
trait CborSerializable
