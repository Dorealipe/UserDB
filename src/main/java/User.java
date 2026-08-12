import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Objects;

public class User {
	private static final ArrayList<User> REGISTERED_USERS = new ArrayList<>();
	private static final SecretKey SECRET_KEY = EncryptionManager.generateKey();
	private static final Cipher CIPHER = EncryptionManager.generateCipher();

	private final UserInfo info;

	public static Boolean userExists(String username) {
		return REGISTERED_USERS.stream().anyMatch(u -> Objects.equals(u.info.username(), username));
	}

	public User(String name, String password) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, EncryptionManager.encrypt(password,SECRET_KEY, CIPHER), null);


		REGISTERED_USERS.add(this);
	}

	public User(String name, String password, String email) {
		if (userExists(name)) {
			throw new ExceptionInInitializerError(String.format("User with username \"%s\" already exists.", name));
		}
		this.info = new UserInfo(name, EncryptionManager.encrypt(password,SECRET_KEY, CIPHER), email);


		REGISTERED_USERS.add(this);
	}

	public static User logIn(String username, String password) {
		if (!userExists(username)) {
			return new User(username, password);
		}
		User foundUser = REGISTERED_USERS.stream()
				.filter(u -> Objects.equals(u.info.username(), username)
		)
		.toList().getFirst();
		if (!Objects.equals(EncryptionManager.decrypt(foundUser.info.encryptedPassword(), SECRET_KEY, CIPHER), password)) {
			throw new SecurityException("Wrong password");
		}
		return foundUser;
	}

	public String emailType() {
		return info.emailType();
	}



	void printUserInfo() {
		System.out.println(info);
	}

}
