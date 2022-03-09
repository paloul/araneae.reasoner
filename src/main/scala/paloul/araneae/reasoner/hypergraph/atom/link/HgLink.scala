package paloul.araneae.reasoner.hypergraph.atom.link

import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Represents a hypergraph link. A hypergraph link is an atom that holds other atoms in
 * tuple-like relationship.
 */
trait HgLink {

  /**
   * Number of targets defined in this link
   *
   * @return Int value greater than 0
   */
  def arity: Int

  /**
   * Return the ith target in this link
   *
   * @param i The index of desired target. Range must be between 0 to arity-1
   * @return A handle to the target atom
   */
  def targetAt(i: Int): HgHandle

  /**
   * Add a new target atom to the link.
   * @param hgHandle A handle to the target atom that will be added
   * @return True if added, false otherwise
   */
  def addNewTarget(hgHandle: HgHandle): Boolean

  /**
   * Remove a target atom at index i.
   * @param i The index of atom to remove. Range must be between 0 to arity-1
   * @return A handle to the target atom that was removed. Null if nothing was removed.
   */
  def removeTargetAt(i: Int): HgHandle

}
