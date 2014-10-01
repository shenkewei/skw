package crawler;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import model.dao.NewsDao;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.LifeCycle;
import us.codecraft.webmagic.Spider;
import crawler.pageProcesser.BasicPageProcessor;
import crawler.pageProcesser.Epochtimes;
import crawler.pipeline.IndexPipeline;

/**
 * Master of the crawler module <br>
 * 
 * @author skw <br>
 */
public class Crawler implements Serializable{
	
	private static Logger logger = LoggerFactory.getLogger(LifeCycle.class);
	
	public static final long serialVersionUID = 100L;
	
	private NewsDao newsDao = null;
	private int threadNumber = 1;
	private HttpHost proxy = null;	
	
	public Crawler(){}
	
	public Crawler(NewsDao newsDao, Properties prop) {
		this.newsDao = newsDao;
		//get thread number
		String threadNum = prop.getProperty("ThreadNumber");
		if(threadNum != null){
			this.threadNumber = Integer.parseInt(threadNum);
		}
		//get Proxy
		String ip = prop.getProperty("ProxyIP");
		String port = prop.getProperty("ProxyPort");
		if(ip != null && port != null){
			this.proxy = new HttpHost(ip, Integer.parseInt(port));
		}
	}
	
	public void run() {			
		logger.info("Spider starting...");
		Date start = new Date();
		
		//crawlers
		crawl(new Epochtimes(),
				"http://www.epochtimes.com/gb/",
				"http://www.epochtimes.com/gb/ncChineseCommunity.htm",
				"http://www.epochtimes.com/gb/nscfreezone.htm", //tattle
				"http://www.epochtimes.com/gb/nccomment.htm",
				"http://www.epochtimes.com/gb/ncGongShang.htm", //industry and commerce
				"http://www.epochtimes.com/gb/ncsports.htm", //sports
				"http://www.epochtimes.com/gb/ncyule.htm", //entertainment
				"http://www.epochtimes.com/gb/ncVideo_Radio.htm",
				"http://www.epochtimes.com/gb/ncMagazine.htm",
				"http://www.epochtimes.com/gb/ncJiyuanDongtai.htm",
				"http://www.epochtimes.com/gb/nsc413.htm",
				"http://www.epochtimes.com/gb/nsc414.htm",
				"http://www.epochtimes.com/gb/nsc415.htm",
				"http://www.epochtimes.com/gb/nsc418.htm",
				"http://www.epochtimes.com/gb/nsc419.htm",
				"http://www.epochtimes.com/gb/nsc420.htm",
				"http://www.epochtimes.com/gb/nsc422.htm",
				"http://www.epochtimes.com/gb/nsc423.htm",
				"http://www.epochtimes.com/gb/nsc424.htm",
				"http://www.epochtimes.com/gb/nsc994.htm"
				);
		
		Date end = new Date();
		logger.info("Spider has been finished in " + (end.getTime() - start.getTime()) + " ms!");
		
		
	}	
	
	private void crawl(BasicPageProcessor pageProcessor, String... urls) {
		if(this.proxy != null){ //set proxy if exist
			pageProcessor.setProxy(proxy);
		}
		Spider.
	       create(pageProcessor).
	       addUrl(urls).
	       addPipeline(new IndexPipeline(newsDao)).thread(this.threadNumber).run();	       		
	}
}
