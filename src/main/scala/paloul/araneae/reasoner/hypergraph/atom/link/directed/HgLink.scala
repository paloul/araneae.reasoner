package paloul.araneae.reasoner.hypergraph.atom.link.directed

import paloul.araneae.reasoner.hypergraph.atom.link.undirected
import paloul.araneae.reasoner.hypergraph.atom.util.HgAtomSet

/**
 * Represents a directed hypergraph link. A directed hypergraph link is an atom itself.
 * It differs from an undirected hypergraph link in that its not just a set of other atoms.
 * A directed hyperedge is an ordered pair of atom subsets, constituting the tail and head
 * of the hyperedge, i.e. E1 = ({V1,V2,V3}, {V4,V5}), meaning V1,V2,V3 all point to both V4,V5.
 */
trait HgLink extends undirected.HgLink {

  /** Represents the source (head) of this hyperedge. */
  protected val sourceAtomSet: HgAtomSet

}
