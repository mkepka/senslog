package cz.hsrs.track;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;


public class TrackManager {
	
	 public TrackManager(){
		 
	 }
	ThreadGroup rootThreadGroup = null;
	 
	ThreadGroup getRootThreadGroup( ) {
	    if ( rootThreadGroup != null )
	        return rootThreadGroup;
	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}
	
	ThreadGroup[] getAllThreadGroups( ) {
	    final ThreadGroup root = getRootThreadGroup( );
	    int nAlloc = root.activeGroupCount( );
	    int n = 0;
	    ThreadGroup[] groups;
	    do {
	        nAlloc *= 2;
	        groups = new ThreadGroup[ nAlloc ];
	        n = root.enumerate( groups, true );
	    } while ( n == nAlloc );
	 
	    ThreadGroup[] allGroups = new ThreadGroup[n+1];
	    allGroups[0] = root;
	    System.arraycopy( groups, 0, allGroups, 1, n );
	    return allGroups;
	}

	ThreadGroup getThreadGroup( final String name ) {
	    if ( name == null )
	        throw new NullPointerException( "Null name" );
	    final ThreadGroup[] groups = getAllThreadGroups( );
	    for ( ThreadGroup group : groups )
	        if ( group.getName( ).equals( name ) )
	            return group;
	    return null;
	}
	
	Thread[] getAllThreads( ) {
	    final ThreadGroup root = getRootThreadGroup( );
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	    int nAlloc = thbean.getThreadCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = root.enumerate( threads, true );
	    } while ( n == nAlloc );
	    return java.util.Arrays.copyOf( threads, n );
	}
	
	Thread[] getGroupThreads( final ThreadGroup group ) {
	    if ( group == null )
	        throw new NullPointerException( "Null thread group" );
	    int nAlloc = group.activeCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = group.enumerate( threads );
	    } while ( n == nAlloc );
	    return java.util.Arrays.copyOf( threads, n );	    	    
	}
   public	Thread getThread( final long id ) {
	    final Thread[] threads = getAllThreads( );
	    for ( Thread thread : threads )
	        if ( thread.getId( ) == id )
	            return thread;
	    return null;
	}
	
	public Thread getThread( final String name ) {
	    if ( name == null )
	        throw new NullPointerException( "Null name" );
	    final Thread[] threads = getAllThreads( );
	    for ( Thread thread : threads )
	        if ( thread.getName( ).equals( name ) )
	            return thread;
	    return null;
	}
}
