/*
	若是出现网络异常导致爬取中断，可运行此main方法从数据库中读取尚未爬取的用户继续爬取
*/
package pokeface.Sad.sina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetData {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		
		//获取原始节点的好友列表
		#FIXME
		//填上要爬取的用户信息即可
		User user = new User();
		user.uid = "";
		user.username = "";
		user.url = "http://weibo.cn/"+user.uid;
		user = SinaNetSpider.getSocialNetwork(user);
		int followerNum = user.followers.size();
		
		//获取数据库中已经explore的好友的uid列表
		Connection conn = dbUtil.getConn();
		PreparedStatement ps = conn.prepareStatement("select uid from user where isexplored='true'");
		ResultSet rs = ps.executeQuery();
		List<String> uidList = new ArrayList<String>();
		while(rs.next())
		{
			uidList.add(rs.getString(1));
			System.out.println(rs.getString(1));
		}
		dbUtil.close(ps, conn, rs);
		
		
		while(uidList.size()<=followerNum){
			
			try{
				
				/*int index = 0;
				List<Integer> removeList = new ArrayList<Integer>();
				for(User follower:user.followers)
				{
					System.out.println(follower.username);
					if(uidList.contains(follower.uid))
					{
						System.out.println("删除"+user.followers.get(index));
						removeList.add(index);
					}
					index++;
				}
				for(int index1:removeList)
				{
					System.out.println(index1);
					System.out.println(user.followers.get(index1).username);
					user.followers.remove(index1);
				}
				System.out.println(user.followers.size());
				for(User follower:user.followers)
				{
					System.out.println(follower.username);
				}
				*/
				
				Iterator<User> it = user.followers.iterator();
				while(it.hasNext())
				{
					User follower = it.next();
					String uid = follower.uid;
					if(uidList.contains(uid))
					{
						it.remove();
					}
				}
				for(User follower:user.followers)
				{
					follower = SinaNetSpider.getSocialNetwork(follower);
					System.out.println("正在将"+follower+"信息写入数据库");
					dbUtil.writeUserMsgIntoDB(follower);
				}
				
			}catch (IndexOutOfBoundsException e) {
				System.err.println("网络异常");
			}
			
			conn = dbUtil.getConn();
			ps = conn.prepareStatement("select uid from user where isexplored='true'");
			rs = ps.executeQuery();
			uidList = new ArrayList<String>();
			while(rs.next())
			{
				uidList.add(rs.getString(1));
				System.out.println(rs.getString(1));
			}
			dbUtil.close(ps, conn, rs);
			
	}
			
	}

}
