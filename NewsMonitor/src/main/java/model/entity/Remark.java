package model.entity;

import java.util.Date;

import javax.persistence.*;

/**
 * Remark bean <br>
 * 
 * @author skw <br>
 */
@Entity
public class Remark {
	
	private int id; //identifier
	private String observer; //anonymity use Website default name instead
	private String content;
	private Date time; //when the remark was published
	private boolean deleted = false; //whether has been deleted on the original Website
	private News news; //foreign key of news
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(nullable=false, updatable=false)
	public String getObserver() {
		return observer;
	}
	public void setObserver(String observer) {
		this.observer = observer;
	}
	
	@Column(nullable=false, updatable=false, columnDefinition="TEXT")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(nullable=false, updatable=false, columnDefinition="TIMESTAMP")
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	@Column(nullable=false)
	public int getDeleted() {
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
	@Transient
	public boolean isDeleted(){
		return this.deleted;
	}
	
	@ManyToOne
	@JoinColumn
	public News getNews() {
		return news;
	}
	public void setNews(News news) {
		this.news = news;
	}

}
