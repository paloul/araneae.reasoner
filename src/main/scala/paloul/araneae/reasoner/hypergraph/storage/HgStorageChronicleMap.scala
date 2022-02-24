package paloul.araneae.reasoner.hypergraph.storage

/**
 * The companion object to HgStorageChronicleMap. Static function calls are
 * available under this static object. Namely, it creates a new instance of
 * HgStorageChronicleMap.
 */
object HgStorageChronicleMap {

  /** Instantiate a new HgStorageChronicleMap */
  def apply(dbStoragePath: String): HgStorageChronicleMap = {

    new HgStorageChronicleMap(dbStoragePath)
  }

}

/**
 * An implementation of <code>HgStorage</code> that uses ChronicleMap as its underlying
 * storage mechanism. This implementation uses ChronicleMap to persist the HyperGraph to
 * disk via Memory Mapped files.
 *
 * https://github.com/OpenHFT/Chronicle-Map
 *
 * @param dbStoragePath The path to where the files for this persistent storage are kept
 */
class HgStorageChronicleMap private (dbStoragePath: String) extends HgStorage {

  val dbFilePath: String = dbStoragePath

}
