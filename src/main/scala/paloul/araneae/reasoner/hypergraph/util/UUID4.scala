package paloul.araneae.reasoner.hypergraph.util

object UUID4 {

  /**
   * Instantiate a new UUID4 instance. Version-4 UUIDs are randomly generated and entirely unique.
   * Version-4 based UUID which are randomly generated are useful when a unique name is not
   * provided for the identification of a certain atom. For example, a randomly unique UUID
   * can be used to represent an abstract link that has no identification other than the
   * atoms it connects.
   * @return
   */
  def apply(): UUID4 = {
    new UUID4(java.util.UUID.randomUUID())
  }

  /**
   * Generate UUID instance from a string representation of UUID
   * @param uuidString A string that specifies a UUID
   * @return UUID with the specified value defined by uuid string
   */
  def fromString(uuidString: String): UUID = {
    // Use static method to convert String UUID to java.util.UUID instance and create UUID4 wrapper
    new UUID4(java.util.UUID.fromString(uuidString))
  }

}

/**
 * A wrapper around java.util.UUID. Can be instantiated only by its companion object.
 * Version-4 UUIDs are randomly generated uuids
 *
 * @param javaUuid The java.util.uuid instance this class instance will be wrapping
 */
class UUID4 private (javaUuid: java.util.UUID) extends UUID {

  val uuid: java.util.UUID = javaUuid

  val bytes: Array[Byte] = bytesFromUUID(uuid.toString)

}
