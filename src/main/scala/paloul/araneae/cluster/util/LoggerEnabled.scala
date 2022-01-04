package paloul.araneae.cluster.util

import org.slf4j.{Logger, LoggerFactory}

trait LoggerEnabled {

  private val _logger = LoggerFactory.getLogger(getClass.getName)

  def log: Logger = { _logger }

}
