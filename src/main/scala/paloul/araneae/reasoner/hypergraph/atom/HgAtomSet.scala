package paloul.araneae.reasoner.hypergraph.atom

import paloul.araneae.reasoner.hypergraph.HgHandle

import scala.collection.mutable.HashSet

/**
 * Represents temporary construction of a set of atoms. A handle for each atom is stored in the set.
 */
class HgAtomSet extends Set[HgHandle] {

  /** HgAtomSet is backed by a HashSet */
  private val hashSet: HashSet[HgHandle] = new HashSet[HgHandle]()

  /**
   * Auxiliary constructor to aid with adding atoms to set
   * @param atomHandles Variable argument of HgHandles of existing atom
   */
  def this(atomHandles: HgHandle*) = {
    this

    // Get all the handles and add to our hash set
    for(atomHandle <- atomHandles) {
      hashSet.add(atomHandle)
    }
  }

  override def incl(elem: HgHandle): Set[HgHandle] = ???

  override def excl(elem: HgHandle): Set[HgHandle] = ???

  override def contains(elem: HgHandle): Boolean = ???

  override def iterator: Iterator[HgHandle] = ???
}
