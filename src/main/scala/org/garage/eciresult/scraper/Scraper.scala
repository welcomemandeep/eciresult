package org.garage.eciresult.scraper

import java.util.logging.Level

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.{HtmlPage, HtmlSelect, HtmlTable}
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters._

/**
  * Created by shanker on 12/3/17.
  */
object Scraper {

  def apply(url: String): Scraper = {
    new Scraper(url)
  }
}

class Scraper(val url: String) {

  def scrapeAll(): Unit = {

    LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

    val webClient: WebClient = new WebClient()
    webClient.getOptions.setJavaScriptEnabled(true)
    webClient.getOptions.setCssEnabled(false)
    webClient.getOptions.setRedirectEnabled(true)
    val page: HtmlPage = webClient.getPage(url)
    val anchor = page.getAnchorByText("Constituencywise-All Candidates")
    val page2: HtmlPage = anchor.click()
    scrapeState(page2, 1)
  }

  def scrapeState(page: HtmlPage, stateOption: Int): Unit = {
    val statesDropdown = page.getElementById("ddlState").asInstanceOf[HtmlSelect]
    val p: HtmlPage = statesDropdown.getOption(stateOption).click()
    val p1: HtmlPage = scrapeAC(p, 1)
    if (stateOption < statesDropdown.getOptionSize - 1) {
      scrapeState(p1, stateOption + 1)
    }
  }

  def scrapeAC(page: HtmlPage, acOption: Int): HtmlPage = {
    val acDropdown = page.getElementById("ddlAC").asInstanceOf[HtmlSelect]

    val stateName = page.getElementById("ddlState").asInstanceOf[HtmlSelect].getSelectedOptions.get(0).asText()
    val acName = page.getElementById("ddlAC").asInstanceOf[HtmlSelect].getSelectedOptions.get(0).asText()

    val x = page.getElementById("div1")
    val table = x.getFirstElementChild.asInstanceOf[HtmlTable]

    val all = table.getRows.subList(3, table.getRows.size() - 1).asScala.foldLeft(List[String]())((a, b) => {
      a :+ stateName + "~" + acName + "~" + b.getCells.asScala.map(_.asText()).mkString("~")
    }).mkString("\n")

    println(all)

    val p: HtmlPage = acDropdown.getOption(acOption).click()
    if (acOption < acDropdown.getOptionSize - 1)
      scrapeAC(p, acOption + 1)
    else
      p
  }
}
