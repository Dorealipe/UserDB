import java.util.Scanner;

void main() {
	Scanner scanner = new Scanner(System.in);
	Database.initialize();
	boolean running = true;
	User user = null;
	String stage = "";

	while (running) {
		while (user == null) {
			write("Sign Up (1) or Log In (2)");
			String in = input(scanner);
			switch (in) {
				case "1" -> {
					stage = "sign_up";
					String name = null;
					String email = null;
					String password = null;
					while (stage.equals("sign_up")) {
						String substage = "name";
						while (substage.equals("name")) {
							write("Insert your username: ");
							name = input(scanner);
							if (!"".equals(name)) {
								substage = "password";
							}
						}
						while (substage.equals("password")) {
							write("Insert a password");
							password = input(scanner);
							if (password.length() > 8) {
								substage = "email";
							} else {
								write("Password should be longer than 8 characters");
							}
						}
						while (substage.equals("email")) {
							write("Insert your email (Optional: Type null to negate)");
							email = input(scanner);
							if (email.equals("null")) {
								email = null;
							}
							if (!"".equals(email)) {
								substage = "";
								stage = "logged_in";
							}
						}
						user = new User(name, email, password);
						write("Signed up!");
					}
				}
				case "2" -> {
					stage = "log_in";
					String name = null;
					String password = null;
					boolean validPassword = false;
					while (stage.equals("log_in")) {
						String substage = "name";
						while (substage.equals("name")) {
							write("Insert your username: ");
							name = input(scanner);
							if (!"".equals(name)) {
								substage = "password";
							}
						}
						while (substage.equals("password")) {
							write("Insert your password (Write back to go back to username):");
							password = input(scanner);
							UserInfo found = Database.findUser(name);
							if (found == null || password.equals("back")) {
								break;
							}
							if (HashManager.verify(password, found.passwordHash())) {
								substage = "";
								stage = "logged_in";
								validPassword = true;
							} else {
								write("Wrong password!");
							}
						}
						if (validPassword) {
							user = User.logIn(name, password);
							write("Logged in!");
						}
					}
				}
				case null, default -> {
					write("Invalid choice");
				}
			}
		}
		boolean loggedIn = true;
		while (loggedIn) {
			write("Welcome, " + user.username() + "!");
			write("1) View profile");
			write("2) Log out");
			write("3) Exit");
			String choice = input(scanner);

			switch (choice) {
				case "1" -> user.printUserInfo();
				case "2" -> {
					user = null;
					loggedIn = false;
				}
				case "3" -> {
					loggedIn = false;
					running = false;
				}
				case null, default -> write("Invalid choice");
			}
		}
	}
}

public void write(String text) {
	IO.println(text);
}

public String input(Scanner scanner) {
	return scanner.nextLine();
}