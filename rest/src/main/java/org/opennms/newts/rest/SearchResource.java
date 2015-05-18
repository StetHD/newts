package org.opennms.newts.rest;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opennms.newts.api.search.Query;
import org.opennms.newts.api.search.SearchResults;
import org.opennms.newts.api.search.Searcher;
import org.opennms.newts.api.search.query.ParseException;
import org.opennms.newts.api.search.query.QueryParser;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;


@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private final Searcher m_searcher;

    public SearchResource(Searcher searcher) {
        m_searcher = checkNotNull(searcher, "searcher argument");
    }

    @GET
    @Timed
    public SearchResults search(@QueryParam("q") Optional<String> query) {
        checkArgument(query.isPresent(), "missing required query parameter (q=<argument>)");
        QueryParser qp = new QueryParser();
        Query parsedQuery;
        try {
            parsedQuery = qp.parse(query.get());
        } catch (ParseException e) {
            throw new WebApplicationException(e, Response.status(Status.BAD_REQUEST).entity("Invalid query " + query.get()).build());
        }
        return m_searcher.search(parsedQuery);
    }

}
