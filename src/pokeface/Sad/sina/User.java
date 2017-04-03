package pokeface.Sad.sina;

import java.util.List;

public class User {
	String username;
	String url;
	String uid;
	List<User> followers; //关注者列表
	List<User> fans; //粉丝列表
	Boolean isExploded  = false;
	String sex;
	String location;
	String birthday;
	@Override
	public String toString() {
		return "User [username=" + username + ", url=" + url + ", uid=" + uid
				+ "]";
	}
}
