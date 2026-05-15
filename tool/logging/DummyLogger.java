package logging;

public class DummyLogger extends Logger {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4992475317252236790L;

	/** Creates a new instance of DummyLogger 
     * @param _class */
    DummyLogger(String _class) {
        super(_class);
    }
    
    /** Creates a new instance of DummyLogger 
     * @param _class */
    DummyLogger(Class _class) {
        this(_class.getName());
    }
    
    public void debug(Object msg) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void debug(Object msg, Throwable t) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void error(Object msg) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void error(Object msg, Throwable t) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void fatal(Object msg) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void fatal(Object msg, Throwable t) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void info(Object msg) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void info(Object msg, Throwable t) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void warn(Object msg) {
    	/*
    	 * This logger does nothing. 
    	 */
    }
    
    public void warn(Object msg, Throwable t) {
    	/*
    	 * This logger does nothing. 
    	 */
    }

	@Override
	public boolean isEnabledFor(LogLevel l) {
		return false;
	}
    
}
