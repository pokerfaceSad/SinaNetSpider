package pokeface.Sad.sina;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


public class dbUtil {
	
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static Connection getConn() throws FileNotFoundException, IOException, SQLException  {
		Properties pro = new Properties();
		Connection conn = null;
		pro = getProperties();
		conn = DriverManager.getConnection(pro.getProperty("url"),pro.getProperty("user"),pro.getProperty("password"));
		return conn;
	}
	public static Properties getProperties() throws FileNotFoundException, IOException{
		
		Properties pro = new Properties();
//		String classes_path = (new dbUtil()).getClass().getClassLoader().getResource("").getPath();
//		classes_path = URLDecoder.decode(classes_path,"UTF-8");
//		pro.load(new FileInputStream(classes_path+"/db.properties"));
		pro.load(new FileInputStream("db.properties"));
		return pro;
			
	}
	private static void beginTransaction(Connection conn){
		if(conn!=null){
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	private static void commitTransaction(Connection conn){
		if(conn!=null){
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	private static void rollback(Connection conn){
		if(conn!=null){
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	static void close(PreparedStatement ps, Connection conn,ResultSet rs) {

		if(rs!=null)
		{
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps!=null)
		{
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getDate() {
		Properties pro = new Properties();
		String date = null;
		try {
			pro = getProperties();
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(pro.getProperty("dateFormat"));
			date = sdf.format(d);
			
			return date;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 将User的个人信息和关注关系和关注�?的信息写入数据库
	 * @return
	 */
	public static boolean writeUserMsgIntoDB(User user){
		updateUserMsgIntoDB(user);
		boolean insertSuccess = true;
		for(User follower:user.followers)
		{
			if(!writeUserIntoDB(follower)) 
			{
				insertSuccess = false;
				System.out.println("写入数据库失败："+follower);
			}	
			if(!writeRelationIntoDB(user.uid, follower.uid)) 
			{
				insertSuccess = false;
				System.out.println("写入数据库失败："+follower);
			}	
		}
		/*
		for(User fan:user.fans)
		{
			if(!writeUserIntoDB(fan)) 
			{
				insertSuccess = false;
				System.out.println("写入数据库失败："+fan);
			}	
			if(!writeRelationIntoDB(fan.uid,user.uid)) 
			{
				insertSuccess = false;
				System.out.println("写入数据库失败："+fan);
			}	
		}
		*/
		return insertSuccess;
	}
	/**
	 * 将User信息写进数据库
	 * @param user
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public static boolean writeUserIntoDB(User user){
		Properties pro = null;
		Connection conn = null;
		PreparedStatement ps = null;
		boolean insertSuccess = false;
		try {
			conn = dbUtil.getConn();
			pro = dbUtil.getProperties();
			dbUtil.beginTransaction(conn);
			if(user.isExploded)
			{
				ps = conn.prepareStatement(pro.getProperty("insert_user_explored"));
				ps.setString(1, user.uid);
				ps.setString(2, user.username);
				ps.setString(3, user.url);
				ps.setString(4, user.sex);
				ps.setString(5, user.location);
				ps.setString(6, user.birthday);
			}
			else{
				ps = conn.prepareStatement(pro.getProperty("insert_user"));
				ps.setString(1, user.uid);
				ps.setString(2, user.username);
				ps.setString(3, user.url);
			}
			if(ps.executeUpdate()==1)
			{
				insertSuccess = true;
				dbUtil.commitTransaction(conn);
			}else {
				dbUtil.rollback(conn);
			}
		}catch (MySQLIntegrityConstraintViolationException e) {
			
			insertSuccess = true;
			System.out.println("已存在该数据"+user.username);
			dbUtil.rollback(conn);
		} 
		catch (Exception e) {
			dbUtil.rollback(conn);
			e.printStackTrace();
		}finally{
			dbUtil.close(ps, conn, null);
		}
		return insertSuccess;
	}
	public static boolean updateUserMsgIntoDB(User user){
		Properties pro = null;
		Connection conn = null;
		PreparedStatement ps = null;
		boolean updateSuccess = false;
		try {
			conn = dbUtil.getConn();
			pro = dbUtil.getProperties();
			dbUtil.beginTransaction(conn);
			ps = conn.prepareStatement(pro.getProperty("update_user_explored"));
			ps.setString(1, user.sex);
			ps.setString(2, user.location);
			ps.setString(3, user.birthday);
			ps.setString(4, user.uid);
			if(ps.executeUpdate()==1)
			{
				updateSuccess = true;
				dbUtil.commitTransaction(conn);
			}else {
				dbUtil.rollback(conn);
			}
		}catch (Exception e1) {
			dbUtil.rollback(conn);
			e1.printStackTrace();
		}finally{
			dbUtil.close(ps, conn, null);
		}
		return updateSuccess;
	}
	private static boolean writeRelationIntoDB(String fanUid,String followederUid ) {
		Properties pro = null;
		Connection conn = null;
		PreparedStatement ps = null;
		boolean insertSuccess = false;
		try {
			conn = dbUtil.getConn();
			pro = dbUtil.getProperties();
			dbUtil.beginTransaction(conn);
			ps = conn.prepareStatement(pro.getProperty("intsert_relation"));
			ps.setString(1, fanUid);
			ps.setString(2, followederUid);
			if(ps.executeUpdate()==1)
			{
				insertSuccess = true;
				dbUtil.commitTransaction(conn);
			}else {
				dbUtil.rollback(conn);
			}
		} catch (Exception e) {
			dbUtil.rollback(conn);
			e.printStackTrace();
		}finally{
			dbUtil.close(ps, conn, null);
		}
		return insertSuccess;
	}

}
