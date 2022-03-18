package paloul.araneae.reasoner.hypergraph.atom.util

import paloul.araneae.reasoner.hypergraph.handle.HgHandle

import scala.collection.mutable

/**
 * Companion object to <code>HgAtomSet</code>
 */
object HgAtomSet {

  /** Instantiate and return a new empty HgAtomSet */
  def apply(): HgAtomSet = {
    new HgAtomSet()
  }

  /** Instantiate and return a new HgAtomSet with given HgHandles */
  def apply(atomHandles: Seq[HgHandle]): HgAtomSet = {
    new HgAtomSet(atomHandles)
  }

}

/**
 * Represents temporary construction of a set of atoms. A handle for each atom is stored in the set.
 */
class HgAtomSet protected extends mutable.Set[HgHandle] {

  /** HgAtomSet is backed by a HashSet */
  protected val hashSet: mutable.LinkedHashSet[HgHandle] = new mutable.LinkedHashSet[HgHandle]()

  /**
   * Auxiliary constructor to aid with adding atoms to set
   * @param atomHandles A sequence/list of HgHandles to store this this set
   */
  protected def this(atomHandles: Seq[HgHandle]) = {
    this()

    // Add all the handles to our internal hash set
    hashSet.addAll(atomHandles)
  }

  override def subtractOne(elem: HgHandle): HgAtomSet.this.type = ???

  override def iterator: Iterator[HgHandle] = ???

  override def addOne(elem: HgHandle): HgAtomSet.this.type = ???

  override def contains(elem: HgHandle): Boolean = ???

  override def clear(): Unit = ???
}
