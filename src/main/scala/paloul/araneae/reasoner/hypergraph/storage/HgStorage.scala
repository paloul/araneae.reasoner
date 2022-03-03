package paloul.araneae.reasoner.hypergraph.storage

import paloul.araneae.reasoner.hypergraph.handle.HgHandle
import paloul.araneae.reasoner.hypergraph.util.UUID4

/**
 * Base Trait for graph storage implementations. Each <code>HgStorage</code>
 * is associated with and controlled by a HyperGraph instance. The HgStore
 * is never directly accessed by client application. It is used by a HyperGraph
 * instance internally. Implementations of <code>HgStorage</code> utilize an
 * underlying technology to either store ephemeral in memory or persist to disk.
 */
trait HgStorage {

  /** Physical file path to the location where db is stored */
  val dbFilePath: String

  /**
   * Create a new randomly generated persistent Handle as the key and
   * store the given Handles as its value
   *
   * @param link A non-null but potentially empty array of handles that will be linked together
   * @return The newly generated handle that points to the list of given handles
   */
  def store(link: Array[HgHandle]): HgHandle

  /**
   * Store the given array of Handles as values to the one Handle acting as key
   *
   * @param handle A unique handle that refers to the link
   * @param link A non-null but potentially empty array of handles that will be linked together
   * @return The <code>handle</code> parameter
   */
  def store(handle: HgHandle, link: Array[HgHandle]): HgHandle

  /**
   * Write raw binary data to the underlying storage. A randomly generated handle will
   * be created to refer to the data as its key
   *
   * @param data A non-null but potentially empty byte array holding the data to store
   * @return The newly generated handle that refers to the recorded byte data
   */
  def store(data: Array[Byte]): HgHandle

  /**
   * Write raw binary data to the underlying storage using the given handle as key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @param data A non-null but potentially empty byte array holding the data to store
   * @return The <code>handle</code> parameter
   */
  def store(handle: HgHandle, data: Array[Byte]): HgHandle

  /**
   * Remove a link value associated with the given <code>HgHandle</code> key
   *
   * @param handle A unique handle that refers to the link
   * @return True if removed, false otherwise
   */
  def removeLink(handle: HgHandle): Boolean

  /**
   * Remove a raw data value associated with the given <code>HgHandle</code> key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return True if removed, false otherwise
   */
  def removeData(handle: HgHandle): Boolean

  /**
   * Retrieve an existing link by its handle
   *
   * @param handle A unique handle that refers to the link
   * @return An array of <code>HgHandle</code> that make up the link
   */
  def getLink(handle: HgHandle): Array[HgHandle]

  /**
   * Retrieve the raw data in bytes given the <code>HgHandle</code> as key
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return The data as array of bytes stored by the given handle key
   */
  def getData(handle: HgHandle): Array[Byte]

  /**
   * Check to see if the link defined by given <code>HgHandle</code> exists
   *
   * @param handle A unique handle that refers to the link
   * @return True if it exists, false otherwise
   */
  def containsLink(handle: HgHandle): Boolean

  /**
   * Check to see if there is data bound to the given <code>HgHandle</code>
   *
   * @param handle A unique handle that will be used as the key to the data
   * @return True if it exists, false otherwise
   */
  def containsData(handle: HgHandle): Boolean

  /**
   * Get an iterable list of other atom handles in the given atom's incidence set
   *
   * @param handle A unique handle to the atom we want to get incidence set of
   * @return An iterable object over other <code>HgHandle</code>s
   */
  def getIncidenceResultSet(handle: HgHandle): Iterable[HgHandle]

  /**
   * Return the number of other atoms in the incidence set of given atom. In short,
   * return the number of links pointing to the given atom defined by <code>HgHandle</code.
   *
   * @param handle A unique handle to the atom we want to get incidence set of
   * @return Number of links as Long pointing to the given atom
   */
  def getIncidenceSetCardinality(handle: HgHandle): Long





}
