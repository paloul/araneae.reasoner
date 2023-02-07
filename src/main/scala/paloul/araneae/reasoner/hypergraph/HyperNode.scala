package paloul.araneae.reasoner.hypergraph

import paloul.araneae.reasoner.hypergraph.atom.Atom
import paloul.araneae.reasoner.hypergraph.atom.util.HgIncidenceSet
import paloul.araneae.reasoner.hypergraph.handle.HgHandle
import paloul.araneae.reasoner.hypergraph.query.HgQueryCondition

/**
 * <p>Abstracts the core interface to HyperGraphs for manipulating data in the model. Implementations
 * can model abstractions such as remote database instance, sub-graphs, or other hypernodes.</p>
 */
trait HyperNode {

  /**
   * Count number of atoms with the given atom type
   * @param atomType The type or label of atoms
   * @return
   */
  def count(atomType: String): Long

  /**
   * Count the number of atoms given the query
   * @param condition The Query Condition searching for atoms
   * @return
   */
  def count(condition: HgQueryCondition): Long

  /**
   * Get the Atom and its properties given its handle
   * @param handle The handle to specific atom
   * @return
   */
  def get(handle: HgHandle): Atom

  /**
   * Get all possible Atoms given the query condition
   * @param condition The query condition searching for atoms
   * @return
   */
  def get(condition: HgQueryCondition): List[Atom]

  /**
   * Get the Atom type of the given atom handle
   * @param handle The handle to specific atom
   * @return
   */
  def getAtomType(handle: HgHandle): String

  /**
   * Get Incidence Set which is a collection of atoms pointing to a particular atom
   * @param hgHandle The handle of a particular atom
   * @return
   */
  def getIncidenceSet(hgHandle: HgHandle): HgIncidenceSet

  /**
   * Remove the atom from the graph
   * @param handle The handle to specific atom
   * @return
   */
  def remove(handle: HgHandle): Some[Atom]

  /**
   * Add the atom to the graph
   * @param atom The Atom
   * @return
   */
  def add(atom: Atom): HgHandle

  /**
   * Add the atom to the graph
   * @param atom The Atom
   * @param atomType A String representing the type of the provided Atom
   * @return
   */
  def add(atom: Atom, atomType: String): HgHandle
}
