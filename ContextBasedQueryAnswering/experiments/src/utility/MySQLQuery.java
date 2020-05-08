package utility;
import java.sql.*;  

public class MySQLQuery {

	private Connection con;
	
	public MySQLQuery() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con=DriverManager.getConnection("jdbc:mysql://localhost:3306/esperimenti_miot","root","pcpcpc");  
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public ResultSet executeQuery(String query) {
			//System.out.println(query);
			Statement stmt;
			ResultSet rs=null;
			try {
				stmt = con.createStatement();
				rs=stmt.executeQuery(query);  
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return rs;
		} 
	
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}  

