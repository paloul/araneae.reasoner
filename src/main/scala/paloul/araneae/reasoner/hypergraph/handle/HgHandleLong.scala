package paloul.araneae.reasoner.hypergraph.handle

class HgHandleLong extends HgHandle {
  /** Number of bytes in the <code>Array[Byte]</code> representation */
  override val size: Int = 8

  /** Get the Array[Byte] representation of this handle */
  override def toByteArray: Array[Byte] = ???
}
