import java.sql.*;

public class Database {
	private static final String URL = "jdbc:sqlite:data/testUsers.db";
	// testUsers:
	// SillKK -> 20/12/2012
	// Dorealipe -> Integer[]


	public static Connection connect() throws SQLException {
		return DriverManager.getConnection(URL);
	}

	/// Initiates the database, if none exists
	public static void initialize() {
		String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                email TEXT,
                password_hash TEXT,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """;

		try (Connection conn = connect();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public static void updateEmail(String username, String newEmail) {
		String sql = "UPDATE users SET email = ? WHERE username = ?";

		try (Connection conn = connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, newEmail);
			stmt.setString(2, username);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	static boolean updatePassword(String username, String newPassword) {
		String sql = "UPDATE users SET password_hash = ? WHERE username = ?";

		try (Connection conn = connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {



			stmt.setString(1, HashManager.hash(newPassword));
			stmt.setString(2, username);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}
	}

	public static void insertUser(String username, String email, String passwordHash) {
		String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

		try (Connection conn = connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setString(3, passwordHash);

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
							rs.getString("password_hash"),
							rs.getString("created_at")
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
		insertUser(user.username(),user.email(),user.passwordHash());
	}

	public static void insertUser(UserInfo info) {
		insertUser(info.username(),info.email(),info.passwordHash());
	}
}