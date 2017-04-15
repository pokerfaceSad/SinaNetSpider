package pokeface.Sad.sina;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;



public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException{
		Properties pro = dbUtil.getProperties();
		String html = SinaNetSpider.getText(pro.getProperty("cookie"), "http://weibo.com/");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/Test1.html")));
		
		Element docs = Jsoup.parse(html);
		bw.write(docs.toString());
		bw.flush();
		bw.close();
	}
}
