package paloul.araneae.reasoner.hypergraph.storage

/**
 * Base Trait for graph storage implementations. Each <code>HgStorage</code>
 * is associated with and controlled by a HyperGraph instance. The HgStore
 * is never directly accessed by client application. It is used by a HyperGraph
 * instance internally. Implementations of <code>HgStorage</code> utilize an
 * underlying technology to either store ephemeral in memory or persist to disk.
 */
trait HgStorage {

  val dbFilePath: String

}
