package net.sf.okapi.lib.tmdb;

import java.util.List;

public interface IRepository {

	public List<ITm> getTmList ();
	
	public ITm addTm (String name,
		String description);
	
}
