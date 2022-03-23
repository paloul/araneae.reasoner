package paloul.araneae.reasoner.hypergraph.atom.link.undirected

import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Represents an undirected hypergraph link. A hypergraph link is an atom
 * that holds other atoms in tuple-like relationship as a set.
 */
trait HgLink {

  /** Represents the target set of this link. */
  protected val targetAtomSet: HgAtomSet

  /**
   * Number of targets defined in this link
   *
   * @return Int value greater than 0
   */
  def arity: Int

  /**
   * Check if the given <code>HgHandle</code> is referenced in this link
   *
   * @param hgHandle A handle to to check is referenced in this link
   * @return True if the hgHandle is a target of this link, false otherwise
   */
  def hasTarget(hgHandle: HgHandle): Boolean

  /**
   * Return the ith target in this link
   *
   * @param i The index of desired target. Range must be between 0 to arity-1
   * @return A handle to the target atom
   */
  def targetAt(i: Int): HgHandle

  /**
   * Add a new target atom to the link.
   *
   * @param hgHandle A handle to the target atom that will be added
   * @return True if added, false otherwise
   */
  def addNewTarget(hgHandle: HgHandle): Boolean

  /**
   * Remove the target atom by its <code>HgHandle</code>
   *
   * @param hgHandle The handle to the target atom to remove
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  def removeTarget(hgHandle: HgHandle): HgHandle

  /**
   * Remove a target atom at index i.
   *
   * @param i The index of atom to remove. Range must be between 0 to arity-1
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  def removeTargetAt(i: Int): HgHandle

  /**
   * Create a string representation with items in targetAtomSet
   * @return
   */
  override def toString: String = {
    "[" + targetAtomSet.mkString(",") + "]"
  }

}
