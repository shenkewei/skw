package crawler.pipeline;

import model.dao.NewsDao;
import model.entity.News;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

/**
 * Store results in Hibernate Searcher <br>
 * 
 * @author skw <br>
 */
public class IndexPipeline extends FilePersistentBase implements Pipeline{
	
	public static final long serialVersionUID = 120L;
	
	private NewsDao newsDao = null;
	
    public IndexPipeline(NewsDao newsDao) {
    	this.newsDao = newsDao;
    }
    
	public void process(ResultItems resultItems, Task task) {
    	News news = (News)resultItems.get("news");
    	if(news != null){
    		newsDao.save(news);
    	}    	
    }
	
}
