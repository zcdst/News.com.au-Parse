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



		for (Article x: articleDB) {
			System.out.println(x.title);
		}





	}



}