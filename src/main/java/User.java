import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Objects;

public class User {
	private static final SecretKey SECRET_KEY = EncryptionManager.generateKey();
	private static final Cipher CIPHER = EncryptionManager.generateCipher();

	private final UserInfo info;



	public static Boolean userExists(String username) {
		return false;
	}

	public User(String name, String password) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, null, EncryptionManager.encrypt(password,SECRET_KEY, CIPHER));
		Database.insertUser(this.info);
	}

	public User(String name, String password, String email) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, email, EncryptionManager.encrypt(password,SECRET_KEY, CIPHER));
		Database.insertUser(this.info);
	}

	/// Used only in logging
	protected User(UserInfo info) {
		this.info = info;
	}



	public static User logIn(String username, String password) {
		if (!userExists(username)) {
			return new User(username, password);
		}
		UserInfo found = Database.findUser(username);
		if (found==null) return null;
		if (!Objects.equals(EncryptionManager.decrypt(found.encryptedPassword(), SECRET_KEY, CIPHER), password)) {
			throw new SecurityException("Wrong password");
		}
		return new User(found);
	}

	public String username() {return info.username();}
	public String email() {return info.email();}
	public byte[] encryptedPassword() {return info.encryptedPassword();}
	public String emailType() {
		return info.emailType();
	}



	void printUserInfo() {
		System.out.println(info);
	}

}
