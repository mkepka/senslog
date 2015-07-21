package cz.hsrs.db.model;

public class NoItemFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoItemFoundException(Exception e){
		super(e);
	}
	public NoItemFoundException(String message){
		super(message);
	}

}
