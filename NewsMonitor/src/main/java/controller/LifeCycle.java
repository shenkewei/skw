package controller;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import model.dao.NewsDao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import crawler.Crawler;
import crawler.TestDeleted;

/**
 * Behaviors executed when the application is initializing and destroying <br>
 * 
 * @author skw <br>
 */
public class LifeCycle extends HttpServlet {
	
	private static Logger logger = LoggerFactory.getLogger(LifeCycle.class);
	
	public static final long serialVersionUID = 000L;
	
	/**
	 * Behaviors executed when the application is initializing
	 */
	public void init(ServletConfig config) throws ServletException {
		//initialization
		logger.info("Application initializing...");
		//get application context
		ServletContext context = config.getServletContext();		
		WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(context);
		//timed Task starting
		new TimedTask(app).start();
	}

	/**
	 * Behaviors executed when the application is destroying
	 */
	public void destroy() {
		logger.info("Application destroying..."); 
	}
	
	private class TimedTask extends Thread {
		
		private Logger logger = LoggerFactory.getLogger(TimedTask.class);
		
		private WebApplicationContext app = null;
		
		private final long minCycle = 600000l;
		
		private NewsDao newsDao = null;
		private Properties prop = null;
		private SessionFactory sessionFactory = null;
		
		TimedTask(WebApplicationContext app){
			this.app = app;
			newsDao = (NewsDao) app.getBean("newsDao");;
			prop = (Properties) app.getBean("configuration");
			sessionFactory = (SessionFactory) app.getBean("sessionFactory");
		}

		@Override
		public void run() {
			
			//run crawler
			new Crawler(newsDao, prop).run();
			//test deleted news
			new TestDeleted(sessionFactory, prop).run();
			
			//set sleep time
			String tmp = prop.getProperty("CrawlInterval");
			long next = 0;
			if(tmp == null){
				next = this.minCycle;
			}else{
				next = (long) (Float.parseFloat(tmp) * 3600000);
				if(next <= this.minCycle){
					next = this.minCycle;
				}
			}
			
			//sleep
			try {
				Thread.sleep(next);
			} catch (InterruptedException e) {
				new Thread(new TimedTask(app)).start();
				logger.error("An Error occured when crawler is sleeping!", e);
			}
			
			run();
		}
		
	}

}
