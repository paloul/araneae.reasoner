package paloul.araneae.reasoner.hypergraph
import paloul.araneae.reasoner.hypergraph.atom.HgIncidenceSet
import paloul.araneae.reasoner.hypergraph.query.HgQueryCondition

/**
 * <p>The main class representing a HyperGraph database.</p>
 *
 * <p>
 * Each datum in a HyperGraph database is called an <code>atom</code>. Atoms are either
 * arbitrary plain objects or instances of {@link HgLink}. Using this class, you may:
 *
 * <ul>
 * <li>Add new atoms with the <code>add</code> family of methods.</li>
 * <li>Remove existing atoms with the <code>remove</code> method.</li>
 * <li>Change the value of an atom while preserving its HyperGraph handle (i.e. its
 * <em>id</em>, if you will) with the <code>replace</code> family of methods.</li>
 * <li>Add new atoms with existing handles with the <code>define</code> family of methods.
 * This is useful, for example, when moving atoms from one hypergraph to another.</li>
 * </ul>
 * </p>
 */
class HyperGraph extends HyperNode {
  override def count(condition: HgQueryCondition): Long = ???

  override def get[A](handle: HgHandle): A = ???

  override def getOne[A](condition: HgQueryCondition): A = ???

  override def getAll[A](condition: HgQueryCondition): List[A] = ???

  override def find[A](condition: HgQueryCondition): List[A] = ???

  override def findOne[A](condition: HgQueryCondition): A = ???

  override def findAll[A](condition: HgQueryCondition): List[A] = ???

  override def remove(handle: HgHandle): Boolean = ???

  override def replace[A](atomHandle: HgHandle, atomObject: A, atomTypeHandle: HgHandle): Boolean = ???

  override def getAtomType(handle: HgHandle): HgHandle = ???

  override def getIncidenceSet(hgHandle: HgHandle): HgIncidenceSet = ???

  override def add[A](atomObject: A, atomTypeHandle: HgHandle, flags: Int): HgHandle = ???

  override def define[A](atomHandle: HgHandle, atomTypeHandle: HgHandle, atomObject: A, flags: Int): Boolean = ???
}
