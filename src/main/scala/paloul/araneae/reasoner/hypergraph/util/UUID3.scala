package paloul.araneae.reasoner.hypergraph.util

import paloul.araneae.reasoner.hypergraph.util.UUID3.bytesFromUUID

/**
 * Companion object to UUID3. Provides the only means to create UUID3 instances.
 * The class instance is light wrapper around java.util.UUID.
 *
 * Version (Level) 3 UUIDs are generated by hashing a name.
 * The hash algorithm used is MD5. Generating a Version 3 UUID with the same name
 * will always generate the same UUID.
 */
object UUID3 {

  private final val ARANEAE_NAMESPACE_BYTES: Array[Byte] = "paloul.araneae.namespace".getBytes("UTF-8")

  /**
   * Instantiate a new UUID3 from given arbitrary string. Underlying UUID3 is version Level 5.
   * Level 3 UUIDs generate the same uuid hex value given the same string name.
   *
   * @param name Any arbitrary string value that will be hashed to generate uuid
   * @return A UUID representing a hash of the given string name and araneae namespace
   */
  def apply(name: String): UUID = {
    val nameBytes = name.getBytes("UTF-8")
    val joinedNameBytes = joinBytes(ARANEAE_NAMESPACE_BYTES, nameBytes)

    new UUID3(java.util.UUID.nameUUIDFromBytes(joinedNameBytes))
  }

  /**
   * Generate UUID instance from a string representation of UUID
   * @param uuidString A string that specifies a UUID
   * @return UUID with the specified value defined by uuid string
   */
  def fromString(uuidString: String): UUID = {
    // Use static method to convert String UUID to java.util.UUID instance and create UUID3 wrapper
    new UUID3(java.util.UUID.fromString(uuidString))
  }

  /**
   * Combine two byte arrays into one
   * @param byteArray1 Byte Array One
   * @param byteArray2 Byte Array Two
   * @return New Array of Bytes joining the given byte arrays
   */
  private def joinBytes(byteArray1: Array[Byte], byteArray2: Array[Byte]): Array[Byte] = {
    val finalLength = byteArray1.length + byteArray2.length
    val result = new Array[Byte](finalLength)

    for (i <- byteArray1.indices) {
      result(i) = byteArray1(i)
    }
    for (i <- byteArray2.indices) {
      result(byteArray1.length + i) = byteArray2(i)
    }

    result
  }

}

/**
 * A wrapper around java.util.UUID. Can be instantiated only by its companion object.
 * Version-3 and version-5 UUIDs are generated by hashing a name.
 * Version 3 uses MD5 as the hashing algorithm, and version 5 uses SHA-1
 *
 * @param javaUuid The java.util.uuid instance this class instance will be wrapping
 */
class UUID3 private (javaUuid: java.util.UUID) extends UUID {

  val uuid: java.util.UUID = javaUuid

  val bytes: Array[Byte] = bytesFromUUID(uuid.toString)

}
