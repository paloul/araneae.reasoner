package paloul.araneae.reasoner.hypergraph.handle

/**
 * Represents a reference to a hypergraph atom. A handle holds system-level identity of an atom.
 */
trait HgHandle {

  /** Number of bytes in the <code>Array[Byte]</code> representation */
  val size: Int

  /** Get the Array[Byte] representation of this handle */
  def toByteArray: Array[Byte]

}
