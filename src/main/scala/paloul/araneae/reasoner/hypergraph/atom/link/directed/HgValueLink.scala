package paloul.araneae.reasoner.hypergraph.atom.link.directed

import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Companion object to <code>HgValueLink</code>
 */
object HgValueLink {

  /**
   * Instantiate and return a directed HgValueLink instance with empty HgAtomSet
   *
   * @param linkValue The underlying value of the arbitrary payload on the link
   * @tparam A The arbitrary object type of the payload carried by this link
   * @return
   */
  def apply[A](linkValue: A): HgValueLink[A] = {
    new HgValueLink(linkValue, HgAtomSet(), HgAtomSet())
  }

  /**
   * Instantiate and return a directed HgValueLink instance with HgAtomSet filled
   * with the atom handles given
   *
   * @param linkValue The underlying value of the arbitrary payload on the link
   * @param sourceHandles Source set of atoms pointed from the link
   * @param targetHandles Target set of atoms pointed to by link
   * @tparam A The arbitrary object type of the payload carried by this link
   * @return
   */
  def apply[A](
                linkValue: A,
                sourceHandles: List[HgHandle],
                targetHandles: List[HgHandle]): HgValueLink[A] = {
    new HgValueLink(linkValue, HgAtomSet(sourceHandles), HgAtomSet(targetHandles))
  }
}

/**
 * This is a directed <code>HgLink</code> that can hold an arbitrary object type
 * as its payload. The object can be any type and hence why it is a generic type.
 * For instance, the linkValue can be of type String and represent a label on
 * the link. Note that the type of the stored atom will be the <code>A</code>
 * type parameter defined with the generic.
 *
 * @param linkValue The underlying value of the arbitrary payload on the link
 * @param source Source set of atoms pointed from the link
 * @param target Target set of atoms pointed to by link
 * @tparam A The arbitrary object type of the payload carried by this link
 */
class HgValueLink[A] protected (val linkValue: A, source: HgAtomSet, target: HgAtomSet)
  extends HgBaseLink (source, target)  {

  /**
   * Create a string representation of this HgValueLink
   * @return
   */
  override def toString: String = {
    "Link: [" + linkValue.toString + "]" +
      "\n\t:Source[" + sourceAtomSet.mkString(",") + "]" +
      "\n\t:Target[" + targetAtomSet.mkString(",") + "]"
  }

}
