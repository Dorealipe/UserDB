import java.time.Instant;

public record TimerInfo(int id, String username, String name, long durationSeconds, Instant startTime) {

	public boolean finished() {
		Instant endTime = startTime.plusSeconds(durationSeconds);
		return Instant.now().isAfter(endTime);
	}

	public long secondsRemaining() {
		Instant endTime = startTime.plusSeconds(durationSeconds);
		long remaining = Instant.now().until(endTime, java.time.temporal.ChronoUnit.SECONDS);
		return Math.max(remaining, 0);
	}

	public long secondsSinceFinished() {
		Instant endTime = startTime.plusSeconds(durationSeconds);
		if (!finished()) return -1;
		return endTime.until(Instant.now(), java.time.temporal.ChronoUnit.SECONDS);
	}

	@Override
	public String toString() {
		if (finished()) {
			return "[Done] " + name;
		}
		return "[Running] " + name + " (" + formatDuration(secondsRemaining()) + " left)";
	}

	public static String formatDuration(long seconds) {
		if (seconds < 60) {
			return seconds + " second" + (seconds == 1 ? "" : "s");
		} else if (seconds < 3600) {
			long minutes = (long) Math.floor((double) seconds / 60);
			long secondsReturn = (long) Math.floorMod(seconds, 60);
			return minutes + " minute" + (minutes == 1 ? "" : "s") +
					" and " +
					secondsReturn + " second" + (secondsReturn == 1 ? "" : "s");
		} else {
			long hours = (long) Math.floor((double) seconds / 3600);
			long minutes = Math.floorMod(seconds/60, 60);
			return hours + " hour" + (hours == 1 ? "" : "s") +
					" and " +
					minutes + " minute" + (minutes == 1 ? "" : "s");


		}
	}
}