public record UserInfo(String username, byte[] encryptedPassword, String email) {

	public String emailType() {
		if (email == null) {
			return null;
		}
		return email.split("@")[1];
	}
};