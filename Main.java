import java.util.*;
import java.net.*;
import java.io.*;
import java.util.zip.*;


public class Main {

	private static ArrayList<Article> articleDB;	// store articles

	public static void main(String[] args) {

		int count = 0;
		URL url;
		Scanner sc = null;
		InputStream is = null;

		loadArticleDB();

		try {	// get home page html
			url = new URL("http://news.com.au/");
			is = url.openStream();
			sc = new Scanner(is, "utf-8");
		} catch (Exception e) {
			System.out.println("Couldn't access news.com.au!");
		}

		while(sc.hasNext()) {				// populate database
			String x = sc.next();
			if (x.startsWith("href=\"/") && x.length() >= 60 && !(x.contains("/comments-"))) {
				String y = "http://news.com.au" + x.substring(x.indexOf("\"") + 1, x.lastIndexOf("\""));
				boolean dupe = false;
				for (Article z : articleDB) {
					if ((z.link).equals(y)) {
						dupe = true;
					}
				}
				if (dupe == false) {
					System.out.println("Adding: " + y);
					count++;
					articleDB.add(new Article(y, Page.parse(y)));
				}
			}
		}

		System.out.println("Added: " + count + " articles" );
		System.out.println("Total number of articles in database: " + articleDB.size());


		saveArticleDB();

	}


		private static void loadArticleDB() {	// load db if exists
			try {
				ObjectInputStream ois = new ObjectInputStream(new InflaterInputStream(new FileInputStream("article-db.ser")));
				articleDB = (ArrayList<Article>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				System.out.println("Couldn't find article db, creating new one!");
				articleDB = new ArrayList<Article>();
			}
		}



		private static void saveArticleDB() {	// save db
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new DeflaterOutputStream(new FileOutputStream("article-db.ser")));
				oos.writeObject(articleDB);
				oos.close();
			} catch (Exception e) {
				System.out.println("Error writing article db!");
			}
		}

}