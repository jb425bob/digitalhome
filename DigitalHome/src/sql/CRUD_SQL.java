package sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CRUD_SQL {

	
	private static String url = "jdbc:mysql://10.2.10.222:3306/smarthome";
	private static String user = "mb";
	private static String password = "123456";
	
	private Connection _connection = null;
	private Statement _statement = null;
	private ResultSet _resultSet = null;
	private ResultSetMetaData _metaData  = null;
	private boolean _isConnected = false;
	
	public CRUD_SQL(){
		//System.out.println("a new sql object");
	}
	static {
		InputStream inputStream = CRUD_SQL.class.getClassLoader().getResourceAsStream("database.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		url = "jdbc:mysql://" + p.getProperty("serverIP") + ":3306/";
		user = p.getProperty("user");
		password = p.getProperty("password");
		System.out.println("MySQLUrl : " + url);
		System.out.println("user : " + user);
		System.out.println("password : " + password);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
		}catch(ClassNotFoundException e) {
			System.out.println("inialize exception");
			//throw new ExceptionInInitializerError(e);
		}
	}
	
	public  boolean connectMySQL(String database){
		//System.out.println("connecting to mysql!");
		try{
			_connection = DriverManager.getConnection(url + database, user, password);
		}catch(SQLException e){
			System.out.println("connection MySQL exception !");
			return false;
		}
		try{
			_statement = _connection.createStatement();
		} catch(SQLException e){
			System.out.println("create statement for MySQL exception !");
			return false;
		}
		_isConnected = true;
		//System.out.println("connected!");
		return true;
	}
	
	public  boolean add2MySQL(String sql){
		
		//System.out.println("mysql insert");
		if(!_isConnected || _connection == null || _statement == null){
			return false;
		}
		try{
			_statement.executeUpdate(sql);
		} catch(SQLException e){
			System.out.println("executing insert exception !");
			return false;
		}
		
		return true;
	}
	public  boolean delete2MySQL(String sql){
		
		//System.out.println("mysql delete");
		if(!_isConnected || _connection == null || _statement == null){
			return false;
		}
		try{
			_statement.executeUpdate(sql);
		} catch(SQLException e){
			System.out.println("executing delete exception !");
			return false;
		}
		
		return true;
		
	}
	public  boolean update2MySQL(String sql){
		
		//System.out.println("sql update");
		if(!_isConnected || _connection == null || _statement == null){
			return false;
		}
		try{
			_statement.executeUpdate(sql);
		} catch(SQLException e){
			System.out.println("executing update exception !");
			return false;
		}
		
		return true;
	}
	
	public  int search2Count(String sql){
		int num;
		//System.out.println("sql search count");
		if(!_isConnected || _connection == null || _statement == null){
			return -1;
		}
		
		try{
			_resultSet = _statement.executeQuery(sql);
			_resultSet.next();
			if(_resultSet.getObject(1) == null){
				num = 0;
			}
			else{
			num  = Integer.parseInt(_resultSet.getObject(1).toString());
			}
			//_metaData = _resultSet.getMetaData();
		} catch(SQLException e){
			System.out.println("executing search exception !");
			return -1;
		}
		return num;
	}
	public  boolean search2MySQL(String sql, List<Map<?,?>> lists){
		
		//System.out.println("search sql");
		if(!_isConnected || _connection == null || _statement == null){
			return false;
		}
		
		try{
			_resultSet = _statement.executeQuery(sql);
			_metaData = _resultSet.getMetaData();
		} catch(SQLException e){
			System.out.println("executing search exception !");
			return false;
		}
		
		try {
			int columnNum = _metaData.getColumnCount();
			
			while(_resultSet.next()) {
				Map<String, String> map = new HashMap<String, String>();
				for(int i = 1; i <= columnNum; i++)
				{
				    String columnName = _metaData.getColumnName(i);
				    String columnAttribute = "";
					byte[] bytes = _resultSet.getBytes(i);				
					if(bytes != null)
						try {
							columnAttribute = new String(_resultSet.getBytes(i), "utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
					 columnAttribute = "";
					
					map.put(columnName, columnAttribute);	
					
				}
				lists.add(map);
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("store results exception!");
			return false;
		}
		//System.out.println("show success!");
		return true;
		
	}
	public boolean customSearch2MySQL(String sql, List<Map<?,?>> lists){
		
		//System.out.println("custom search sql");
		if(!_isConnected || _connection == null || _statement == null){
			return false;
		}
		
		try{
			_resultSet = _statement.executeQuery(sql);
			_metaData = _resultSet.getMetaData();
		} catch(SQLException e){
			System.out.println("executing search exception !");
			return false;
		}
		
		try {
			int columnNum = _metaData.getColumnCount();
			
			while(_resultSet.next()) {
				Map<String, String> map = new HashMap<String, String>();
				for(int i = 1; i <= columnNum; i++)
				{
				    String columnName = _metaData.getColumnName(i);
				    String columnAttribute = "";
					byte[] bytes = _resultSet.getBytes(i);				
					if(bytes != null)
						try {
							columnAttribute = new String(_resultSet.getBytes(i), "utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							System.out.println("UnsupportedEncodingException");
						}
					else
					 columnAttribute = "";
					
					//if(!columnName.equals("password")){
						if(columnName.equals("privilege")){
							int nPrivilege = Integer.parseInt(columnAttribute);
							for(int j=1;j<10;j++){
								map.put(columnName + j, ((nPrivilege>>(9-j)) & 1) +"");
								//System.out.println(((nPrivilege>>(9-j)) & 1) + "xxx");
							}
						}else{
							map.put(columnName, columnAttribute);
						}	
						
				//	}
					//System.out.println("xxx" + columnName + ":" + columnAttribute);
					
				}
				lists.add(map);
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("custom search exception!");
			return false;
		}
		//System.out.println("show success!");
		return true;
		
	}
	public  boolean closeMySQL(){
		
		try {
			if(_resultSet != null)
				_resultSet.close();
		}catch(SQLException e) {
			System.out.println("close resultset exception");
		}finally {
			try {
				if(_statement != null)
					_statement.close();
				
			}catch(SQLException e) {
				System.out.println("close statement exception!");
			}finally {
				try {
					if(_connection != null) 
						_connection.close();
				}catch(SQLException e){
					System.out.println("close connection exception!");
				}
			}
			
		}
		//System.out.println("close success");
		return true;
	}
	
	public static void main(String[] args) {
		CRUD_SQL mysql = new CRUD_SQL();
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(mysql.connectMySQL("smarthome")!= true){

			mysql.closeMySQL();

		}
		if(mysql.search2MySQL("select control from family_devices where sncode='1122334455667788990011223344'", _lists) != true){}
		if(mysql.closeMySQL() != true){}
		//System.out.println(_lists.size());

		if(_lists.size() > 0){
			Map<String, String> map = (Map<String, String>) _lists.get(0);
			String control = map.get("control");
			System.out.println("xxx" + control);
			JSONArray jsonArray = JSONObject.parseArray(control);
			Iterator <Object> itr =  jsonArray.iterator();
			HashMap<String,List<String>>hash = new HashMap<String, List<String>>();
			int count = 1;
			int id = 1;
			JSONObject jsonTemp;
			while(itr.hasNext()){
				JSONObject json = (JSONObject)itr.next();
				JSONArray json_array = json.getJSONArray("device");
				Iterator<Object> iterator = json_array.iterator();
				while(iterator.hasNext()){
					JSONObject json_category = (JSONObject)iterator.next();
					String category =(String) json_category.get("category");
					List<String> listContents =  null;
					if((listContents = hash.get(category)) == null){
						listContents = new ArrayList<String>();
						hash.put(category, listContents);
					}
					JSONArray json_content = json_category.getJSONArray("content");
					if(json_content != null){
						Iterator<Object> iterator1 = json_content.iterator();
						while(iterator1.hasNext()){
							JSONObject json_content_temp = (JSONObject)iterator1.next();
							listContents.add((String)json_content_temp.get("name"));
						}
					}
					
				}
			}

			Iterator<String> keyIterator = hash.keySet().iterator();
			int keyLength = hash.size() + 1;
		
			JSONArray json_Result = new JSONArray();
			while(keyIterator.hasNext()){
				String key = keyIterator.next();
				jsonTemp = new JSONObject();
				jsonTemp.put("category", key);
				jsonTemp.put("id", id);
				jsonTemp.put("state", "closed");
				json_Result.add(jsonTemp);
				List<String> list_values = hash.get(key);
				for(int k=0; k<list_values.size(); k++){
					jsonTemp = new JSONObject();
					jsonTemp.put("name", list_values.get(k));
					jsonTemp.put("_parentId", id);
					jsonTemp.put("id", keyLength++);
					jsonTemp.put("category", "");
					json_Result.add(jsonTemp);
				}
				id ++;
			}
			
			System.out.println(json_Result.toJSONString());
			//show_jsResult.put("total", keyLength);
			//show_jsResult.put("rows", json_Result);
			//System.out.println(show_jsResult.toJSONString());
	}
	}
}
