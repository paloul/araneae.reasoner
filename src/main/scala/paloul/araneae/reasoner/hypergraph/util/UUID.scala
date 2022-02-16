package paloul.araneae.reasoner.hypergraph.util

/**
 * Base Trait for all Araneae specific UUIDs.
 * All specialized Levels of UUID implementations
 * should extend from this trait
 */
trait UUID {

  protected val uuid: java.util.UUID

  def getLeastSignificantBits: Long = uuid.getLeastSignificantBits

  def getMostSignificantBits: Long = uuid.getMostSignificantBits

  override def toString: String = uuid.toString

  override def hashCode(): Int = uuid.hashCode

  override def equals(obj: Any): Boolean = uuid.equals(obj)

}
