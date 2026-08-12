public record UserInfo(String username, byte[] encryptedPassword, String email) {

	public String emailType() {
		return email.split("@")[1];
	}
};