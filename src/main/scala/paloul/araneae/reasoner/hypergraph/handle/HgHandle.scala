package paloul.araneae.reasoner.hypergraph.handle

/**
 * Represents a reference to a hypergraph atom. A handle holds system-level identity of an atom.
 * A handle is only the unique identifier to an atom stored in memory, on disk, or wherever.
 * The handle to an atom does not store any information about the atom, it is merely a pointer.
 */
trait HgHandle {

  /** Number of bytes in the <code>Array[Byte]</code> representation */
  val size: Int

  /** Get the Array[Byte] representation of this handle */
  def toByteArray: Array[Byte]

}
