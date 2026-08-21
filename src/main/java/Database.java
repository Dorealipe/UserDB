import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


public class Database {
	private static final String URL = "jdbc:sqlite:data/testUsers.db";
	// testUsers:
	// SillKK -> 20/12/2012
	// Dorealipe -> Integer[]


	public static Connection connect() throws SQLException {
		return DriverManager.getConnection(URL);
	}

	public static void dropTable(String table) {
		String sql = String.format("DROP TABLE %s",table);
		try (Connection conn = connect();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	/// <code>Users</code> module, implements <code>users</code> table
	public static class Users {
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

		public static void updatePassword(String username, String newPassword) {
			String sql = "UPDATE users SET password_hash = ? WHERE username = ?";

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, HashManager.hash(newPassword));
				stmt.setString(2, username);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static void insert(String username, String email, String passwordHash) {
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

		public static void insert(User user) {
			insert(user.username(),user.email(),user.passwordHash());
		}

		public static void insert(UserInfo info) {
			insert(info.username(),info.email(),info.passwordHash());
		}
	}
	/// <code>Notes</code> module, implements <code>notes</code> table
	public static class Notes {
		public static void initialize() {
			String sql = """
        CREATE TABLE IF NOT EXISTS notes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            content TEXT NOT NULL,
            is_task INTEGER NOT NULL DEFAULT 0,
            done INTEGER NOT NULL DEFAULT 0,
            created_at TEXT DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (username) REFERENCES users(username)
        )
    """;

			try (Connection conn = connect();
			     Statement stmt = conn.createStatement()) {
				stmt.execute(sql);
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static void insert(String username, String content, boolean isTask) {
			String sql = "INSERT INTO notes (username, content, is_task) VALUES (?, ?, ?)";

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, username);
				stmt.setString(2, content);
				stmt.setInt(3, isTask ? 1 : 0);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static void setDone(int id, boolean done) {
			String sql = "UPDATE notes SET done = ? WHERE id = ?";

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, done ? 1 : 0);
				stmt.setInt(2, id);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static void deleteNote(int id) {
			String sql = "DELETE FROM notes WHERE id = ?";

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static List<NoteInfo> findByUser(String username) {
			String sql = "SELECT * FROM notes WHERE username = ? ORDER BY id";
			List<NoteInfo> notes = new ArrayList<>();

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, username);

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						notes.add(new NoteInfo(
								rs.getInt("id"),
								rs.getString("username"),
								rs.getString("content"),
								rs.getInt("is_task") == 1,
								rs.getInt("done") == 1,
								rs.getString("created_at")
						));
					}
				}
			} catch (SQLException e) {
				System.err.println(e);
			}
			return notes;
		}
	}
	/// <code>Timers</code> module, implements <code>timers</code> table
	public static class Timers {
		public static void initialize() {
			String sql = """
        CREATE TABLE IF NOT EXISTS timers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            name TEXT NOT NULL,
            duration_seconds INTEGER NOT NULL,
            start_time TEXT DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (username) REFERENCES users(username)
        )
    """;

			try (Connection conn = connect();
			     Statement stmt = conn.createStatement()) {
				stmt.execute(sql);
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		public static int insert(String username, String name, long durationSeconds) {
			String sql = "INSERT INTO timers (username, name, duration_seconds) VALUES (?, ?, ?)";

			try (Connection conn = connect();
				 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				stmt.setString(1, username);
				stmt.setString(2, name);
				stmt.setLong(3, durationSeconds);
				stmt.executeUpdate();

				try (ResultSet keys = stmt.getGeneratedKeys()) {
					if (keys.next()) {
						return keys.getInt(1);
					}
				}
			} catch (SQLException e) {
				System.err.println(e);
			}
			return -1;
		}

		public static List<TimerInfo> findByUser(String username) {
			String sql = "SELECT * FROM timers WHERE username = ? ORDER BY id";
			List<TimerInfo> timers = new ArrayList<>();

			try (Connection conn = connect();
				 PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, username);

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						String raw = rs.getString("start_time");
						timers.add(new TimerInfo(
								rs.getInt("id"),
								rs.getString("username"),
								rs.getString("name"),
								rs.getLong("duration_seconds"),
								LocalDateTime.parse(
										raw.replace(" ", "T"))
										.toInstant(ZoneOffset.UTC)
						));
					}
				}
			} catch (SQLException e) {
				System.err.println(e);
			}
			return timers;
		}

		public static void delete(int id) {
			String sql = "DELETE FROM timers WHERE id = ?";

			try (Connection conn = connect();
			     PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

}