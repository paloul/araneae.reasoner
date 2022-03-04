package paloul.araneae.reasoner.hypergraph.storage
import paloul.araneae.reasoner.hypergraph.handle.HgHandle

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

  /**
   * Create a new randomly generated persistent Handle as the key and
   * store the given Handles as its value
   *
   * @param link A non-null but potentially empty array of handles that will be linked together
   * @return The newly generated handle that points to the list of given handles
   */
  override def store(link: Array[HgHandle]): HgHandle = ???

  /**
   * Store the given array of Handles as values to the one Handle acting as key
   *
   * @param handle A unique handle that refers to the link
   * @param link   A non-null but potentially empty array of handles that will be linked together
   * @return The <code>handle</code> parameter
   */
  override def store(handle: HgHandle, link: Array[HgHandle]): HgHandle = ???

  /**
   * Write raw binary data to the underlying storage. A randomly generated handle will
   * be created to refer to the data as its key
   *
   * @param data A non-null but potentially empty byte array holding the data to store
   * @return The newly generated handle that refers to the recorded byte data
   */
  override def store(data: Array[Byte]): HgHandle = ???

  /**
   * Write raw binary data to the underlying storage using the given handle as key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @param data   A non-null but potentially empty byte array holding the data to store
   * @return The <code>handle</code> parameter
   */
  override def store(handle: HgHandle, data: Array[Byte]): HgHandle = ???

  /**
   * Remove a link value associated with the given <code>HgHandle</code> key
   *
   * @param handle A unique handle that refers to the link
   * @return True if removed, false otherwise
   */
  override def removeLink(handle: HgHandle): Boolean = ???

  /**
   * Remove a raw data value associated with the given <code>HgHandle</code> key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return True if removed, false otherwise
   */
  override def removeData(handle: HgHandle): Boolean = ???

  /**
   * Retrieve an existing link by its handle
   *
   * @param handle A unique handle that refers to the link
   * @return An array of <code>HgHandle</code> that make up the link
   */
  override def getLink(handle: HgHandle): Array[HgHandle] = ???

  /**
   * Retrieve the raw data in bytes given the <code>HgHandle</code> as key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return The data as array of bytes stored by the given handle key
   */
  override def getData(handle: HgHandle): Array[Byte] = ???

  /**
   * Check to see if the link defined by given <code>HgHandle</code> exists
   *
   * @param handle A unique handle that refers to the link
   * @return True if it exists, false otherwise
   */
  override def containsLink(handle: HgHandle): Boolean = ???

  /**
   * Check to see if there is data bound to the given <code>HgHandle</code>
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return True if it exists, false otherwise
   */
  override def containsData(handle: HgHandle): Boolean = ???

  /**
   * Get an iterable list of other atom handles in the given atom's incidence set
   *
   * @param handle A unique handle to the atom we want to get incidence set of
   * @return An iterable object over other <code>HgHandle</code>s
   */
  override def getIncidenceResultSet(handle: HgHandle): Iterable[HgHandle] = ???

  /**
   * Return the number of other atoms in the incidence set of given atom. In short,
   * return the number of links pointing to the given atom defined by <code>HgHandle</code.
   *
   * @param handle A unique handle to the atom we want to get incidence set of
   * @return Number of links as Long pointing to the given atom
   */
  override def getIncidenceSetCardinality(handle: HgHandle): Long = ???

  /**
   * Insert a new link handle in the target atom's <code>HgHandle</code>. If the link is
   * part of the incidence set for the atom, it will not be added, and this function will
   * return false.
   *
   * @param atomHandle A unique handle to the atom who's incidence set will be updated
   * @param linkHandle A unique handle to the newly created link
   * @return True if added, false otherwise
   */
  override def addIncidenceLink(atomHandle: HgHandle, linkHandle: HgHandle): Boolean = ???

  /**
   * Remove the link handle from the given target atom's incidence set. If the link is
   * not part of the incidence set of the given atom, nothing will be done, and this
   * function will return false.
   *
   * @param atomHandle A unique handle to the atom who's incidence set will be updated
   * @param linkHandle A unique handle to a link that no longer points to the atom
   * @return True if removed, false otherwise
   */
  override def removeIncidenceLink(atomHandle: HgHandle, linkHandle: HgHandle): Boolean = ???

  /**
   * Remove the whole incidence set of the given target atom. Typically used only when
   * the atom itself is being completely removed from the hypergraph.
   *
   * @param atomHandle A unique handle to the atom who's incidence set will be removed
   * @return True if removed, false otherwise
   */
  override def removeIncidenceSet(atomHandle: HgHandle): Boolean = ???

  /**
   * Close the underlying storage mechanism. Reserved for internal use. Based on
   * implementation of underlying storage, might not be required.
   *
   * @return True if successful cleanup and exit, false otherwise
   */
  override def close(): Boolean = ???
}
