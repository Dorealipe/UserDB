public record NoteInfo(int id, String username, String content, boolean isTask, boolean done, String createdAt) {

	@Override
	public String toString() {
		if (!isTask) {
			return "- " + content;
		}
		return "[" + (done ? "X" : " ") + "] " + content;
	}
}