package projects.exception;

import java.sql.SQLException;

@SuppressWarnings("serial")

public class DbException extends RuntimeException {
	// methods from superclass
	public DbException() {

	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}
	public DbException(String message, Throwable cause) {
		super(message, cause);
	}
}
