package takSejf;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitServlet extends HttpServlet {
	
	public static final Key key = KeyFactory.createKey("DB", 1);
	
	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        
		DatastoreService storage = DatastoreServiceFactory.getDatastoreService();
		try {
			storage.get(key);
		} catch (EntityNotFoundException e) {
			Entity db = new Entity("DB",1);
			db.setProperty("lastIndex", 2);
			storage.put(db);
			resp.getWriter().write("Initiation complete.");
			return;
		}
		resp.getWriter().write("Allready intitiated.");
        	
    }
}
