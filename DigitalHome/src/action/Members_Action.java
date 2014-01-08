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
import com.opensymphony.xwork2.ActionSupport;

public class Members_Action extends ActionSupport{

	
	public static final String numberChar = "0123456789";
	public static final String domainUrl = "http://localhost:9090/plugins/userService/userservice?";
	public static final String ofSecret = "NJ248tgK";
	private URL url = null;
	private HttpURLConnection connection = null;
	//SHOW GATES VARIABLES
	private String page;
	private String rows;
	
	//ADD GATES VARIABLES
	
	private String username;
	private String password;
	private String type;
	private String name;
	private String cardid;
	private String phone1;
	private String phone2;
	
	//result variables;
	private JSONObject show_jsResult;
	private JSONObject add_jsResult;
	private JSONObject bind_jsResult;
	private JSONObject unbind_jsResult;
	private JSONObject delete_jsResult;
	
	//getter and setter
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public JSONObject getShow_jsResult() {
		return show_jsResult;
	}
	public void setShow_jsResult(JSONObject show_jsResult) {
		this.show_jsResult = show_jsResult;
	}
	public JSONObject getAdd_jsResult() {
		return add_jsResult;
	}
	public void setAdd_jsResult(JSONObject add_jsResult) {
		this.add_jsResult = add_jsResult;
	}
	
	
	public JSONObject getBind_jsResult() {
		return bind_jsResult;
	}
	public void setBind_jsResult(JSONObject bind_jsResult) {
		this.bind_jsResult = bind_jsResult;
	}
	public JSONObject getUnbind_jsResult() {
		return unbind_jsResult;
	}
	public void setUnbind_jsResult(JSONObject unbind_jsResult) {
		this.unbind_jsResult = unbind_jsResult;
	}
	public JSONObject getDelete_jsResult() {
		return delete_jsResult;
	}
	public void setDelete_jsResult(JSONObject delete_jsResult) {
		this.delete_jsResult = delete_jsResult;
	}



	///sql
	private  CRUD_SQL _mysql = new CRUD_SQL() ;//sql
	
	public String show() throws Exception { //

		show_jsResult = new JSONObject();
		String id = (String)ServletActionContext.getRequest().getParameter("id");
		//int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			show_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		if(_mysql.search2MySQL("select username,snuser,isbind,type,password,name,cardid,phone1,phone2 from family_members where snuser = '" + id + "'" , _lists) != true)
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
		add_jsResult.put("errorMsg", "添加家庭成员失败！！");
		String id = (String)ServletActionContext.getRequest().getParameter("id");
		String parameters = "type=add&secret=" + ofSecret + "&username=" + username + "&password=" + password + "&name=" + username;   
		//type=add&secret=bigsecret&username=kafka&password=drowssap&name=franz&email=franz@kafka.com
		String result = "";
		try {
			url = new URL(domainUrl + parameters);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("add family members exception : " + e);
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
			System.out.println("add family members exception : " + e);
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
		String sql01 = "insert into ofuser (username,plainPassword,creationDate,modificationDate)values('" + username +
				"','" + password +
				"','" + System.currentTimeMillis() +
				"','" + System.currentTimeMillis() +
				"')";
		
		System.out.println(sql01);
		if(_mysql.add2MySQL(sql01) != true)
			add_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			add_jsResult.put("error", 1);*/
		
		//先添加到openfire里面再添加到smarthome保证唯一。
		
		
	
	
		if(_mysql.connectMySQL("smarthome")!= true){
			
			add_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql = "insert into family_members (username,snuser,type,password,name,cardid,phone1,phone2)values('" + username +
				"','" + id +
				"'," + Integer.parseInt(type) +
				",'" + password +
				"','" + name +
				"','" + cardid +
				"','" + phone1 +
				"','" + phone2 +
				"')";
		System.out.println(sql);
		if(_mysql.add2MySQL(sql) != true)
			add_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			add_jsResult.put("error", 1);
			
		
		return SUCCESS;
	}
	
	public String bind() throws Exception {
		
		bind_jsResult = new JSONObject();
		
		bind_jsResult.put("error", 0);
		bind_jsResult.put("errorMsg", "用户绑定网关失败！！");
		String roster1 = (String)ServletActionContext.getRequest().getParameter("ofroster1");//homeuser
		String roster2 = (String)ServletActionContext.getRequest().getParameter("ofroster2");//snuser
		String parameters = "type=add_roster&secret=" + ofSecret + "&username=" + roster2 + "&item_jid=" + roster1 + "&name=" + roster1 + "&subscription=0";   
		//type=add&secret=bigsecret&username=kafka&password=drowssap&name=franz&email=franz@kafka.com
		String result = "";
		try {
			url = new URL(domainUrl + parameters);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("bind family members exception : " + e);
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
			System.out.println("bind family members exception : " + e);
		}
		if(!"<result>ok</result>".equals(result)){
			bind_jsResult.put("error", 1);
			bind_jsResult.put("errorMsg", result.substring(7, result.length()-8));
			return SUCCESS;
		}
		if(bind_jsResult.getIntValue("error") != 1){
			
			if(_mysql.connectMySQL("smarthome")!= true){
				
				bind_jsResult.put("error", 1);
				_mysql.closeMySQL();
				return SUCCESS;
			}
			String sql01 = "update family_members set isbind = " + 1 +  " where username = '" + roster1 +"'";
			System.out.println(sql01);
			if(_mysql.update2MySQL(sql01) != true)
				bind_jsResult.put("error", 1);
			if(_mysql.closeMySQL() != true)
				bind_jsResult.put("error", 1);
		}
		return SUCCESS;
	}
	
	public String unbind() throws Exception {
		
		unbind_jsResult = new JSONObject();
		
		unbind_jsResult.put("error", 0);
		unbind_jsResult.put("errorMsg", "取消用户绑定网关失败！！");
		String roster1 = (String)ServletActionContext.getRequest().getParameter("ofroster1");//homeuser
		String roster2 = (String)ServletActionContext.getRequest().getParameter("ofroster2");//snuser
		String parameters = "type=delete_roster&secret=" + ofSecret + "&username=" + roster2 + "&item_jid=" + roster1 ;   
		//type=add&secret=bigsecret&username=kafka&password=drowssap&name=franz&email=franz@kafka.com
		String result = "";
		try {
			url = new URL(domainUrl + parameters);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("unbind family members exception : " + e);
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
			System.out.println("unbind family members exception : " + e);
		}
		if(!"<result>ok</result>".equals(result)){
			bind_jsResult.put("error", 1);
			bind_jsResult.put("errorMsg", result.substring(7, result.length()-8));
			return SUCCESS;
		}
	
		
		if(_mysql.connectMySQL("smarthome")!= true){
			
			unbind_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql01 = "update family_members set isbind = " + 0 +  " where username = '" + roster1 +"'";
		
		if(_mysql.update2MySQL(sql01) != true)
			unbind_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			unbind_jsResult.put("error", 1);
		
		return SUCCESS;
	}
	public String delete()throws Exception{
		delete_jsResult = new JSONObject();
		
		delete_jsResult.put("error", 0);
		delete_jsResult.put("errorMsg", "删除用户失败！！");
		String username = (String)ServletActionContext.getRequest().getParameter("id");
		String parameters = "type=delete&secret=NJ248tgK&username=" + username;   
		//type=add&secret=bigsecret&username=kafka&password=drowssap&name=franz&email=franz@kafka.com
		String result = "";
		try {
			url = new URL(domainUrl + parameters);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("delete family members exception : " + e);
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
			System.out.println("delete family members exception : " + e);
		}
		if(!"<result>ok</result>".equals(result)){
			bind_jsResult.put("error", 1);
			bind_jsResult.put("errorMsg", result.substring(7, result.length()-8));
			return SUCCESS;
		}
		if(_mysql.connectMySQL("smarthome")!= true){
			
			delete_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql = "delete from family_members where username='" + username + "'";
		System.out.println(sql);
		if(_mysql.update2MySQL(sql) != true)
			delete_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			delete_jsResult.put("error", 1);
		
		return SUCCESS;
	}



}
