public class Main {
	public void main() {

		new User("JohnTheBest", "johnbestest_1234", "johnthebest@qmail.com");
		new User("xXAliceXx", "10_10_1980_ALICE","xxalicexx@firemail.com");
		new User("Dorealipe","12A*34b>0");



		User john = User.log("JohnTheBest", "johnbestest_1234");
		User alice = User.log("xXAliceXx", "10_10_1980_ALICE");
		john.printUserInfo();
		alice.printUserInfo();

	}
}
