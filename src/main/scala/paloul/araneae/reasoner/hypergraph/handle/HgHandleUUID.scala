package paloul.araneae.reasoner.hypergraph.handle

class HgHandleUUID extends HgHandle {
  /** Number of bytes in the <code>Array[Byte]</code> representation */
  override val size: Int = 16

  /** Get the Array[Byte] representation of this handle */
  override def toByteArray: Array[Byte] = ???
}
