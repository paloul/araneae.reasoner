package paloul.araneae.reasoner.hypergraph.handle

class HgHandleInt extends HgHandle {
  /** Number of bytes in the <code>Array[Byte]</code> representation */
  override val size: Int = 4

  /** Get the Array[Byte] representation of this handle */
  override def toByteArray: Array[Byte] = ???
}
