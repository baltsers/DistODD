package logging;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public abstract class Logger implements java.io.Serializable {

	public static enum LogLevel{ DEBUG, INFO, WARN, ERROR, FATAL }
	
	/**
	 * Name of property that specifies the name of the logger class to be used.
	 */
	public final static String LOGGER_CLASS_NAME_PROPERTY_NAME = "de.uniba.wiai.lspi.util.logging.logger.class";

	/**
	 * Name of property that defines if logging is off.
	 */
	public final static String LOGGING_OFF_PROPERTY_NAME = "de.uniba.wiai.lspi.util.logging.off";

	/**
	 * The name of the standard logger class.
	 */
	private final static String STANDARD_LOGGER_CLASS = Log4jLogger.class
			.getName();

	/**
	 * The name of the class, for which this is the logger.
	 */
	protected String name = "";

	/**
	 * Map containing instances of loggers. Key: name of the logger. Value: The
	 * logger itself.
	 */
	private static final Map<String, Logger> loggerInstances = new HashMap<String, Logger>();

	/**
	 * Creates a new instance of Logger
	 * 
	 * @param name
	 */
	protected Logger(String name) {
		this.name = name;
	}

	/**
	 * @param _class
	 * @return The logger for the given class.
	 */
	public static Logger getLogger(Class _class) {
		return getLogger(_class.getName());
	}

	/**
	 * @param name
	 * @return The logger with the given name.
	 */
	public synchronized static Logger getLogger(String name) {

		String loggingOff = System.getProperty(LOGGING_OFF_PROPERTY_NAME);
		boolean logOff = false; 

		if ((loggingOff != null) && (loggingOff.equalsIgnoreCase("true"))) {
			name = Logger.class.getName();
			logOff = true; 
		}

		Logger logger = Logger.loggerInstances.get(name);
		if (logger != null) {
			return logger;
		} else {

			if (!logOff) {
				String loggerClassName = System
						.getProperty(LOGGER_CLASS_NAME_PROPERTY_NAME);
				if ((loggerClassName == null) || (loggerClassName.equals(""))) {
					loggerClassName = STANDARD_LOGGER_CLASS;
				}
				try {
					Class loggerClass = Class.forName(loggerClassName);
		
					Constructor cons = loggerClass
							.getConstructor(new Class[] { java.lang.String.class });
					logger = (Logger) cons.newInstance(new Object[] { name });
				} catch (Throwable t) {
					/*
					 * Exception occured during instantiation of custom logger or
					 * Log4jLogger. Create dummy logger.
					 */
					System.setProperty(LOGGING_OFF_PROPERTY_NAME, "true"); 
					logger = getLogger(name); 
				}
			} else {
				logger = new DummyLogger(name); 
			}
			Logger.loggerInstances.put(name, logger);
			return logger;
		}
	}

	public abstract boolean isEnabledFor(LogLevel l); 
	/**
	 * @param msg
	 */
	public abstract void debug(Object msg);

	/**
	 * @param msg
	 * @param t
	 */
	public abstract void debug(Object msg, Throwable t);

	/**
	 * @param msg
	 */
	public abstract void info(Object msg);

	/**
	 * @param msg
	 * @param t
	 */
	public abstract void info(Object msg, Throwable t);

	/**
	 * @param msg
	 */
	public abstract void warn(Object msg);

	/**
	 * @param msg
	 * @param t
	 */
	public abstract void warn(Object msg, Throwable t);

	/**
	 * @param msg
	 */
	public abstract void error(Object msg);

	/**
	 * @param msg
	 * @param t
	 */
	public abstract void error(Object msg, Throwable t);

	/**
	 * @param msg
	 */
	public abstract void fatal(Object msg);

	/**
	 * @param msg
	 * @param t
	 */
	public abstract void fatal(Object msg, Throwable t);

}
