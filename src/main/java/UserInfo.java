public record UserInfo(String username, String email, String passwordHash, String createdAt ) {

	public String emailType() {
		if (email == null) {
			return null;
		}
		return email.split("@")[1];
	}

	@Override
	public String toString() {
		return "Username: " + username +
				"\nEmail: " + email +
				"\nMember since: " + createdAt;
	}
};