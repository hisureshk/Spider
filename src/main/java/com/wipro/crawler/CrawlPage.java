package com.wipro.crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CrawlPage {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private List<String> thirdPartyLinks = new LinkedList<String>();
	private static Logger LOG = LoggerFactory.getLogger(CrawlPage.class);
	
	
	/**
	 * This performs all the work. It makes an HTTP request, checks the response,
	 * and then gathers up all the links on the page.
	 * 
	 * @param url
	 *            - The URL to visit
	 * @return whether or not the crawl was successful
	 */
	public boolean crawl(String url, String domain) {

		if (url == null)
			return false;
		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			if ((connection.response().statusCode() != 200) || (!connection.response().contentType().contains("text/html"))) {
				LOG.error("**Failure** Retrieved something other than HTML");
				return false;
			}
			Elements linksOnPage = htmlDocument.select("a[href]");
			for (Element link : linksOnPage) {
				String absUrl = link.absUrl("href");
				String absUrlLower = absUrl.toLowerCase();

				// Adding only wipro digital links
				if (absUrlLower.contains(domain)) {					
					int hashedIndex = absUrl.indexOf("#");
					if (hashedIndex != -1)
						absUrl = absUrl.substring(0, hashedIndex);
					this.links.add(absUrl);
				} else {
					this.thirdPartyLinks.add(absUrl);
				}
			}
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	public List<String> getLinks() {
		return this.links;
	}

	public List<String> getThirdPartyLinks() {
		return this.thirdPartyLinks;
	}

}