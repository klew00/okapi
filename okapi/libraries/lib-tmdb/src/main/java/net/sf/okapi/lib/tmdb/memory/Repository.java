package net.sf.okapi.lib.tmdb.memory;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;

public class Repository implements IRepository {

	private List<ITm> tms;
	private long lastKey = 0;
	
	public Repository () {
		tms = new ArrayList<ITm>();
	}

	@Override
	public List<ITm> getTmList () {
		return tms;
	}

	@Override
	public ITm addTm (String name,
		String description)
	{
		Tm tm = new Tm(++lastKey, name, description);
		tms.add(tm);
		return tm;
	}

}
