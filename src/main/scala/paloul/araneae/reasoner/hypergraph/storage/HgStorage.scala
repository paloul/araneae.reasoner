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

}
