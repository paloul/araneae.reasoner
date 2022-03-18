package paloul.araneae.reasoner.hypergraph.atom.util

import paloul.araneae.reasoner.hypergraph.handle.HgHandle

/**
 * Represents an atom's incidence set. That is, a set containing all atoms
 * pointing to a given atom. This might be confused with a link, I did.
 * The incidence set does not define a (link) relationship. Its a basic
 * collection of all other atoms that point to a particular/given atom.
 * The collection of all other atoms that point to the particular/given
 * atom can be across multiple unrelated links.
 * Instances of this class can be cached and queried in memory.
 * @param atom Our atom's handle that all other atoms in the atom set point to
 */
sealed class HgIncidenceSet(val atom: HgHandle) extends HgAtomSet {

}
