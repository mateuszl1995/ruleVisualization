package org.rulevisualization;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;

@Path("demo")
public class DemoResource {	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getList() throws URISyntaxException, IOException {
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		File folder = new File(decodedPath).getParentFile();
		folder = new File(folder, "datasets");
		
		JsonArray list = new JsonArray();
        for (final File f : folder.listFiles())
			list.add(f.getName());
		return Response.ok(list.toString()).build();
	}
	
	@GET
	@Path("{demoname}/{filename}")
	@Produces({MediaType.MULTIPART_FORM_DATA})
	public Response getFile(
			@PathParam("demoname") String demoname,
			@PathParam("filename") String filename
	) {
		File f = null, root = null;
		try {
			String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			root = new File(decodedPath).getParentFile();
			root = new File(root, "datasets");
			
			f = new File(new File(root, demoname), filename);
			if (!f.getCanonicalPath().startsWith(root.getCanonicalPath()))
				throw new Exception();
				
		} catch (Exception e) {
			throw new HttpException(404, "Demo dataset does not exist.");
		}
        return Response.ok(f).build();
		
	}

}
