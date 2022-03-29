package paloul.araneae.reasoner.hypergraph.atom.link.directed

import paloul.araneae.reasoner.hypergraph.atom.link.undirected
import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Represents a directed hypergraph link. A directed hypergraph link is an atom itself.
 * It differs from an undirected hypergraph link in that its not just a set of other atoms.
 * A directed hyperedge is an ordered pair of atom subsets, constituting the tail and head
 * of the hyperedge, i.e. E1 = ({V1,V2,V3}, {V4,V5}), meaning V1,V2,V3 all point to both V4,V5.
 */
trait HgLink extends undirected.HgLink {

  /** Represents the source (head) of this hyperedge. */
  protected val sourceAtomSet: HgAtomSet

  /**
   * Number of sources defined in this link
   *
   * @return Int value greater than 0
   */
  def sourceArity: Int

  /**
   * Check if the given <code>HgHandle</code> is referenced as a source in this link
   *
   * @param hgHandle A handle to check is referenced in this link
   * @return True if the hgHandle is a source of this link, false otherwise
   */
  def hasSource(hgHandle: HgHandle): Boolean

  /**
   * Return the ith source in this link
   *
   * @param i The index of desired source. Range must be between 0 to sourceArity-1
   * @return A handle to the source atom
   */
  def sourceAt(i: Int): HgHandle

  /**
   * Add a new source atom to the link.
   *
   * @param hgHandle A handle to the source atom that will be added
   * @return True if added, false otherwise
   */
  def addNewSource(hgHandle: HgHandle): Boolean

  /**
   * Remove the source atom by its <code>HgHandle</code>
   *
   * @param hgHandle The handle to the source atom to remove
   * @return A handle to the source atom that was removed. Null if nothing was removed.
   */
  def removeSource(hgHandle: HgHandle): HgHandle

  /**
   * Remove a source atom at index i.
   *
   * @param i The index of atom to source. Range must be between 0 to sourceArity-1
   * @return A handle to the source atom that was removed. Null if nothing was removed.
   */
  def removeSourceAt(i: Int): HgHandle

  /**
   * Create a string representation with items in targetAtomSet
   * @return
   */
  override def toString: String = {
    "Link:" +
      "\n\t:Source[" + sourceAtomSet.mkString(",") + "]" +
      "\n\t:Target[" + targetAtomSet.mkString(",") + "]"
  }

}
