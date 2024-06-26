package paloul.araneae.cluster

import paloul.araneae.cluster.util.Settings

object Main extends MainSupportSettings with MainSupportInit {

  /**
   * Main Entry point of the application
   */
  def main(args: Array[String]): Unit = {

    // Load the application.conf file and create our own Settings helper class
    // The Settings class is the very first thing created. This is to update
    // certain fields in application.conf dynamically, i.e. Akka Cluster Seed list,
    // before ActorSystem takes hold.
    val s: Settings = settings()

    // Pass the dynamic settings over to init methods
    initialize(s)

  }
}