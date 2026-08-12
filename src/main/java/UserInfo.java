public record UserInfo(String username, String email, byte[] encryptedPassword ) {

	public String emailType() {
		if (email == null) {
			return null;
		}
		return email.split("@")[1];
	}
};