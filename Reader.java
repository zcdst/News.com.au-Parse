import java.util.*;
import java.net.*;
import java.io.*;
import java.util.zip.*;

public class Reader {

	private static ArrayList<Article> articleDB;	// articles, loaded from file


	public static void main(String[] args) {


		HashSet<String> stopwords = new HashSet<String>();	// load stopwords.txt

		int travel = 0;
		int technology = 0;
		int lifestyle = 0;
		int sport = 0;
		int entertainment = 0;
		int finance = 0;
		int national = 0;
		int world = 0;
		int other = 0;

		boolean spread = false;


		try {	// load articles from file
			ObjectInputStream ois = new ObjectInputStream(new InflaterInputStream(new FileInputStream("article-db.ser")));
			articleDB = (ArrayList<Article>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println("Can't find articles db!");
			System.exit(0);
		}


		try {	// load stop words from file
			Scanner sc = new Scanner(new File("stopwords.txt"));
			while (sc.hasNext()) {
				stopwords.add(sc.next().toUpperCase());
			}
		} catch (Exception e) {
			System.out.println("No stopwords file!");
		}


		if (args.length > 0) {
			if (args[0].equals("-all")) {	// ignore stop words, print everything
				stopwords.clear();
			} else if (args[0].equals("-spread")) {	// show spread of words across articles
				spread = true;
			} else if (args[0].equals("-check")) {	// print links to articles for a given word
				String s = args[1].toUpperCase();
				for (Article a : articleDB) {
					int hits = 0;
					ArrayList<String> w = a.article;
					for (String z : w) {
						if (z.equals(s)) {
							hits++;
						} 
					}
					if (hits > 0) {
						System.out.println("\n" + hits + " Hit(s) for " + s + ": \n" + a.link);
					}
				}
				return;
			} else if (args[0].equals("-list")) {	// show titles and IDS of all articles in given category
				String filter = "";
				if (args.length > 1) {
					filter = args[1];
				}
				int count = 0;
				for (Article a : articleDB) {
					if (a.category.contains(filter)) {
						System.out.println("ID: " + count + " " + a.title);
					}
					count++;
				}
				return;
			} else if (args[0].equals("-get") && args.length > 1) {	// print article with given ID
				ArrayList<String> list = articleDB.get(Integer.parseInt(args[1])).article;
				System.out.println();
				for (String z : list) {
					System.out.print(z + " ");
				}
				System.out.println("\n\nLink: " + articleDB.get(Integer.parseInt(args[1])).link);
				return;
			} else if (args[0].equals("-find") && args.length > 1) {
				String tmp = args[1].toUpperCase();
				int count = 0;
				for (Article x : articleDB) {
					if (x.title.contains(tmp)) {
						System.out.println("ID: " + count + " " + x.title);
					}
					count++;
				}
				return;
			} else {
				System.out.println("\nCommands:");
				System.out.println("-all : Ignore stop word list, print everything");
				System.out.println("-spread : Show the number of articles in which each word appears");
				System.out.println("-check <word> : Shows links to articles containing given word, in addition to number of uses");
				System.out.println("-list <category> : show titles and IDs of all articles in a given category");
				System.out.println("-find <word> : list articles with titles containing given word");
				System.out.println("-get <id> : Print article with given ID\n");

				return;
			}
		}






		MyComparator comp;	// for sorting
		HashMap<String,ArrayList<Integer>> wordmap = new HashMap<String,ArrayList<Integer>>();	// key is the word, value is: 0: number of times that word is encountered in the db, 1: number of articles containing that word
		TreeMap<String,ArrayList<Integer>> sortedmap;	// sorted by count

		for (Article a : articleDB) {
			String cat = a.category;
			if (cat.equals("travel")) {	// count categories
				travel++;
			} else if (cat.equals("technology")) {
				technology++;
			} else if (cat.equals("lifestyle")) {
				lifestyle++;
			} else if (cat.equals("sport")) {
				sport++;
			} else if (cat.equals("entertainment")) {
				entertainment++;
			} else if (cat.equals("finance")) {
				finance++;
			} else if (cat.equals("national")) {
				national++;
			} else if (cat.equals("world")) {
				world++;
			} else {
				other++;
			}

			HashSet<String> tmp = new HashSet<String>();
			for (String x : a.article) {	// fill map with words and counts, ignore short and stop words
				if (x.length() < 3 || stopwords.contains(x)) {
					continue;
				}
				if (wordmap.containsKey(x)) {
					ArrayList<Integer> z = wordmap.get(x);
					int num = z.get(0) + 1;
					z.set(0, num);
					if (!(tmp.contains(x))) {
						tmp.add(x);
						z.set(1, z.get(1) + 1);
					}
					wordmap.put(x, z);
				} else {
					ArrayList<Integer> z = new ArrayList<Integer>();
					z.add(0, 1);
					z.add(1, 1);
					tmp.add(x);
					wordmap.put(x, z);
				}

			}
		}

		comp = new MyComparator(wordmap, spread);
		sortedmap = new TreeMap<String,ArrayList<Integer>>(comp);
		sortedmap.putAll(wordmap);			// sorted



		for (Map.Entry<String,ArrayList<Integer>> x : sortedmap.entrySet()) {	// print keys and values
			if (spread) {
				int n = x.getValue().get(1);
				System.out.println(x.getKey() + ": " + n + " articles (" + (int) (n * 100.0 / articleDB.size()) + "%)");
			} else {
				System.out.println(x.getKey() + ": " + x.getValue().get(0));
			}

		}


		System.out.println();	// stats
		printStats("Travel", travel);
		printStats("Technology", technology);
		printStats("Lifestyle", lifestyle);
		printStats("Sport", sport);
		printStats("Entertainment", entertainment);
		printStats("Finance", finance);
		printStats("National", national);
		printStats("World", world);
		printStats("Other", other);
		System.out.println("\nTotal number of articles: " + articleDB.size());
		System.out.println("Total number of unique words: " + sortedmap.size());

	}




	private static void printStats(String s, int k) {	// category stats
		System.out.println(s + ": " + k + " articles " + "(" + ((int)((double) k / articleDB.size() * 100)) + "%)");
	}


}



class MyComparator implements Comparator<Object> {

    Map map;
    boolean spread;

    public MyComparator(Map map, boolean spread) {
        this.map = map;
        this.spread = spread;
    }

    public int compare(Object key1, Object key2) {
    	ArrayList<Integer> first = (ArrayList<Integer>) map.get(key1);
    	ArrayList<Integer> second = (ArrayList<Integer>) map.get(key2);
    	int q = 0;
    	if (spread) {	// sort by spread value if enabled
    		q = 1;
    	}
        Integer val1 = first.get(q);
        Integer val2 = second.get(q);
        if (val1 < val2) {
            return -1;
        } else {
            return 1;
        }
    }
}