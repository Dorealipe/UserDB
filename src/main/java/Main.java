import java.util.Scanner;

void main() {
	Scanner scanner = new Scanner(System.in);
	Database.initialize();
	Database.initializeNotes();
	boolean running = true;
	User user = null;
	String stage = "";
	String substage = "";

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
						substage = "name";
						while (substage.equals("name")) {
							write("(Write \"back\" to go back)");
							write("Insert your username: ");
							name = input(scanner);
							if (name.equalsIgnoreCase("back")) {
								stage = "";
								substage = "";
								break;
							}
							if (!name.isEmpty()) {
								substage = "password";
							}
						}
						while (substage.equals("password")) {
							write("(Write \"back\" to go back)");
							write("Insert a password");
							password = input(scanner);
							if (password.equalsIgnoreCase("back")) {
								substage = "name";
								break;
							}
							if (password.length() > 8) {
								substage = "email";
							} else {
								write("Password should be longer than 8 characters");
							}
						}
						while (substage.equals("email")) {
							write("(Write \"back\" to go back)");
							write("Insert your email (Optional: Type null to negate)");
							email = input(scanner);
							if (email.equalsIgnoreCase("back")) {
								substage = "password";
								break;
							}
							if (email.equals("null")) {
								email = null;
							}
							if (!"".equals(email)) {
								substage = "ready";
								stage = "logged_in";
							}
						}
						if (substage.equals("ready")) {
							user = new User(name, email, password);
							write("Signed up!");
						}
					}
				}
				case "2" -> {
					stage = "log_in";
					String name = null;
					String password = null;
					boolean validPassword = false;
					UserInfo found = null;
					while (stage.equals("log_in")) {
						substage = "name";
						while (substage.equals("name")) {
							write("(Write \"back\" to go back)");
							write("Insert your username: ");
							name = input(scanner);
							found = Database.findUser(name);
							if (name.equalsIgnoreCase("back")) {
								stage = "";
								substage = "";
								break;
							}
							if (found == null) {
								write("User \"" + name + "\" not found");
								break;
							}
							if (!name.isEmpty()) {
								substage = "password";
							}
						}
						while (substage.equals("password")) {
							write("(Write \"back\" to go back)");
							write("Insert your password :");
							password = input(scanner);


							if (password.equalsIgnoreCase("back")) {
								substage = "name";
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
			write("2) Edit profile");
			write("3) Set up timer");
			write("4) Manage notes");
			write("L) Log out");
			write("E) Exit");
			String choice = input(scanner);

			switch (choice.toLowerCase()) {
				case "1" -> user.printUserInfo();
				case "2" -> {
					write("(Write \"back\" to go back)");
					write("Edit email (1) or password (2)");
					String editChoice = input(scanner);
					switch (editChoice) {
						case "1" -> {
							write("Insert your new email:");
							String newEmail = input(scanner);
							if (newEmail.equals("null")) newEmail = null;
							user.updateEmail(newEmail);
							write("Email updated!");
						}
						case "2" -> {
							substage = "old_password";
							String oldPassword = "";
							String hashedPassword = user.passwordHash();
							String newPassword;
							while (substage.equals("old_password")) {
								write("Insert your old password:");
								oldPassword = input(scanner);
								if (HashManager.verify(oldPassword,hashedPassword)) {
									substage = "new_password";
								} else {
									write("Wrong password!");
								}
							}
							while (substage.equals("new_password")) {
								write("Insert your new password:");
								newPassword = input(scanner);
								if (newPassword.length() > 8) {
									user.updatePassword(newPassword);
									write("Password updated!");
									substage = "";
								} else {
									write("Password should be longer than 8 characters");
								}
							}
						}
						case null, default -> write("Invalid choice");
					}
				}
				case "3" -> {
					substage = "timer_set_time";
					String name = "";
					String time = "";
					while (substage.equals("timer_set_time")) {
						write("Suffixes: m -> minutes, s -> seconds");
						write("Insert time: ");
						time = input(scanner);
						if (parseTimes(time) > 0) {
							substage = "timer_set_name";
						} else {
							write("Invalid time");
						}

					}
					while (substage.equals("timer_set_name")) {
						write("Insert the name of the timer:");
						name = input(scanner);
						substage = "";
					}
					setTimer(parseTimes(time), name).start();

				}
				case "4" -> {
					boolean inNoteMenu = true;
					while (inNoteMenu) {
						write("~~~ My Notes ~~~");
						List<NoteInfo> notes = user.getNotes();
						if (notes.isEmpty()) {
							write("No notes yet");
						} else {
							for (NoteInfo note : notes) {
								write(note.id() + ") " + note);
							}
						}
						write("1) Add   2) Toggle done   3) Delete   B) Back");
						String noteChoice = input(scanner).toLowerCase();
						switch (noteChoice) {
							case "1" -> {
								boolean isTask = false;
								String message = "";
								substage = "add_note.isTask";
								while (substage.equals("add_note.isTask")) {
									write("Is this a task (1) or a note (2)");
									String isTaskInput = input(scanner);
									switch (isTaskInput.toLowerCase()) {
										case "1" -> {
											isTask = true;
											substage = "add_note.message";
										}
										case "2" -> {
											isTask = false;
											substage = "add_note.message";
										}
										case null, default -> {
											write("Invalid option");
										}

									}
								}
								while (substage.equals("add_note.message")) {
									write("Input the content of the note");
									message = input(scanner);
									if (!message.isEmpty()) {
										substage = "";
									} else {
										write("Note cannot be empty");
									}
								}
								user.addTask(message, isTask);
								write("Added!");
							}
							case "2" -> {
								substage = "toggle_note";
								while (substage.equals("toggle_note")) {
									write("Write the id to toggle");
									String inputId = input(scanner);
									if (inputId.equalsIgnoreCase("back")) {
										substage = "";
										break;
									}
									try {
										int id = Integer.parseInt(inputId);
										NoteInfo target = notes.stream().filter(n -> n.id() == id).findFirst().orElse(null);
										if (target == null) {
											write("Note not found");
										} else if (!target.isTask()) {
											write("This is a note, not a task — can't toggle done");
										} else {
											Database.setTaskDone(id, !target.done());
											write("Toggled!");
											substage = "";
										}
									} catch (NumberFormatException e) {
										write("Invalid id");
									}
								}
							}
							case "3" -> {
								int id;
								substage = "del_note";
								while (substage.equals("del_note")) {
									write("Write the id to delete");
									String inputId = input(scanner);
									if (inputId.equalsIgnoreCase("back")) {
										substage = "";
										break;
									}
									try {
										id = Integer.parseInt(inputId);
										Database.deleteNote(id);
										write("Deleted!");
										substage = "";

									} catch (NumberFormatException e) {
										write("Invalid id");
									}
								}
							}
							case "b" -> {
								inNoteMenu = false;
							}
							default -> write("Invalid choice");
						}
					}
				}
				case "l" -> {
					user = null;
					loggedIn = false;
				}
				case "e" -> {
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

public long parseTime(String input) {
	input = input.trim().toLowerCase();
	try {
		long baseTime = Long.parseLong(input.substring(0, input.length() - 1));
		if (input.endsWith("h")) {
			return baseTime * 3600;
		} else if (input.endsWith("m")) {
			return baseTime * 60;
		} else if (input.endsWith("s")) {
			return baseTime;
		} else {
			write("Invalid format. Use e.g. 5m or 30s");
			return -1;
		}
	} catch (NumberFormatException e) {
		write("Invalid number.");
		return -1;
	}
}

public long parseTimes(String input) {
	long total = 0;
	String[] cut = input.split(" ");
	for (String time : cut) {
		if (parseTime(time) == -1) return -1;
		total += parseTime(time);
	}
	return total;
}

public Thread setTimer(long seconds, String name) {
	long milliseconds = seconds*1000;
	class Timer implements Runnable {
		@Override
		public void run() {

			try {
				Thread.sleep(milliseconds);
				write("Timer \"" + name + "\" ended");
			} catch (InterruptedException e) {
				write("Timer \"" + name + "\" interrupted");
			}

		}
	}
	Thread thread = new Thread(new Timer());
	thread.setDaemon(true);
	return thread;
}