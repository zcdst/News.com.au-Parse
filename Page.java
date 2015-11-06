import java.util.*;
import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Page {


	static String[] remove = {"href=", "dir=", "lang=",	// remove entire entry containing this
	 "shockwave-flash", "http", "stylefont", "data-", "font-",
	  "-wrap", "margin-", "-source", "twitter-tweet", "color", "text-", "nowrap", "text-",
	   "serif", "px"};

	static String[] replace = {"&#.+;", "class=\".+?\">", "</?[pib]>?", "</?a>?",	// replace matches with empty string
	 "</?strong>", "</?span>?", "&apos;", "[^\\w-'’]"};


	public static ArrayList<String> parse(String link) {

			ArrayList<String> wordlist = new ArrayList<String>();	// every word in an article
			URL url;
			InputStream is = null;
			Scanner sc = null;
			StringBuilder str = new StringBuilder();	// store entire article as a string

			try {
				url = new URL(link);
				is = url.openStream();
				sc = new Scanner(is, "utf-8");
			} catch (Exception e) {
				System.out.println("Couldn't open page");
			}

			String tmp;
			while (sc.hasNextLine()) {
				tmp = sc.nextLine();
				if (tmp.contains("els.length")) {	// article ends upon reaching this string
					break;
				} else {
					str.append(tmp);
				}
			}

			String all = str.toString();	// entire article as string


			Pattern p = Pattern.compile("<p.+?p>");	// only match text wrapped in <p> tags
			Matcher m = p.matcher(all);
			str = new StringBuilder();
			while (m.find()) {
				str.append(m.group());
			}
			all = str.toString();



			sc = new Scanner(all);	// advance to begninning of article
			while (sc.hasNext()) {
				tmp = sc.next();
				if (tmp.contains("<p><strong>")) {	// article begins with this string
					break;
				}
			}

			thang: while (sc.hasNext()) {	
				String x = sc.next();
				for (String z : remove) {	// don't include removed words
					if (x.contains(z)) {
						continue thang;
					}
				}

				for (String z : replace) {	// replace strings
					x = x.replaceAll(z, "");
				}

				x = x.replaceAll("&#x.+;", "");	// might not need this anymore?
				String[] t = x.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"); // camelcase
				
				for (String y : t) {		// populate word list, exclude empty strings
					if (y.length() > 0) {
						wordlist.add(y.toUpperCase());
					}
				}

			}

			return wordlist;



}

}