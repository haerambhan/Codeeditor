package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import Database.DatabaseAccess;
import Model.Test;

@WebServlet("/Test/*")
public class TestServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	public Gson gson = new Gson();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DatabaseAccess db = new DatabaseAccess();
		Set<Test> tests = db.getTests();
		sendAsJson(resp,tests);
	}

	public void sendAsJson(HttpServletResponse response, Object obj) throws IOException
    {
    	String res = gson.toJson(obj);
    	response.setContentType("application/json");
    	PrintWriter out = response.getWriter(); 
		out.print(res);
		out.flush();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DatabaseAccess db = new DatabaseAccess();
	    String body = request.getReader().readLine();
	    try 
	    {
			JSONObject obj = new JSONObject(body);
			String title = obj.getString("title");
			String description = obj.getString("description");
			String difficulty = obj.getString("difficulty");			
			int testId = db.createTest(title, description, difficulty);
			JSONArray testcases = obj.getJSONArray("testcases");
			for(int i = 0 ; i < testcases.length(); i++)
			{
				String input = testcases.getJSONObject(i).getString("input");
				String output = testcases.getJSONObject(i).getString("output");
				db.addTestCase(input,output,testId);
			}
			sendAsJson(response,"false");
		} catch (JSONException e) {
			sendAsJson(response,e.getMessage());
		}catch (SQLException e) {
			System.out.println(e);
			System.out.println(e.getErrorCode());
			sendAsJson(response,"exists");
		}
	}
}
