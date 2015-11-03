import java.time.LocalDateTime;
import java.util.*;

public class Article implements java.io.Serializable {

	public String link;	// link to page
	public LocalDateTime time;	// time page was accessed
	public String category;	// news category
	public ArrayList<String> article;	// list of words in article
	public int wordcount;	// number of words
	public String title;

	public Article(String link, ArrayList<String> article) {
		this.link = link;
		this.time = LocalDateTime.now();
		this.category = link.substring(19, link.indexOf('/', 19));
		this.article = article;
		this.wordcount = article.size();
		String tmp = link.substring(0, link.lastIndexOf('/'));
		this.title = tmp.substring(tmp.lastIndexOf('/') + 1, tmp.length()).replace("-", " ").toUpperCase();
	}

}