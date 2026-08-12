public class Main {
	public void main() {

		Database.initialize();

		new User("JohnTheBest", "johnbestest_1234", "johnthebest@qmail.com");
		new User("xXAliceXx", "10_10_1980_ALICE","xxalicexx@firemail.com");
		new User("Dorealipe","12A*34b>0");



		User john = User.logIn("JohnTheBest", "johnbestest_1234");
		User alice = User.logIn("xXAliceXx", "10_10_1980_ALICE");
		john.printUserInfo();
		alice.printUserInfo();
		User dorea = User.logIn("Dorealipe","12A*34b>0");
		dorea.printUserInfo();
		System.out.println(john.emailType());

	}
}
