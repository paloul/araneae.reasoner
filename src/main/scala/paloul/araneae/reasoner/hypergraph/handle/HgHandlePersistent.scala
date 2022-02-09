package paloul.araneae.reasoner.hypergraph.handle

/**
 * A more permanent <code>HgHandle</code> that survives downtime. A <code>HgHandlePersistent</code>
 * handle will be valid between startup and shutdown of a Hyper Graph system.
 */
trait HgHandlePersistent extends HgHandle {

}
