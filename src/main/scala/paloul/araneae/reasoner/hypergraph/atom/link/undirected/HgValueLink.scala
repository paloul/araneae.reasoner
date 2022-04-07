package paloul.araneae.reasoner.hypergraph.atom.link.undirected

import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

object HgValueLink {

  /**
   * Instantiate and return a HgValueLink instance with empty HgAtomSet
   * @param linkValue The underlying value of the arbitrary payload on the link
   * @tparam A The arbitrary object type of the payload carried by this link
   * @return
   */
  def apply[A](linkValue: A): HgValueLink[A] = {
    new HgValueLink(linkValue, HgAtomSet())
  }

  /**
   * Instantiate and return a HgValueLink instance with HgAtomSet filled
   * with the atom handles given
   * @param linkValue The underlying value of the arbitrary payload on the link
   * @param atomHandles Target set of atoms pointed to by link
   * @tparam A The arbitrary object type of the payload carried by this link
   * @return
   */
  def apply[A](linkValue: A, atomHandles: HgHandle*): HgValueLink[A] = {
    new HgValueLink(linkValue, HgAtomSet(atomHandles))
  }

  /**
   * Instantiate and return a HgValueLink instance with HgAtomSet filled
   * with the atom handles given
   * @param linkValue The underlying value of the arbitrary payload on the link
   * @param atomHandles Target set of atoms pointed to by link
   * @tparam A The arbitrary object type of the payload carried by this link
   * @return
   */
  def apply[A](linkValue: A, atomHandles: List[HgHandle]): HgValueLink[A] = {
    new HgValueLink(linkValue, HgAtomSet(atomHandles))
  }
}

/**
 * This is a <code>HgLink</code> that can hold an arbitrary object type as its
 * payload. The object can be of any type and hence why it is a generic type.
 * For instance, the linkValue can be of type String and represent a label on
 * the link. Note that the type of the stored atom will be the <code>A</code>
 * type parameter defined with the generic.
 *
 * @param linkValue The underlying value of the arbitrary payload on the link
 * @param atomSet Target set of atoms pointed to by link
 * @tparam A The arbitrary object type of the payload carried by this link
 */
class HgValueLink[A] protected (val linkValue: A, atomSet: HgAtomSet)
  extends HgBaseLink (atomSet) {

  /**
   * Create a string representation of this HgValueLink
   * @return
   */
  override def toString: String = {
    "Link" +
      "\n\t:Value[" + linkValue.toString + "]" +
      "\n\t:Targets[" + targetAtomSet.mkString(",") + "]"
  }

}
