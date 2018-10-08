package com.wipro.crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Crawler {

	private static final String URL_CLOSE = "</url>";
	private static final String NEWLINE = "\n";
	private static final String URLSET = "<urlset>";
	private static final String URLSET_CLOSE = "<urlset>";
	private static final String URL = "<url>";
	private static final String SITEMAP_XML = "sitemap.xml";
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();	
	private String currentUrl;
	private String domainURL;
	
	private static Logger LOG = LoggerFactory.getLogger(Crawler.class);
		
	public Crawler(){
		this.domainURL = "https://wiprodigital.com";
	}
	
	public Crawler(String domainURL){
		this.domainURL = domainURL;
	}
	
	/**
	 *  This method crawls the given URL of the site and writes all
	 *  the links related that to the domain in a file called sitemap.xml
	 * @param url
	 * @throws IOException
	 */
	public void crawl() throws IOException {
		LOG.debug("Begining crawling of site " + domainURL);
		CrawlPage crawlPage = new CrawlPage();		
		do {
			if (this.pagesToVisit.isEmpty()) {
				currentUrl = domainURL;
				this.pagesVisited.add(domainURL);
			} else {
				currentUrl = this.nextUrl();
			}
			boolean isCrawl = crawlPage.crawl(currentUrl, domainURL);
			if (isCrawl)
				this.pagesToVisit.addAll(crawlPage.getLinks());
		} while (currentUrl != null);
		
		writeToFile();
		LOG.debug("Completed crawling of site " + domainURL);

	}

	/**
	 * This method returns the next url from the list of pagesToVisit
	 * that is not already visited by the crawler. If not url found returns null.
	 * @return url
	 */
	private String nextUrl() {
		String nextUrl;
		while (!this.pagesToVisit.isEmpty()) {
			nextUrl = this.pagesToVisit.remove(0);
			if (!pagesVisited.contains(nextUrl)) {
				this.pagesVisited.add(nextUrl);
				return nextUrl;
			}
		}
		return null;
	}

	/**
	 * This method writes the sites visited into a file called sitemap.xml
	 * @throws IOException
	 */
	private void writeToFile() throws IOException {
		Iterator<String> iter = pagesVisited.iterator();
		FileWriter fileWriter = new FileWriter(SITEMAP_XML);
		fileWriter.append(URLSET).append(NEWLINE);
		while (iter.hasNext()) {
			String next = iter.next();
			fileWriter.append(URL).append(next).append(URL_CLOSE).append(NEWLINE);
		}
		fileWriter.append(URLSET_CLOSE).append(NEWLINE);
		fileWriter.close();
	}

}
