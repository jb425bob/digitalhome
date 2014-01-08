package action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.struts2.ServletActionContext;

import sql.CRUD_SQL;

import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Gates_Action extends ActionSupport{

	public static final String domainUrl = "http://localhost:9090/plugins/userService/userservice?";
	public static final String ofSecret = "NJ248tgK";
	private URL url = null;
	private HttpURLConnection connection = null;
	//SHOW GATES VARIABLES
	private String page;
	private String rows;
	
	//ADD GATES VARIABLES
	private String snuser;
	private String sncode;
	private String password;
	private String province;
	private String city;
	private String zone;
	private String location;
	
	//result variables;
	private JSONObject show_jsResult;
	private JSONObject add_jsResult;
	private JSONObject delete_jsResult;
	
	//getter and setter
	
	
	public JSONObject getShow_jsResult() {
		return show_jsResult;
	}
	public JSONObject getDelete_jsResult() {
		return delete_jsResult;
	}
	public void setDelete_jsResult(JSONObject delete_jsResult) {
		this.delete_jsResult = delete_jsResult;
	}
	public void setShow_jsResult(JSONObject show_jsResult) {
		this.show_jsResult = show_jsResult;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getRows() {
		return rows;
	}
	public void setRows(String rows) {
		this.rows = rows;
	}


	public String getSnuser() {
		return snuser;
	}
	public void setSnuser(String snuser) {
		this.snuser = snuser;
	}
	public String getSncode() {
		return sncode;
	}
	public void setSncode(String sncode) {
		this.sncode = sncode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public JSONObject getAdd_jsResult() {
		return add_jsResult;
	}
	public void setAdd_jsResult(JSONObject add_jsResult) {
		this.add_jsResult = add_jsResult;
	}


	private  CRUD_SQL _mysql = new CRUD_SQL() ;//sql
	
	public String show() throws Exception { //显示网关信息
		
		int nPage = Integer.parseInt(page);
		int nRows = Integer.parseInt(rows);
		int offset = (nPage - 1) * nRows;
		
		String user = (String) ActionContext.getContext().getSession().get("user");
		String identity = (String) ActionContext.getContext().getSession().get("identity");
		String p= (String) ActionContext.getContext().getSession().get("province");
		String c = (String) ActionContext.getContext().getSession().get("city");
		String z = (String) ActionContext.getContext().getSession().get("zone");
		
		System.out.println(user + identity  + p + c + z);
		String limit_sql = "";
		if(user == null)
			return SUCCESS;
		if("1".equals(identity)){
			limit_sql += "where province = '" + p + "' ";
		} else if("2".equals(identity)){
			limit_sql += "where province = '" + p + "' and city='" + c + "' ";
		} else if("3".equals(identity)){
			limit_sql += "where province = '" + p + "' and city='" + c + "' and zone='" + z + "' ";
		}
		String sql_limit = " limit " + offset + "," + rows;
		show_jsResult = new JSONObject();
		int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			show_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		if( (nNum = _mysql.search2Count("select count(*) from family_gates " + limit_sql)) != -1)
			show_jsResult.put("total", nNum);
		if(_mysql.search2MySQL("select snuser,sncode,password,state,province,city,zone,location from family_gates " + limit_sql + sql_limit, _lists) != true)
			show_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			show_jsResult.put("error", 1);
		
		if(_lists != null){
			
				show_jsResult.put("rows", _lists);
			   // System.out.println(show_jsResult.toJSONString());
			}
		return SUCCESS;
	}
	
	public String add() throws Exception {
		
		add_jsResult = new JSONObject();
		
		add_jsResult.put("error", 0);
		add_jsResult.put("errorMsg", "添加网关失败！！");
		
		String user = (String) ActionContext.getContext().getSession().get("user");
		String identity = (String) ActionContext.getContext().getSession().get("identity");
		String p= (String) ActionContext.getContext().getSession().get("province");
		String c = (String) ActionContext.getContext().getSession().get("city");
		String z = (String) ActionContext.getContext().getSession().get("zone");
		
		System.out.println(user + identity  + p + c + z);
		String limit_sql = "";
		if(user == null || "".equals(province) || "".equals(city) || "".equals(zone)){
			add_jsResult.put("error", 1);
			return SUCCESS;
		}	
		if("1".equals(identity)){
			if(!province.equals(p)){
				add_jsResult.put("error", 1);
				return SUCCESS;
			}
		} else if("2".equals(identity)){
			if(!province.equals(p) || !city.equals(c)){
				add_jsResult.put("error", 1);
				return SUCCESS;
			}
		} else if("3".equals(identity)){
			if(!province.equals(p) || !city.equals(c) || !zone.equals(z)){
				add_jsResult.put("error", 1);
				return SUCCESS;
			}
		}
		
		String parameters = "type=add&secret=" + ofSecret + "&username=" + snuser + "&password=" + password ;   
		//type=add&secret=bigsecret&username=kafka&password=drowssap&name=franz&email=franz@kafka.com
		String result = "";
		try {
			url = new URL(domainUrl + parameters);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("add family gateway exception : " + e);
		}
		try {
			connection = (HttpURLConnection)url.openConnection();
		
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while((line = br.readLine()) != null){
				result += line;
			}
			System.out.println(connection.getContentEncoding() + result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("add family gateway exception : " + e);
		}
		if(!"<result>ok</result>".equals(result)){
			add_jsResult.put("error", 1);
			add_jsResult.put("errorMsg", result.substring(7, result.length()-8));
			return SUCCESS;
		}
		/*if(_mysql.connectMySQL("jz_test")!= true){
			
			add_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql01 = "insert into ofuser (username,plainPassword,creationDate,modificationDate)values('" + snuser +
				"'," + password +
				",'" + System.currentTimeMillis() +
				"','" + System.currentTimeMillis() +
				"')";
		if(_mysql.add2MySQL(sql01) != true)
			add_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			add_jsResult.put("error", 1);
		//先往openfire数据库里面添加用户，保证唯一。	
*/		//if(add_jsResult.getIntValue("error") != 1){
		
			if(_mysql.connectMySQL("smarthome")!= true){
				
				add_jsResult.put("error", 1);
				_mysql.closeMySQL();
				return SUCCESS;
			}
			String sql = "insert into family_gates (snuser,sncode,password,province,city,zone,location)values('" + snuser + "','" + sncode +
					"','" + password +
					"','" + province +
					"','" + city +
					"','" + zone +
					"','" + location +	
					"')";
			
			System.out.println("smarthome: " + sql);
			if(_mysql.add2MySQL(sql) != true)
				add_jsResult.put("error", 1);
			if(_mysql.closeMySQL() != true)
				add_jsResult.put("error", 1);
			
	//	}
		return SUCCESS;
	}
public String delete() throws Exception {
		
		delete_jsResult = new JSONObject();
		
		delete_jsResult.put("error", 0);
		delete_jsResult.put("errorMsg", "删除网关失败,请确保此网关下没有任何家庭成员！！");
		String gateway_name = (String)ServletActionContext.getRequest().getParameter("id");
		if(_mysql.connectMySQL("smarthome")!= true){
			delete_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql = "delete from family_gates where snuser='" + gateway_name + "'";
		System.out.println("smarthome: " + sql);
		if(_mysql.update2MySQL("begin") == true ){//开始transaction
			if(_mysql.delete2MySQL(sql) == true){
				String parameters = "type=delete&secret=NJ248tgK&username=" + gateway_name;  ;   
				String result = "";
				try {
					url = new URL(domainUrl + parameters);
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("add family gateway exception : " + e);
				}
				try {
					connection = (HttpURLConnection)url.openConnection();
				
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while((line = br.readLine()) != null){
						result += line;
					}
					System.out.println(connection.getContentEncoding() + result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("delete family gateway exception : " + e);
				}
				if(!"<result>ok</result>".equals(result)){
					_mysql.update2MySQL("rollback");
					delete_jsResult.put("error", 1);
					//delete_jsResult.put("errorMsg", result.substring(7, result.length()-8));
				}else{
					_mysql.update2MySQL("commit");
				}
			} else {
				_mysql.update2MySQL("commit");
			}
		}
		if(_mysql.closeMySQL() != true)
			delete_jsResult.put("error", 1);		
		return SUCCESS;
	}
	
}
