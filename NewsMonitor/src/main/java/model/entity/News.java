package model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * News bean <br>
 * 
 * @author skw <br>
 */
@Entity
@Indexed(index="News")
public class News {
	
	private Integer id; //identifier
	private String website; //source website
	private String url;
	private String title;
	private Date date; //time when the news was published
	private Set<String> reporter = new TreeSet<String>(); //output a string which use space as separator when being indexed or persistent
	private Set<String> editor = new TreeSet<String>(); //output a string which use space as separator when being indexed or persistent
	private Set<String> writer = new TreeSet<String>(); //output a string which use space as separator when being indexed or persistent
	private Set<String> keywords = new TreeSet<String>(); //output a string which use space as separator when being indexed or persistent
	private String digest; //abstract
	private StringBuilder content = new StringBuilder(); 
	private boolean deleted = false; //whether this news has been deleted on the original Website
	private Set<Remark> remarks = new HashSet<Remark>();
	
	private String separator = System.getProperty("line.separator"); //system line break, used for store content

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(nullable=false, updatable=false)
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
	@org.hibernate.annotations.NaturalId
	@Column(nullable=false, unique=true, updatable=false)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(nullable=false, updatable=false)
	@Field(index=Index.YES, analyze=Analyze.NO)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(nullable=false, updatable=false, columnDefinition="TIMESTAMP")
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Transient
	@Field(index=Index.YES, analyze=Analyze.NO)
	@NumericField
	public Long getTime() {
		return this.date.getTime();
	}
	
	@Column(updatable=false)
	public String getReporter() {
		StringBuilder rel = new StringBuilder();
		for(String s: this.reporter){
			rel.append(s).append(" ");
		}
		return rel.toString().trim();
	}
	void setReporter(String reporter) {
		this.addReporter(reporter);
	}
	/**
	 * Add reporter to reporter list
	 * @param reporter: Reporter name, separate by space
	 */
	public void addReporter(String reporter){
		if(reporter != null){
			for(String s: reporter.split("[^\u4e00-\u9fa5]+")){
				this.reporter.add(s);
			}
		}
	}
	
	@Column(updatable=false)
	public String getEditor() {
		StringBuilder rel = new StringBuilder();
		for(String s: this.editor){
			rel.append(s).append(" ");
		}
		return rel.toString().trim();
	}
	void setEditor(String editor) {
		this.addEditor(editor);
	}
	/**
	 * Add editor to editor list
	 * @param editor: Editor name, separate by space
	 */
	public void addEditor(String editor) {
		if(editor != null){
			for(String s: editor.split("[^\u4e00-\u9fa5]+")){
				this.editor.add(s);
			}
		}
		
	}
	
	@Column(updatable=false)
	public String getWriter() {
		StringBuilder rel = new StringBuilder();
		for(String s: this.writer){
			rel.append(s).append(" ");
		}
		return rel.toString().trim();
	}
	void setWriter(String writer) {
		this.addWriter(writer);
	}
	/**
	 * Add writer to writer list
	 * @param writer: Editor name, separate by space
	 */
	public void addWriter(String writer) {
		if(writer != null){
			for(String s: writer.split("[^\u4e00-\u9fa5]+")){
				this.writer.add(s);
			}
		}		
	}
	
	@Transient
	@Field(index=Index.YES, analyze=Analyze.YES, analyzer=@Analyzer(impl = WhitespaceAnalyzer.class))
	public String getAuthor() {
		StringBuilder rel = new StringBuilder();
		for(String s: this.reporter){
			rel.append(s).append(" ");
		}
		for(String s: this.editor){
			rel.append(s).append(" ");
		}
		for(String s: this.writer){
			rel.append(s).append(" ");
		}
		return rel.toString().trim();
	}
	
	@Column(updatable=false)
	public String getKeywords() {
		StringBuilder rel = new StringBuilder();
		for(String s: this.keywords){
			rel.append(s).append(" ");
		}
		return rel.toString().trim();
	}
	public void setKeywords(String keywords) {
		this.addKeywords(keywords);
	}
	public void addKeywords(String keywords) {
		for(String s: keywords.split("[^\u4e00-\u9fa5]+")){
			this.keywords.add(s);
		}
	}	
	
	@Column(nullable=false, updatable=false, columnDefinition="TEXT")
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	
	@Column(nullable=false, updatable=false, columnDefinition="TEXT")
	@Fields({
			@Field(name="content", index=Index.YES, analyze=Analyze.YES, store=Store.NO, analyzer=@Analyzer(impl = IKAnalyzer.class)),
			@Field(name="paragraph", index=Index.YES, analyze=Analyze.YES, store=Store.NO, analyzer=@Analyzer(impl = WhitespaceAnalyzer.class))
	})
	public String getContent() {
		return content.toString();
	}
	void setContent(String content) {
		this.content = new StringBuilder();
		this.content.append(content);
	}
	/**
	 * Add a new paragraph to the content
	 * @param paragraph: A new paragraph
	 */
	public void addParagraph(String paragraph){
		this.content.append(paragraph).append(separator);
	}
	@Transient
	public String[] getParagraph(){
		return this.content.toString().split(separator);
	}
	
	
	@Column(nullable=false)
	@Field(index=Index.YES, analyze=Analyze.NO)
	@NumericField
	int getDeleted() {
		if(deleted == true){
			return 1;
		}else{
			return 0;
		}
	}	
	public void setDeleted(int deleted) {
		if(deleted == 1){
			this.deleted = true;
		}else{
			this.deleted = false;
		}
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	@Transient
	public boolean isDeleted(){
		return this.deleted;
	}
	
	@OneToMany(mappedBy="news")
	public Set<Remark> getRemarks() {
		return remarks;
	}
	void setRemarks(Set<Remark> remarks) {
		this.remarks = remarks;
	}
	public void addRemarks(Remark remark){
		remark.setNews(this);
		this.remarks.add(remark);
	}	

}
