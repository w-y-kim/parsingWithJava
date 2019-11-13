package c.parsing;

import javax.swing.text.html.parser.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
class Parser extends HTMLEditorKit.ParserCallback {

	private boolean inAnchor = false;
	public static ArrayList list = new ArrayList<HashMap>();
	public static HashMap<String, Object> set = new HashMap<String, Object>();

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (t.equals(HTML.Tag.A)) {
			inAnchor = true;
			set.put("text", "");
			set.put("title",
					(String) a.getAttribute(HTML.Attribute.TITLE) != null
							? (String) a.getAttribute(HTML.Attribute.TITLE)
							: "");
			set.put("href",
					(String) a.getAttribute(HTML.Attribute.HREF) != null ? (String) a.getAttribute(HTML.Attribute.HREF)
							: "");
		}
	}

	public void handleEndTag(HTML.Tag t, int pos) {
		if (t.equals(HTML.Tag.A)) {
			inAnchor = false;
			list.add(set);
			set = new HashMap<String, Object>();

		}
	}

	public void handleText(char[] data, int pos) {
		if (inAnchor) {
			set.put("text", new String(data));

		}
	}

	@SuppressWarnings("unchecked")
	private static Map getCombination(String[] list) {
		Map result = new HashMap();
		int count = list.length;
		int num = 0;
		for (int i = 0; i < count - 1; i++) {
			for (int j = i + 1; j < count; j++) {
				num++;
				result.put(num, list[i] + "." + list[j]);
			}
		}
		return result;
	}

	public static void removeDuplicate() {

		ArrayList combinationList = new ArrayList();
		ArrayList arr11 = new ArrayList();
		ArrayList arr22 = new ArrayList();
		
		List<ArrayList<String>> combs = new ArrayList<>(3);
		List<ArrayList<String>> combs2 = new ArrayList<>(3);
		
		combs.add(new ArrayList());
		combs.add(new ArrayList());
		combs.add(new ArrayList());		
		
		combs2.add(new ArrayList());
		combs2.add(new ArrayList());
		combs2.add(new ArrayList());
		
		// make combination by values
		for (Object e : list) {
			HashMap map= (HashMap<?, ?>) e;
			String key1 = (String) map.get("text");
			String key2 = (String) map.get("title");
			String key3 = (String) map.get("href");

			String[] slst = { key1, key2, key3 };
			combinationList.add(getCombination(slst));
		}

		for (int i = 0; i < combinationList.size(); i++) {
			for (int j = 0; j < 3; j++) {
				((ArrayList) combs.get(j)).add(((HashMap<String, Object>) combinationList.get(i)).get(j + 1));
			}

		}

		for (int i = 0; i < combs.size(); i++) {
			ArrayList tempList = combs.get(i);
			for (int j = 0; j < tempList.size(); j++) {
				if (!(combs2.get(i)).contains(tempList.get(j))) {
					((ArrayList) combs2.get(i)).add(tempList.get(j));
				} else {
					arr11.add(j);
				}
			}
		}

		for (int i = 0; i < arr11.size(); i++) {
			if (!arr22.contains(arr11.get(i))) {
				arr22.add(arr11.get(i));
			}
		}
		arr22.sort(null);

		int listLen = arr22.size();
		int i = 0;
		while (i < listLen) {
			int ind = (Integer) arr22.get(i) - i;
			list.remove(ind);
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws java.lang.Exception {

		// (1) http connection get(200)
		// (2) downlaod html
		Download down = new Download();
		down.downloadUrl("https://qiita.com/tags?page=1");

		// (3) parsing
		System.out.println("(3) parsing");
		ParserDelegator pd = new ParserDelegator();
		String path = "./copy.html";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
		pd.parse(br, new Parser(), false);

		// (4) remove duplicate set
		System.out.println("(4) remove duplicate set");
		removeDuplicate();

		// (5) sort anchor text as a key
		System.out.println("(5) sort anchor text as a key");
		Collections.sort(list, new Comparator<HashMap<String, String>>() {
			public int compare(HashMap<String, String> map1, HashMap<String, String> map2) {
				return map1.get("text").compareTo(map2.get("text"));
			}
		});

		// (6) output csv
		System.out.println("(6) output csv");
		Writer fstream = null;
	    fstream = new OutputStreamWriter(new FileOutputStream("./anchorParsing.csv"), StandardCharsets.UTF_8);
		PrintWriter pw = new PrintWriter(new BufferedWriter(fstream));
		for (Object e : list) {
			String line = ((HashMap) e).get("text") + "," + ((HashMap) e).get("href");
			pw.print(line);
			pw.println();
		}
		System.out.println("...CSV extracted " + !pw.checkError());

		pw.close();
		br.close();

	}
}