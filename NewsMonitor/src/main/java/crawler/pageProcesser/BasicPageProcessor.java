package crawler.pageProcesser;

import org.apache.http.HttpHost;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Superclass of customized crawler <br>
 * This class is used for the common configurations of all customized crawlers <br>
 * 
 * @author skw <br>
 */
public abstract class BasicPageProcessor implements PageProcessor {
	
	public static final long serialVersionUID = 110L;
	
	private Site site = Site.me().setRetryTimes(3).setCharset("utf8");

	public Site getSite() {
		return site;
	}	
	
	public BasicPageProcessor setCharset(String charset){
		site.setCharset(charset);
		return this;
	}
	
	public BasicPageProcessor setProxy(HttpHost proxy){
		site.setHttpProxy(proxy);
		return this;
	}

}
