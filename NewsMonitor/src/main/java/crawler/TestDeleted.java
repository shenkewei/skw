package crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.entity.News;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test all news to find the deleted ones <br>
 * 
 * @author skw <br>
 */
public class TestDeleted implements Serializable {
	
	private static Logger logger = LoggerFactory.getLogger(TestDeleted.class);
	
	public static final long serialVersionUID = 102L;
	
	private SessionFactory sessionFactory = null;
	private ExecutorService threadPool = null;
	private Proxy proxy = null;
	
	public TestDeleted() {};
	
	public TestDeleted(SessionFactory sessionFactory, Properties prop) {
		this.sessionFactory = sessionFactory;
		//initialize thread pool
		String threadNumber = prop.getProperty("ThreadNumber");
		if(threadNumber != null){
			this.threadPool = Executors.newFixedThreadPool(Integer.parseInt(threadNumber));
		}else{
			this.threadPool = Executors.newSingleThreadExecutor();
		}
		//get Proxy
		String ip = prop.getProperty("ProxyIP");
		String port = prop.getProperty("ProxyPort");
		if(ip != null && port != null) {
			InetSocketAddress socketAddress;
			try{
				socketAddress = new InetSocketAddress(InetAddress.getByName(ip), Integer.parseInt(port));
				proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
			}catch(Exception e){
				proxy = null;
			}			
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void run() {
		logger.info("Seeking deleted news...");
		Date start = new Date();
		Session session = null;
		try{
			//config hibernate query
			int pageSize = 1000; //results per page
			session = this.sessionFactory.openSession();
			Query query = session.createQuery("select news.url from News news");
			Transaction transaction = session.beginTransaction();
			query.setMaxResults(pageSize);
		
			//test urls
			int now = 0; //current result number
			final ArrayList<String> deletedNews = new ArrayList<String>(100); //URLs of the deleted news			
			List list = null;
			do{
				ArrayList<Callable<Object>> callers = new ArrayList<Callable<Object>>(pageSize);
				query.setFirstResult(now);
				now += pageSize;
				list = query.list();
				if(proxy != null){ //use proxy
					for(Iterator it = list.iterator(); it.hasNext();) {
						final String url = (String) it.next();
						callers.add(new Callable<Object>(){
							public Object call(){
								InputStream input = null;
								try{
									input = new URL(url).openConnection(proxy).getInputStream();
								}catch (FileNotFoundException e){
									deletedNews.add(url);
									logger.info(url + " has been deleted!");
								}catch (Exception e) {
									logger.error("An Exception occured testing url: " + url + " when seeking deleted news!", e);
								}finally{
									if(input!=null){try{input.close();}catch(IOException e){									
										logger.error("An closing net connection exception occured when seeking deleted news!", e);}}
								}
								return null;
							}
						});
					}
				}else{ //don't use proxy
					for(Iterator it = list.iterator(); it.hasNext();) {
						final String url = (String) it.next();
						callers.add(new Callable<Object>(){
							public Object call(){
								InputStream input = null;
								try{
									input = new URL(url).openConnection().getInputStream();
								}catch (FileNotFoundException e){
									deletedNews.add(url);
									logger.info(url + " has been deleted!");
								}catch (Exception e) {
									logger.error("An Exception occured testing url: " + url + " when seeking deleted news!", e);
								}finally{
									if(input!=null){try{input.close();}catch(IOException e){									
										logger.error("An closing net connection exception occured when seeking deleted news!", e);}}
								}
								return null;
							}
						});
					}
				}				
				threadPool.invokeAll(callers);
				threadPool.shutdown();
			}while(list.size() != 0);
			
			//update deleted attribute
			for(String url: deletedNews){
				News news = (News) session.bySimpleNaturalId(News.class).load(url);
				news.setDeleted(true);
			}
		
			//submit
			transaction.commit();			
		}catch(Exception e){
			logger.error("An Exception occured when seeking deleted news!", e);
		}finally{
			if(session!=null&&session.isConnected()){session.close();}
		}
		Date end = new Date();
		logger.info("Deleted attribute of all the news has been modified in " + (end.getTime() - start.getTime()) + " ms!");
	}

}
