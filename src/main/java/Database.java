import java.sql.*;

public class Database {
	private static final String URL = "jdbc:sqlite:data/users.db";


	public static Connection connect() throws SQLException {
		return DriverManager.getConnection(URL);
	}

	///
	public static void initialize() {
		String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                email TEXT,
                encrypted_password BLOB
            )
        """;

		try (Connection conn = connect();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public static void insertUser(String username, String email, byte[] encryptedPassword) {
		String sql = "INSERT INTO users (username, email, encrypted_password) VALUES (?, ?, ?)";

		try (Connection conn = connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setBytes(3, encryptedPassword);

			stmt.executeUpdate(); // executeUpdate() = INSERT/UPDATE/DELETE

		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public static UserInfo findUser(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";

		try (Connection conn = connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) { // true when username = info.username
					return new UserInfo(
							rs.getString("username"),
							rs.getString("email"),
							rs.getBytes("encrypted_password")
					);
				}
				return null;
			}

		} catch (SQLException e) {
			System.err.println(e);
			return null;
		}
	}

	public static boolean userExists(String username) {
		return findUser(username) != null;
	}

	public static void insertUser(User user) {
		insertUser(user.username(),user.email(),user.encryptedPassword());
	}

	public static void insertUser(UserInfo info) {
		insertUser(info.username(),info.email(),info.encryptedPassword());
	}
}