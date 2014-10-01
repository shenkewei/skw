package controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import crawler.Searcher;

/**
 * Handle search request <br>
 * 
 * @author skw <br>
 */
@Controller
@Scope("session")
@RequestMapping("search")
public class Search implements Serializable{
	
	public static final long serialVersionUID = 001L;
	
	@Autowired
	private Searcher searcher;
		
	@RequestMapping("search")
	public String search(String term, String field, boolean deleted, Model model){
		//search	
		searcher.search(term, field, deleted);
		//send to view
		return getPage(1, term, field, model);
	}
	
	@RequestMapping("getPage")
	public String getPage(int page, String term, String field, Model model){
		model.addAttribute("term", term);
		model.addAttribute("field", field);
		model.addAttribute("results", searcher.get(page));
		model.addAttribute("page", page);
		model.addAttribute("pageNumber", searcher.getPageNumber());
		model.addAttribute("resultsNumber", searcher.getResultSize());
		return "search";
	}

}
