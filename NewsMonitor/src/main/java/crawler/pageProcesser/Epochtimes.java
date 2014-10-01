package crawler.pageProcesser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.entity.News;
import model.entity.Remark;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;


/**
 * Crawler for Epochtimes <br>
 * 
 * @author skw ChenHang <br>
 */
public class Epochtimes extends BasicPageProcessor{

	private static Logger logger = LoggerFactory.getLogger(Epochtimes.class);
	
	public static final long serialVersionUID = 111L;
	
	private static String news = "http://www\\.epochtimes\\.com/gb/\\d\\S*html"; //news
	private static String rmk = "http://www\\.epochtimes\\.com/services/ajax/getcomments\\S*"; //remark
	
	public void process(Page page) {
		
		String url = page.getUrl().toString();
		if(url.matches(news)){ //news
			this.newsProcess(page);
		}else if(url.matches(rmk)){ //remark
			this.remarkProcess(page);		
		}else{ //navigation
			page.addTargetRequests(page.getHtml().links().regex(news).all());
			page.setSkip(true);
		}		
		
	}
	
	//process news
	private void newsProcess(Page page){
		
		News news = new News();
		//website
		news.setWebsite("大纪元新闻网");
		//url
		news.setUrl(page.getUrl().toString());		
		//title
		news.setTitle(page.getHtml().xpath("//h1[@class*='title']/text()").toString().trim());
		//date
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);			
			//use RegEx to extract time
			Pattern time = Pattern.compile("(\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
			String s = page.getHtml().xpath("//div[@class*='mbottom10']/allText()").toString();		
			Matcher matcher = time.matcher(s);
			if(matcher.find()){ //find time stamp
				news.setDate(df.parse(matcher.group()));
			}else{ //use crawl time instead
				news.setDate(new Date());
			}
		} catch (ParseException e) {				
			logger.error("Parse time error", e);
		}
		//keywords
		news.setKeywords(page.getHtml().xpath("//meta[@name='keywords']/@content").toString());
		//digest
		news.setDigest(page.getHtml().xpath("//meta[@name='description']/@content").toString().split("-")[0].trim());
		//editor, reporter and content
		Pattern reporterPattern = Pattern.compile("记者[(\u4e00-\u9fa5|、)|\\d]+(报|电|专|特|综|编)");
		Pattern editorPattern = Pattern.compile("(编(辑|缉)|译者)：[\u4e00-\u9fa5]+");
		for(String par : page.getHtml().xpath("//p").all()){
			
			Matcher reporter = reporterPattern.matcher(par);
			Matcher editor = editorPattern.matcher(par);
			
			if(news.getContent().equals("") && reporter.find()){ //reporter
		      	String tmp = reporter.group();
		      	tmp = tmp.substring(2, tmp.length()-1);
		      	String[] tmps = tmp.split("、");
		      	if(tmps.length > 1){ //more than one reporter
		      		tmp = tmps[tmps.length-1];
		      		while(tmp.length() > 3){
		          		tmp = tmp.substring(0, tmp.length()-2);
		          	}
		      		tmps[tmps.length-1] = tmp;
		      		for(String s : tmps){
		      			news.addReporter(s);
		      		}
		      	}else{ //one reporter
		      		while(tmp.length() > 3){
		      			tmp = tmp.substring(0, tmp.length()-2);
		      		}
		      		news.addReporter(tmp);
		      	}		      	
			}
			
			if(editor.find()){ //editor
				do{
					String s = editor.group();
					while(s.length() > 3){
						s = s.substring(3);
					}
					news.addEditor(s);
				}while(editor.find());
				
			}else{ //paragraph				
				news.addParagraph(par.replaceAll("<[^<>]*>", "").trim());
			}		
		}
		
		//get writer
		String[] tmp = news.getTitle().split("：");
		if(tmp.length > 1 && tmp[0].length() <= 3){
			if(!tmp[0].equals("组图")){
				news.addWriter(tmp[0]);
			}			
		}		
		
		//forward to remarkProcess
		String newsId = getNewsId(page.getUrl().toString());
		String link = "http://www.epochtimes.com/services/ajax/getcomments?aid="+newsId+"&t=uc&encoding=gb&offset=0&number=1000";
		Request request = new Request(link).putExtra("news", news);
		page.addTargetRequest(request);
		page.setSkip(true);
	}
	
	//process remark
	private void remarkProcess(Page page){
		
		String allremark = page.getRawText().toString();
		JSONObject jsonObject = JSONObject.fromObject(allremark);
		JSONObject jsonObjectNext = jsonObject.getJSONObject("comments");
        JSONArray array = jsonObjectNext.getJSONArray("rows"); 

		News news = (News) page.getRequest().getExtra("news");
		
    	for(int i=0;i<array.size();i++){
			Remark remark = new Remark();
    		@SuppressWarnings("unchecked")
			Map<String,String> rmk = (Map<String, String>)array.get(i);
			//time
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				System.out.print(rmk.get("time"));
				remark.setTime(df.parse(rmk.get("time")));			
			} catch (ParseException e) {				
				logger.error("Parse time error", e);
			}
			//content
			remark.setContent(rmk.get("content").replaceAll("<[^<>]*>", ""));
			//observer
			remark.setObserver("大纪元网友");
			//remark
			news.addRemarks(remark);
    	}
    	
		page.putField("news", news);
	}

	private String getNewsId(String url){
		
		String NewsId = null;
		if(url.matches(news)){
			String reg = "[/n,.htm]+";
			String[]s = url.split(reg);
			NewsId = s[11];
		}
		else if(url.matches(rmk)){
			String reg = "[d=,&t]+";
			String[]s = url.split(reg);
			NewsId = s[5];
		}
		return NewsId;
	}

}
