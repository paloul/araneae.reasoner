package paloul.araneae.reasoner.hypergraph.atom.util

import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Represents an atom's incidence set. That is, a set containing all atoms pointing to a given
 * atom. Instances of this class can be cached and queried in memory.
 */
sealed class HgIncidenceSet(val atom: HgHandle) extends HgAtomSet {

}
