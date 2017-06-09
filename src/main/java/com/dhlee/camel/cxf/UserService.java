package com.dhlee.camel.cxf;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/userservice")
@WebService
public interface UserService {
	
	@GET
	@Path("user/{id}")
//	@Consumes( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED} )
//	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
	public Response getUser(@PathParam("id") String id);
	
	@GET
	@Path("user")
	public Response getUsers();
	
	@PUT
	@Path("user/{id}")
	public Response updateUser(@PathParam("id") String id);
	
	@POST
	@Path("user")
	public Response createUser(String data);
	
	@DELETE
	@Path("user/{id}")
	public Response deleteUser(@PathParam("id") String id);
}
