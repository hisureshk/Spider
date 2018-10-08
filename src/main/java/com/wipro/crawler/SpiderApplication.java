package com.wipro.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpiderApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpiderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpiderApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("EXECUTING : command line runner");
		Crawler crawler;
		if (args.length == 1)
			crawler = new Crawler(args[0]);
		else
			crawler = new Crawler();
		try {
			crawler.crawl();
		} catch (IOException e) {
			LOG.error("Exception processing", e);
		}
	}

}
