/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.tm.pensieve.server;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.seeker.PensieveSeeker;
import net.sf.okapi.tm.pensieve.server.model.QueryResult;

import org.slf4j.Logger;

@Path("/")
public class QueryResource {

	@Inject Logger log;
	
	@Inject PensieveSeeker seeker;
	
	@Path("/search/{srcLang}/{trgLang}")
	@GET
	@Produces("application/json")
	public Response search(
			@PathParam("srcLang") LocaleId srcLang,
			@PathParam("srcLang") LocaleId trgLang,
			@QueryParam("q") @DefaultValue("") String query,
			@QueryParam("threshold") @DefaultValue("85") int threshold,
			@QueryParam("maxhits") @DefaultValue("25") int maxhits) {

		TextFragment tf = new TextFragment(query);
		List<TmHit> hits = seeker.searchFuzzy(tf, threshold, maxhits, new Metadata());
		
		log.debug("found {} hits", hits.size());

		List<QueryResult> results = new ArrayList<QueryResult>();
		
		for(TmHit hit : hits) {
			QueryResult result = new QueryResult();
			result.setScore(hit.getScore());
			result.setSource(hit.getTu().getSource().getContent().getCodedText());
			result.setTarget(hit.getTu().getTarget().getContent().getCodedText());
			results.add(result);
		}
		
		return Response.ok().entity(results).build();
		
	}
	
}
