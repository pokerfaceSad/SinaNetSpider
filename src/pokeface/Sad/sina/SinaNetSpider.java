package pokeface.Sad.sina;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SinaNetSpider {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//设置原始节点
		User user = new User();
		#FIXME
		//填上要爬取的用户信息即可
		user.uid = "";
		user.username = "";
		user.url = "http://weibo.cn/"+user.uid;
		dbUtil.writeUserIntoDB(user);
		user = getSocialNetwork(user);
		dbUtil.writeUserMsgIntoDB(user);
		for(User follower:user.followers)
		{
			follower = getSocialNetwork(follower);
			System.out.println("正在将"+follower+"信息写入数据库");
			dbUtil.writeUserMsgIntoDB(follower);
		}
	}
	/**
	 * 爬取一个用户的信息、关注者信息及关系写入数据库
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void explore(User user) throws FileNotFoundException, IOException{
		List<User> followers = getAllFollowers("http://weibo.cn/"+user.uid);
		user.followers = followers;
		dbUtil.writeUserMsgIntoDB(user);
	}
	private static String[] getUidList(Document doc) {
		Elements es = doc.select("[name=uidList]");
		String uids = es.attr("value");
		String[] uidList = uids.split(",");
		return uidList;
	}
	/**
	 * 获取关注和粉丝刘表，并将isExplored成员变量更新为true
	 * @param user
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static User getSocialNetwork(User user) throws FileNotFoundException, IOException{
		user = getMsg(user);
		user.followers = getAllFollowers("http://weibo.cn/"+user.uid);
		user.isExploded = true;
//		user.fans = getAllFans("http://weibo.cn/"+user.uid);
		return user;
	}
	private static List<User> getAllFollowers(String URL) throws FileNotFoundException, IOException{
		List<User> followers = new ArrayList<User>();
		Properties pro = dbUtil.getProperties();
		String html = getText(pro.getProperty("cookie"),URL+"/follow");
		Document doc = Jsoup.parse(html);
		int Num = getNumOfPages(doc);
		StringBuffer aimURL = new StringBuffer();
		String[] uidList = null;
		User follower = null;
		for(int i=1;i<=Num;i++)
		{
			 aimURL.append(URL);
			 aimURL.append("/follow?page=");
			 aimURL.append(i);
			 html = getText(pro.getProperty("cookie"),aimURL.toString());
			 doc = Jsoup.parse(html);
			 uidList = getUidList(doc);
			 Elements es = doc.select("table");
			 int index = 0; //增强for循环中的索引
			 for(Element e:es)
			 {
				 follower = new User();
				 follower.url = e.child(0).child(0).child(0).child(0).attr("href");
				 follower.username = e.child(0).child(0).child(1).child(0).text();
				 follower.uid = uidList[index];
				 followers.add(follower);
				 index++;
			 }
			 aimURL.replace(0, aimURL.length(), "");
			 try {
				 System.out.println("等待中...");
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return followers;
	}
	
	/**
	 * 为避免大V粉丝多占用资源，暂不爬取粉丝
	 * @param url
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static List<User> getAllFans(String url) throws FileNotFoundException, IOException{
		List<User> fans = new ArrayList<User>();
		Properties pro = dbUtil.getProperties();
		String html = getText(pro.getProperty("cookie"),url+"/fans");
		Document doc = Jsoup.parse(html);
		int Num = getNumOfPages(doc);
		StringBuffer aimURL = new StringBuffer();
		String[] uidList = null;
		User fan = null;
		for(int i=1;i<=Num;i++)
		{
			 aimURL.append(url);
			 aimURL.append("/fans?page=");
			 aimURL.append(i);
			 html = getText(pro.getProperty("cookie"),aimURL.toString());
			 doc = Jsoup.parse(html);
			 uidList = getUidList(doc);
			 Elements es = doc.select("table");
			 int index = 0; //增强for循环中的索引
			 for(Element e:es)
			 {
				 fan = new User();
				 fan.url = e.child(0).child(0).child(0).child(0).attr("href");
				 fan.username = e.child(0).child(0).child(1).child(0).text();
				 fan.uid = uidList[index];
				 fans.add(fan);
				 index++;
			 }
			 aimURL.replace(0, aimURL.length(), "");
			 try {
				 System.out.println("等待中...");
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		return fans;
	}
	private static int getNumOfPages(Document doc) {
		Elements es = doc.select("[name=mp]");
		try {
			return new Integer(es.get(0).attr("value")).intValue(); //获取页数
		} catch (IndexOutOfBoundsException e) {
			return 1;
		}
	}
	/**
	 * 
	 * @param user
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static User getMsg(User user) throws FileNotFoundException, IOException{
		Pattern pSex = Pattern.compile(new String("性别:(.+)"));
		Pattern pLocation = Pattern.compile(new String("地区:(.+)"));
		Pattern pBirthday = Pattern.compile(new String("生日:(.+)"));
		
		Properties pro = dbUtil.getProperties();
		String html = getText(pro.getProperty("cookie"),"http://weibo.cn/"+user.uid+"/info");
		Document doc = Jsoup.parse(html);
		Element ele = doc.select("div[class=c]").get(2);
		Matcher m = pSex.matcher(ele.toString());
		if(m.find()) 
			user.sex = m.group(1);
		else 
			user.sex = "未填写";
		m = pLocation.matcher(ele.toString());
		if(m.find()) 
			user.location = m.group(1);
		else
			user.location = "未填写";
		m = pBirthday.matcher(ele.toString());
		if(m.find()) 
			user.birthday = m.group(1);
		else
			user.birthday = "未填写";
		return user;
	}
	public static String getText(String cookie,String URL){
    	HttpClient closeableHttpClient = HttpClients.createDefault();
    	String responseBody = ""; 
        RequestConfig requestConfig = RequestConfig.custom()  
        	    .setConnectionRequestTimeout(10000).setConnectTimeout(10000)  
        	    .setSocketTimeout(10000).build();  
        HttpGet httpGet = new HttpGet(URL);  
        httpGet.setConfig(requestConfig);    
        httpGet.addHeader("User-Agent", "spider");
        httpGet.addHeader("Cookie", cookie);

        HttpEntity httpEntity = null;
        
        System.out.println(httpGet.getRequestLine());  
        try {  
//            responseBody= new String(closeableHttpClient.execute(httpGet, responseHandler).getBytes(),"GBK");  
            //执行get请求  
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
            //获取响应消息实体  
            HttpEntity entity = httpResponse.getEntity();  
            //响应状态  
//            responseBody = closeableHttpClient.execute(httpGet, responseHandler);
            StringBuffer sb = new StringBuffer();
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
            	BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
            	for(String str=null;(str=br.readLine())!=null;)
            	{
            		sb.append(str);
            	}
            }
            responseBody = sb.toString();
//            System.out.println(responseBody);
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            httpGet.abort();  
        }  
    	return responseBody;
    }
	
	
	
}
