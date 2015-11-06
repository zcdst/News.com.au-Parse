import java.util.*;
import java.net.*;
import java.io.*;
import java.util.zip.*;


public class Test{

	private static ArrayList<Article> articleDB = new ArrayList<Article>();	// store articles





	public static void main(String[] args) throws Exception {


			ObjectInputStream ois = new ObjectInputStream(new InflaterInputStream(new FileInputStream("article-db.ser")));
			articleDB = (ArrayList<Article>) ois.readObject();
			ois.close();



		for (Article art: articleDB) {
			ArrayList<String> list = art.article;
			ArrayList<String> newlist = new ArrayList<String>();

			for (String s : list) {
				if (s.equals("SOURCEEM") || s.equals("CLASSTWITTER-TWEET")) {
				} else {
					newlist.add(s);
				}
			}
			art.article = newlist;
		}



		ObjectOutputStream oos = new ObjectOutputStream(new DeflaterOutputStream(new FileOutputStream("article-db.ser")));
		oos.writeObject(articleDB);
		oos.close();



	}



}