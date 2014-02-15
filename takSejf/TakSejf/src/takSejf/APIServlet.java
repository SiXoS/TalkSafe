package takSejf;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.datastore.DatastoreV4.Filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

public class APIServlet extends HttpServlet {
	
	private DatastoreService storage = DatastoreServiceFactory.getDatastoreService();
	private Entity parent;
	
	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        
        try {
			parent = storage.get(InitServlet.key);
		} catch (EntityNotFoundException e) {
			resp.getWriter().write("{\"error\":\"Server not initialised.\"}");
			return;
		}
        System.out.println(req.getParameter("phone"));
        if(req.getParameter("add") != null){
        	add(req, resp.getWriter());
        }else if(req.getParameter("view") != null){
        	view(resp.getWriter());
        }else if(req.getParameter("editIP") != null){
        	editIP(req, resp.getWriter());
        }else if(req.getParameter("get") != null){
        	get(req, resp.getWriter());
        }else if(req.getParameter("delete") != null){
        	delete(req,resp.getWriter());
        }else if(req.getParameter("deleteAll") != null){
        	deleteAll(req,resp.getWriter());
        }else
        	resp.getWriter().write("{\"error\": \"No valid argument supplied\"}");
        	
    }
	
	private void deleteAll(HttpServletRequest req, PrintWriter writer){
		Query row = new Query("row", parent.getKey());
		List<Entity> result = storage.prepare(row).asList(FetchOptions.Builder.withLimit(20));
		for(Entity res : result)
			storage.delete(res.getKey());
		writer.write("done");
	}
	
	private void delete(HttpServletRequest req, PrintWriter writer){
		Query row = new Query("row", parent.getKey()).setFilter(new FilterPredicate("phone", FilterOperator.EQUAL, req.getParameter("delete")));
		Entity result = storage.prepare(row).asSingleEntity();
		if(result == null){
			writer.write("{\"error\":\"No such phone.\"}");
		}else{
			storage.delete(result.getKey());
			writer.write("{\"success\":\"1\"}");
		}
	}
	
	private void get(HttpServletRequest req, PrintWriter writer){
		Query row = new Query("row", parent.getKey()).setFilter(new FilterPredicate("phone", FilterOperator.EQUAL, req.getParameter("get")));
		Entity result = storage.prepare(row).asSingleEntity();
		if(result == null)
			writer.write("{\"error\":\"No such phone.\"}");
		else{
			writer.write("{\"success\":\"1\","
					+ "\"item\":{"
					+ "\"phone\":\"" + result.getProperty("phone") + "\","
					+ "\"IP\":\"" + result.getProperty("IP") + "\","
					+ "\"port\":\"" + result.getProperty("port") + "\"}}");
		}
	}
	
	private void editIP(HttpServletRequest req, PrintWriter writer){
		if(req.getParameter("IP") == null && req.getParameter("port") == null){
			writer.write("{\"error\":\"Some required parameters is missing.\"}");
			return;
		}
		
		Query row = new Query("row", parent.getKey()).setFilter(new FilterPredicate("phone", FilterOperator.EQUAL, req.getParameter("editIP")));
		Entity result = storage.prepare(row).asSingleEntity();
		if(result == null){
			writer.write("{\"error\":\"No such phone.\"}");
		}else{
			result.setProperty("IP", req.getParameter("IP"));
			result.setProperty("port", req.getParameter("port"));
			storage.put(result);
			
			writer.write("{\"success\":\"1\"}");
		}
		
	}
	
	private void view(PrintWriter writer){
		Query rows = new Query(parent.getKey());
		List<Entity> result = storage.prepare(rows).asList(FetchOptions.Builder.withLimit(5));
		writer.write("{items:[\n");
		boolean first = true;
		for(Entity row : result){
			if(row.getProperty("phone") == null || row.getProperty("IP") == null) continue;
			if(!first) writer.write(",\n");
			else first = false;
			writer.write("{\"phone\":\"" + row.getProperty("phone") + "\", "
						+ "\"IP\":\"" + row.getProperty("IP") + "\", "
						+ "\"port\":\"" + row.getProperty("port") + "\"}");
		}
		writer.write("]}");
	}
	
	private void add(HttpServletRequest req, PrintWriter writer){
		
		if(req.getParameter("phone") != null && req.getParameter("IP") != null && req.getParameter("port") != null){
			
			Query allreadyExists = new Query("row", parent.getKey()).setFilter(new FilterPredicate("phone", FilterOperator.EQUAL, req.getParameter("phone")));
			Entity item = storage.prepare(allreadyExists).asSingleEntity();
			if(item != null){
				writer.write("{\"error\":\"This number is allready registered\"}");
				return;
			}
			
			long newIndex = incIndex();
			Key key = KeyFactory.createKey(parent.getKey(),"row", newIndex);
			Entity row = new Entity("row",key);
			row.setProperty("phone", req.getParameter("phone"));
			row.setProperty("IP", req.getParameter("IP"));
			row.setProperty("port", req.getParameter("port"));
			
			storage.put(row);
			writer.write("{\"success\":\"1\"}");
		}else{
			writer.write("{\"error\":\"Some required fields were missing\"}");
		}
		
	}
	
	private long incIndex(){
		
		long newIndex = (long)parent.getProperty("lastIndex") + 1;
		parent.setProperty("lastIndex", newIndex);
		storage.put(parent);
		return newIndex;
		
	}

}
