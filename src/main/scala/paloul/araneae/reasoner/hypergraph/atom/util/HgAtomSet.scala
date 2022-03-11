package paloul.araneae.reasoner.hypergraph.atom.util

import paloul.araneae.reasoner.hypergraph.handle.HgHandle

import scala.collection.mutable

/**
 * Represents temporary construction of a set of atoms. A handle for each atom is stored in the set.
 */
class HgAtomSet extends mutable.Set[HgHandle] {

  /** HgAtomSet is backed by a HashSet */
  private val hashSet: mutable.LinkedHashSet[HgHandle] = new mutable.LinkedHashSet[HgHandle]()

  /**
   * Auxiliary constructor to aid with adding atoms to set
   * @param atomHandles Variable argument of HgHandles of existing atom
   */
  def this(atomHandles: HgHandle*) = {
    this

    // Add all the handles to our internal hash set
    hashSet.addAll(atomHandles)
  }

  override def subtractOne(elem: HgHandle): HgAtomSet.this.type = ???

  override def iterator: Iterator[HgHandle] = ???

  override def addOne(elem: HgHandle): HgAtomSet.this.type = ???

  override def contains(elem: HgHandle): Boolean = ???

  override def clear(): Unit = ???
}
