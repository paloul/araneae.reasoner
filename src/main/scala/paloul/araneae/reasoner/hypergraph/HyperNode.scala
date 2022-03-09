package paloul.araneae.reasoner.hypergraph

import paloul.araneae.reasoner.hypergraph.atom.util.HgIncidenceSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle
import paloul.araneae.reasoner.hypergraph.query.HgQueryCondition

/**
 * <p>Abstracts the core interface to HyperGraphs for manipulating data in the model. Implementations
 * can model abstractions such as remote database instance, sub-graphs, or other hypernode.</p>
 */
trait HyperNode {

  def count(condition: HgQueryCondition): Long

  def get[A](handle: HgHandle): A
  def getOne[A](condition: HgQueryCondition): A
  def getAll[A](condition: HgQueryCondition): List[A]

  def find[A](condition: HgQueryCondition): List[A]
  def findOne[A](condition: HgQueryCondition): A
  def findAll[A](condition: HgQueryCondition): List[A]

  def remove(handle: HgHandle): Boolean

  def replace[A](atomHandle: HgHandle, atomObject: A, atomTypeHandle: HgHandle): Boolean

  def getAtomType(handle: HgHandle): HgHandle

  def getIncidenceSet(hgHandle: HgHandle): HgIncidenceSet

  def add[A](atomObject: A, atomTypeHandle: HgHandle, flags: Int): HgHandle
  def add[A](atomObject: A, atomTypeHandle: HgHandle): HgHandle = {
    add[A](atomObject, atomTypeHandle, 0)
  }

  def define[A](atomHandle: HgHandle, atomTypeHandle: HgHandle, atomObject: A, flags: Int): Boolean
  def define[A](atomHandle: HgHandle, atomTypeHandle: HgHandle, atomObject: A): Boolean = {
    define[A](atomHandle, atomTypeHandle, atomObject, 0)
  }
}
