package paloul.araneae.reasoner.hypergraph.atom.link.directed

import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Companion object to <code>HgBaseLink</code>
 */
object HgBaseLink {

  /**
   * Instantiate and return a HgBaseLink instance with empty HgAtomSets
   *
   * @return
   */
  def apply(): HgBaseLink = {
    new HgBaseLink(HgAtomSet(), HgAtomSet())
  }

  /**
   * Instantiate and return a HgBaseLink instance with HgAtomSet filled
   * with the atom handles given
   *
   * @param sourceHandles Source set of atoms pointed from the link
   * @param targetHandles Target set of atoms pointed to by link
   * @return
   */
  def apply(sourceHandles: List[HgHandle], targetHandles: List[HgHandle]): HgBaseLink = {
    new HgBaseLink(HgAtomSet(sourceHandles), HgAtomSet(targetHandles))
  }
}

/**
 * A simple/plain implementation of directed <code>HgLink</code> hyperedge.
 *
 * @param source Source set of atoms pointed from the link
 * @param target Target set of atoms pointed to by link
 */
class HgBaseLink protected(source: HgAtomSet, target: HgAtomSet) extends HgLink {

  /** Represents the source (head) of this hyperedge. */
  override protected val sourceAtomSet: HgAtomSet = source
  /** Represents the target set of this link. */
  override protected val targetAtomSet: HgAtomSet = target

  /**
   * Number of targets defined in this link
   *
   * @return Int value greater than 0
   */
  override def arity: Int = ???

  /**
   * Check if the given <code>HgHandle</code> is referenced in this link
   *
   * @param hgHandle A handle to to check is referenced in this link
   * @return True if the hgHandle is a target of this link, false otherwise
   */
  override def hasTarget(hgHandle: HgHandle): Boolean = ???

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
   * Remove the target atom by its <code>HgHandle</code>
   *
   * @param hgHandle The handle to the target atom to remove
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  override def removeTarget(hgHandle: HgHandle): HgHandle = ???

  /**
   * Remove a target atom at index i.
   *
   * @param i The index of atom to remove. Range must be between 0 to arity-1
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  override def removeTargetAt(i: Int): HgHandle = ???

  /**
   * Number of sources defined in this link
   *
   * @return Int value greater than 0
   */
  override def sourceArity: Int = ???

  /**
   * Check if the given <code>HgHandle</code> is referenced as a source in this link
   *
   * @param hgHandle A handle to check is referenced in this link
   * @return True if the hgHandle is a source of this link, false otherwise
   */
  override def hasSource(hgHandle: HgHandle): Boolean = ???

  /**
   * Return the ith source in this link
   *
   * @param i The index of desired source. Range must be between 0 to sourceArity-1
   * @return A handle to the source atom
   */
  override def sourceAt(i: Int): HgHandle = ???

  /**
   * Add a new source atom to the link.
   *
   * @param hgHandle A handle to the source atom that will be added
   * @return True if added, false otherwise
   */
  override def addNewSource(hgHandle: HgHandle): Boolean = ???

  /**
   * Remove the source atom by its <code>HgHandle</code>
   *
   * @param hgHandle The handle to the source atom to remove
   * @return A handle to the source atom that was removed. Null if nothing was removed.
   */
  override def removeSource(hgHandle: HgHandle): HgHandle = ???

  /**
   * Remove a source atom at index i.
   *
   * @param i The index of atom to source. Range must be between 0 to sourceArity-1
   * @return A handle to the source atom that was removed. Null if nothing was removed.
   */
  override def removeSourceAt(i: Int): HgHandle = ???
}
