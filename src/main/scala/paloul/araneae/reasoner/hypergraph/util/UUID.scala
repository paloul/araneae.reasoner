package paloul.araneae.reasoner.hypergraph.util

/**
 * Base Trait for all Araneae specific UUIDs.
 * All specialized Levels of UUID implementations
 * should extend from this trait
 */
trait UUID {

  protected val uuid: java.util.UUID

  protected val bytes: Array[Byte]

  def getBytes: Array[Byte] = bytes

  def getLeastSignificantBits: Long = uuid.getLeastSignificantBits

  def getMostSignificantBits: Long = uuid.getMostSignificantBits

  override def toString: String = uuid.toString

  override def hashCode: Int = uuid.hashCode

  override def equals(obj: Any): Boolean = uuid.equals(obj)

  /**
   * Convert the given hex string representation of uuid to an array of bytes
   * @param uuidHexString The uuid in string form
   * @return Byte array representing the given uuid
   */
  protected def bytesFromUUID(uuidHexString: String): Array[Byte] = {
    // Remove the dashes (-) from the given uuid string
    val normalizedUUIDHexString = uuidHexString.replace("-", "")

    // There are 4 dashes (-) in any uuid. Total 36 chars long.
    // Remove 4 dashes and you have 32 chars.
    assert(normalizedUUIDHexString.length == 32)

    val bytes = new Array[Byte](16)
    for (i <- 0 until 16) {
      // Moving up by 2's because 2 hex characters are captured from string
      val b = hexToByte(normalizedUUIDHexString.substring(i * 2, i * 2 + 2))
      bytes(i) = b
    }

    bytes
  }

  /**
   * Convert the given string that represents a single hex value to its byte representation
   * @param hexString The hex value in string form
   * @return Numeric value in bytes of the hex string
   */
  private def hexToByte(hexString: String): Byte = {
    val firstDigit = Character.digit(hexString.charAt(0), 16)
    val secondDigit = Character.digit(hexString.charAt(1), 16)

    ((firstDigit << 4) + secondDigit).toByte
  }

}
