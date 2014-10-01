package crawler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.entity.News;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

public class Searcher implements Serializable{
	
	public static final long serialVersionUID = 101L;
		
	private FullTextSession fullTextSession = null;
	private QueryBuilder queryBuilder = null; 
	private FullTextQuery query = null; //search results
	
	private int RPG = 10; //results per page

	Sort sort = new Sort(new SortField("time", SortField.LONG, true)); //default sort
	
	public Searcher(){}
	
	public Searcher(SessionFactory sessionFactory){
		fullTextSession = Search.getFullTextSession(sessionFactory.openSession());
		queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(News.class).get();
	}
	
	/**
	 * Execute specific Keyword query <br>
	 * @param field: Field
	 * @param term: Keyword
	 * @param deleted: Do search only in the deleted news when deleted is true. <br>
	 * 				   Do search in all the news when deleted is false.
	 */
	public void search(String term, String field, boolean deleted){
		
		ArrayList<Query> queries = new ArrayList<Query>();
		
		if(term.equals("") || term == null){
			queries.add(queryBuilder.keyword().wildcard().onField("content").matching("?").createQuery());
		}else{
			switch(field){
				case "content": 
					queries.add(queryBuilder.keyword().onField(field).matching(term).createQuery());
					break;
				case "author":
					for(String s: term.split(" ")){
						queries.add(queryBuilder.keyword().onField("author").matching(s).createQuery());
					}
					break;
				case "paragraph":
					query = fullTextSession.createFullTextQuery(
							queryBuilder.keyword().onField("title").matching(term).createQuery(), News.class);
					
					if(query.getResultSize() == 0){
						return;
					}else{
						News news = (News) query.list().get(0);
						queries.add(queryBuilder.keyword().onField("paragraph").matching(news.getContent()).createQuery());
					}
					break;
				default:
					queries.add(queryBuilder.keyword().onField("content").matching(term).createQuery());
			}
		}
		
		if(deleted){ //if deleted
			queries.add(queryBuilder.keyword().onField("deleted").matching(1).createQuery());
		}
		
		//create query
		if(queries.size() == 1){
			query = fullTextSession.createFullTextQuery(queries.get(0), News.class);
		}else{
			@SuppressWarnings("rawtypes")
			BooleanJunction<BooleanJunction> condition = queryBuilder.bool();
			for(Query q: queries){
				condition.must(q);
			}
			query = fullTextSession.createFullTextQuery(condition.createQuery(), News.class);
		}
		
		configQuery();
	}
	
	/**
	 * Get the specific page of the results
	 * @param page Page number, numbered from 1
	 * @return News list
	 */
	@SuppressWarnings("unchecked")
	public List<News> get(int page){
		if(query == null){ //search for all the news if no specific query has been executed
			search(null, null, false);
		}
		query.setFirstResult((page-1) * RPG);		
		return query.list();
	}	
	
	/**
	 * Get total number of the results
	 */
	public int getResultSize(){
		if(query == null){ //search for all the news if no specific query has been executed
			search(null, null, false);
		}
		return query.getResultSize();
	}
	
	/**
	 * Get total number of the pages
	 */
	public int getPageNumber(){
		int result = getResultSize()-1;
		int rel = result/RPG+1;
		return rel;
	}	
	
	//configure query after search
	private void configQuery(){
		query.setSort(sort).setMaxResults(RPG);
	}
	
	@Override
	protected void finalize(){
		this.fullTextSession.close();
	}
	
}