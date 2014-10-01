package model.dao;

import java.util.Date;

import model.entity.News;
import model.entity.Remark;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Data access object of entity "News" <br>
 * 
 * @author skw <br>
 */
@Repository
public class NewsDao {
	
	private static Logger logger = LoggerFactory.getLogger(NewsDao.class);
	
	public static final long serialVersionUID = 120L;
	
	@Autowired
	private SessionFactory sessionFactory;

	public void save(News news){
		FullTextSession session = null;
		Transaction transaction = null;
		try{
			session = Search.getFullTextSession(sessionFactory.openSession());
			transaction = session.beginTransaction();
			News origin = (News) session.bySimpleNaturalId(News.class).load(news.getUrl());		
			if(origin == null){ // store new news
				try{
					session.save(news);
					for(Remark remark : news.getRemarks()){
						session.save(remark);
					}
					logger.info("Storing " + news.getUrl() + " succeed!");
					transaction.commit();				
				}catch(HibernateException e){
					transaction.rollback();
					logger.error("An Exception occured when storing news: " + news.getUrl(), e);
				}
			}else if(origin.getRemarks().size() != news.getRemarks().size()) { // update remarks
				Date last = new Date(0);
				for(Remark r: origin.getRemarks()){ // get time of the last remark stored
					if(last.before(r.getTime())){
						last = r.getTime();
					}
				}
				for(Remark r: news.getRemarks()){ // store new remark
					if(last.before(r.getTime())){
						r.setNews(origin);
						session.save(r);
					}
				}
				transaction.commit();
			}
		}catch(Exception e){
			logger.error("An Exception occured when updating news: " + news.getUrl(), e);
		}finally {			
			if(session!=null&&session.isConnected()){session.close();}
		}
				
	}
	
}
