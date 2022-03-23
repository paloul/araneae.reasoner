package paloul.araneae.reasoner.hypergraph.atom.link
import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.{HgHandle, HgHandleUUID}

/**
 * Companion object to <code>HgBaseLink</code>
 */
object HgBaseLink {

  /**
   * Instantiate and return a HgBaseLink instance with empty HgAtomSet
   * @return
   */
  def apply(): HgBaseLink = {
    new HgBaseLink(HgAtomSet())
  }

  /**
   * Instantiate and return a HgBaseLink instance with HgAtomSet filled
   * with the atom handles given
   * @param atomHandles Target set of atoms pointed to by link
   * @return
   */
  def apply(atomHandles: HgHandle*): HgBaseLink = {
    new HgBaseLink(HgAtomSet(atomHandles))
  }

  /**
   * Instantiate and return a HgBaseLink instance with HgAtomSet filled
   * with the atom handles given
   * @param atomHandles Target set of atoms pointed to by link
   * @return
   */
  def apply(atomHandles: List[HgHandle]): HgBaseLink = {
    new HgBaseLink(HgAtomSet(atomHandles))
  }
}

/**
 * A simple/plain implementation of <code>HgLink</code>.
 */
class HgBaseLink protected(atomSet: HgAtomSet) extends HgLink {

  /** Represents the target set of this link. */
  override protected val targetAtomSet: HgAtomSet = atomSet

  /**
   * Number of targets defined in this link
   *
   * @return Int value greater than 0
   */
  override def arity: Int = ???

  /**
   * Return the ith target in this link
   *
   * @param i The index of desired target. Range must be between 0 to arity-1
   * @return A handle to the target atom
   */
  override def targetAt(i: Int): HgHandle = ???

  /**
   * Add a new target atom to the link.
   *
   * @param hgHandle A handle to the target atom that will be added
   * @return True if added, false otherwise
   */
  override def addNewTarget(hgHandle: HgHandle): Boolean = ???

  /**
   * Remove a target atom at index i.
   *
   * @param i The index of atom to remove. Range must be between 0 to arity-1
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  override def removeTargetAt(i: Int): HgHandle = ???

}
