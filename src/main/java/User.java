public class User {
	private UserInfo info;

	public static Boolean userExists(String username) {
		return Database.userExists(username);
	}

	public User(String name, String password) {
		this(name,null,password);
	}

	public User(String name, String email, String password) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, email, HashManager.hash(password), null);
		Database.insertUser(this.info);
		this.info = Database.findUser(name);
	}

	/// Used only in logging
	protected User(UserInfo info) {
		this.info = info;
	}



	public static User logIn(String username, String password) {
		if (!userExists(username)) {
			return null;
		}
		UserInfo found = Database.findUser(username);
		if (found == null) return null;
		if (!HashManager.verify(password, found.passwordHash())) {
			throw new SecurityException("Wrong password");
		}
		return new User(found);
	}

	public void updateEmail(String newEmail) {
		Database.updateEmail(this.info.username(), newEmail);
		this.info = Database.findUser(this.info.username());
	}
	public void updatePassword(String newPassword) {
		Database.updatePassword(this.info.username(), newPassword);
		this.info = Database.findUser(this.info.username());
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

}
