package org.garage.eciresult.scraper

import org.scalatest.FlatSpec

/**
  * Created by shanker on 12/3/17.
  */
class ScraperTest extends FlatSpec {

  "Scrape" should "scrape the page" in {
    val scraper = new Scraper("http://eciresults.nic.in")
    scraper.scrapeAll
  }
}
