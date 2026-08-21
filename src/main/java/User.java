import java.util.List;

public class User {
	private UserInfo info;

	public static Boolean userExists(String username) {
		return Database.Users.userExists(username);
	}

	public User(String name, String password) {
		this(name,null,password);
	}

	public User(String name, String email, String password) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, email, HashManager.hash(password), null);
		Database.Users.insert(this.info);
		this.info = Database.Users.findUser(name);
	}

	/// Used only in logging
	protected User(UserInfo info) {
		this.info = info;
	}



	public static User logIn(String username, String password) {
		if (!userExists(username)) {
			return null;
		}
		UserInfo found = Database.Users.findUser(username);
		if (found == null) return null;
		if (!HashManager.verify(password, found.passwordHash())) {
			throw new SecurityException("Wrong password");
		}
		return new User(found);
	}

	public void updateEmail(String newEmail) {
		Database.Users.updateEmail(this.info.username(), newEmail);
		this.info = Database.Users.findUser(this.info.username());
	}
	public void updatePassword(String newPassword) {
		Database.Users.updatePassword(this.info.username(), newPassword);
		this.info = Database.Users.findUser(this.info.username());
	}

	public String username() {return info.username();}
	public String email() {return info.email();}
	public String passwordHash() {return info.passwordHash();}
	public String emailType() {
		return info.emailType();
	}
	public String createdAt() {return info.createdAt();}



	void printUserInfo() {
		System.out.println(info);
	}

	public void addNote(String content, boolean isTask) {
		Database.Notes.insert(this.username(), content, isTask);
	}

	public List<NoteInfo> getNotes() {
		return Database.Notes.findByUser(this.username());
	}

	public List<TimerInfo> getTimers() { return Database.Timers.findByUser(this.username()); }

}
