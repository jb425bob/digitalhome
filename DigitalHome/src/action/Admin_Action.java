package action;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.struts2.ServletActionContext;

import sql.CRUD_SQL;

import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author mabo
 *管理员业务处理acton类{登陆 (login)、添加(add)、修改密码(editpwd)、修改权限(edit)、删除(remove)、查找(show)、显示信息(get)}
 *每次action方法请求都产生一个JSON对象，作为结果返回给客户端。
 *JSON对象基于方法名称命名，如login返回JSON对象为login_jsResult即为*_jsResult（查看strut.xml配置文件action配置说明）
 *
 */
public class Admin_Action extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _user = "";//登陆用户名
	private String _pwd = ""; //登陆用户密码
	private String user = ""; //要添加的用户名
	private String rows ;		//datagrid 行号，默认会post到服务器
	private String page;		//datagrid 页号，默认会post到服务器
	private int login_jsResult = 0;
	private int  editpwd_jsResult = 0;
	private JSONObject logout_jsResult;
	private JSONObject get_jsResult;
	private JSONObject show_jsResult;
	private JSONObject add_jsResult;
	private JSONObject edit_jsResult;
	private JSONObject remove_jsResult ;
	private JSONObject valid_jsResult ;
	


	//权限内容
	private String name;
	private String province;
	private String city;
	private String zone;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	private String identity;

	
	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	//原始密码与新密码
	private String _prePWD;
	private String _newPWD;
	private String _rePWD;

	
	///
	//数据库操作接口
	private  CRUD_SQL _mysql = new CRUD_SQL() ;
	///

	public String get_user() {
		return _user;
	}

	public void set_user(String _user) {
		this._user = _user;
	}

	public String get_pwd() {
		return _pwd;
	}

	public void set_pwd(String _pwd) {
		this._pwd = _pwd;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getLogin_jsResult() {
		return login_jsResult;
	}

	public void setLogin_jsResult(int login_jsResult) {
		this.login_jsResult = login_jsResult;
	}

	public int getEditpwd_jsResult() {
		return editpwd_jsResult;
	}

	public void setEditpwd_jsResult(int editpwd_jsResult) {
		this.editpwd_jsResult = editpwd_jsResult;
	}

	public JSONObject getLogout_jsResult() {
		return logout_jsResult;
	}
	public JSONObject getValid_jsResult() {
		return valid_jsResult;
	}

	public void setValid_jsResult(JSONObject valid_jsResult) {
		this.valid_jsResult = valid_jsResult;
	}
	public void setLogout_jsResult(JSONObject logout_jsResult) {
		this.logout_jsResult = logout_jsResult;
	}

	public JSONObject getGet_jsResult() {
		return get_jsResult;
	}

	public void setGet_jsResult(JSONObject get_jsResult) {
		this.get_jsResult = get_jsResult;
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

	public JSONObject getEdit_jsResult() {
		return edit_jsResult;
	}

	public void setEdit_jsResult(JSONObject edit_jsResult) {
		this.edit_jsResult = edit_jsResult;
	}

	public JSONObject getRemove_jsResult() {
		return remove_jsResult;
	}

	public void setRemove_jsResult(JSONObject remove_jsResult) {
		this.remove_jsResult = remove_jsResult;
	}


	public String get_prePWD() {
		return _prePWD;
	}

	public void set_prePWD(String _prePWD) {
		this._prePWD = _prePWD;
	}

	public String get_newPWD() {
		return _newPWD;
	}

	public void set_newPWD(String _newPWD) {
		this._newPWD = _newPWD;
	}
	public String get_rePWD() {
		return _rePWD;
	}

	public void set_rePWD(String _rePWD) {
		this._rePWD = _rePWD;
	}
	public CRUD_SQL get_mysql() {
		return _mysql;
	}

	public void set_mysql(CRUD_SQL _mysql) {
		this._mysql = _mysql;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	
	
	//登陆业务处理函数
	public String login() throws Exception {
		
		System.out.println("login in");
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		MessageDigest md = MessageDigest.getInstance("MD5");		
		if(_mysql.connectMySQL("smarthome")!= true)
			return SUCCESS;
		if(_mysql.search2MySQL("select * from admins where name = '" + _user + "' ", _lists) != true){
			return SUCCESS;
		}
		System.out.println("The result of _lists:"+_lists);
		if(_mysql.closeMySQL() != true)
			return SUCCESS;
		if(_lists.size() == 0)
			return SUCCESS;
		Map<String, String> map = (Map<String, String>) _lists.get(0);
		String user = map.get("name");
		String pwd = map.get("password");
		String identity = map.get("identity");
		String province = map.get("province");
		String city = map.get("city");
		String zone = map.get("zone");
		md.update(pwd.getBytes());
		//pwd = new String(md.digest());
		byte [] bytes = md.digest();
	    String tempPwd = "";
	    String str;
		for(int i = 0; i < bytes.length; i++){
			str = Integer.toHexString(bytes[i] & 0xFF);
			tempPwd += (str.length() == 1? "0" + str : str);
		}
		if(_pwd.equals(tempPwd) != true){
			return SUCCESS;		
			}
		
		ActionContext.getContext().getSession().put("user", _user);
		ActionContext.getContext().getSession().put("identity", identity);
		ActionContext.getContext().getSession().put("province", province);
		ActionContext.getContext().getSession().put("city", city);
		ActionContext.getContext().getSession().put("zone", zone);
		//ActionContext.getContext().getSession().put("user", _user);
		
		login_jsResult = 1;
	
		return SUCCESS;
	}

	
	//显示管理员信息
	public String show() throws Exception {
		
		System.out.println("show admins");
	    user = (String) ActionContext.getContext().getSession().get("user");
		int nPage = Integer.parseInt(page);
		int nRows = Integer.parseInt(rows);
		
		int offset = (nPage - 1) * nRows;
		String sql_limit = " limit " + offset + "," + rows;
		show_jsResult = new JSONObject();
		int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true)
			show_jsResult.put("error", 1);
		if( (nNum = _mysql.search2Count("select count(*) from admins")) != -1)
			show_jsResult.put("total", nNum);
		List<Map<?, ?>> listid=new ArrayList<Map<?,?>>();;
	   _mysql.search2MySQL("select * from admins where name= '"+user+"'" , listid);
	    int _id= Integer.parseInt((String)(listid.get(0).get("identity")));
	    String _province=(String)(listid.get(0).get("province"));
	    String _city=(String)(listid.get(0).get("city"));
	    String _zone=(String)(listid.get(0).get("zone"));
		//System.out.print("the information of user "+_id+_user+_city+_zone);
	    if(_id==0){
		if(_mysql.customSearch2MySQL("select name,province,city,zone,identity from admins" + sql_limit, _lists) != true)
			show_jsResult.put("error", 1);
		}
	    if(_id==1){
	    	if(_mysql.customSearch2MySQL("select name,province,city,zone,identity from admins where province= '"+_province+"'and identity>'"+_id+"'"+ sql_limit, _lists) != true)
				show_jsResult.put("error", 1);
	    }
	    if(_id==2){
	    	if(_mysql.customSearch2MySQL("select name,province,city,zone,identity from admins where province= '"+_province+"'and city='"+_city+"'and identity>'"+_id+"'"+ sql_limit, _lists) != true)
				show_jsResult.put("error", 1);
	    }
	    if(_mysql.closeMySQL() != true)
			show_jsResult.put("error", 1);
		
		if(_lists != null){
				show_jsResult.put("rows", _lists);
			    System.out.println(show_jsResult.toJSONString());
			}
		return SUCCESS;
	}

	//显示管理员信息业务处理函数
	public String get() throws Exception {
		System.out.println("get admin info");
	    user = (String) ActionContext.getContext().getSession().get("user");
		get_jsResult = new JSONObject();
		get_jsResult.put("error", 0);
		//ActionContext.getContext().getSession().put("user", _user);
		//String user = (String) ActionContext.getContext().getSession().get("user");
		System.out.println("user is :"+user);
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();	
		if(_mysql.connectMySQL("smarthome")!= true)
			get_jsResult.put("error", 1);
		if(_mysql.search2MySQL("select * from admins where name = '" + user + "' ", _lists) != true){
			get_jsResult.put("error", 1);
		}
		if(_mysql.closeMySQL() != true)
			show_jsResult.put("error", 1);
		
		if(_lists.size() != 0){
			get_jsResult.put("identity", Integer.parseInt((String)_lists.get(0).get("identity")));
		}
		  int _id= Integer.parseInt((String)_lists.get(0).get("identity"));
		  if(_id==0)get_jsResult.put("identityName", "超级");
		  else  if(_id==1)get_jsResult.put("identityName", "省级");
		  else  if(_id==2)get_jsResult.put("identityName", "市级");
		  else  get_jsResult.put("identityName", "区级");
		  
		if(_user!= null)
			get_jsResult.put("admin", user);
		else
			get_jsResult.put("admin", null);
		
		return SUCCESS;
	}

	//管理员修改密码
	public String editpwd()throws Exception {
		
		System.out.println("admin edit pwd");
		String tempUser = (String) ActionContext.getContext().getSession().get("user");
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		System.out.print(_newPWD+" and "+tempUser);
		if(!(_newPWD.equals(_rePWD))){return SUCCESS;}
		if(_mysql.connectMySQL("smarthome")!= true)
			return SUCCESS;
		if(_mysql.search2MySQL("select * from admins where name = '" + tempUser + "' and password = '" + _prePWD + "'", _lists) != true){
			return SUCCESS;
		}
		if(_mysql.closeMySQL() != true)
			return SUCCESS;
		if(_lists.size() == 0)
			return SUCCESS;
		String sql = "update admins set password = '" + _newPWD + "' where name = '" + tempUser  + "' and password = '" +_prePWD + "'" ;
		System.out.println(sql);
		
		if(_mysql.connectMySQL("smarthome")!= true)
			return SUCCESS;
		if(_mysql.update2MySQL(sql) != true)
			return SUCCESS;
		if(_mysql.closeMySQL() != true)
			return SUCCESS;
		editpwd_jsResult = 1;
		return  SUCCESS;
	}
	//管理员登出业务处理函数
		public String logout() throws Exception {
			
			System.out.println("logout");
			
			logout_jsResult = new JSONObject();
			logout_jsResult.put("ret", 1);
			String user = (String)ActionContext.getContext().getSession().get("user");
			if(user != null){
				ActionContext.getContext().getSession().remove("user");

			}
			return SUCCESS;
		}
//添加管理员信息,管理员默认密码为88888888
public String add() throws Exception{
	
	System.out.println("add admin");
	String sql = "insert into admins (name,password,province,city,zone,identity)values('" + name + "','" +"88888888" + "','" + province + "','"+city+"','"+zone+"','"+identity+ "')";
	add_jsResult = new JSONObject();
	user = (String) ActionContext.getContext().getSession().get("user");
	int _identity=(Integer.parseInt(identity));
	add_jsResult.put("error", 0);
	add_jsResult.put("errorMsg", "添加管理员失败！");
	System.out.println("identity is "+identity+"p"+province+"c"+city+"z"+zone);
	List<Map<?, ?>> listid=new ArrayList<Map<?,?>>();;
	if(_mysql.connectMySQL("smarthome")!= true)
		add_jsResult.put("error", 1);
	_mysql.search2MySQL("select * from admins where name= '"+user+"'" , listid);
	//System.out.println("listid in"+listid);
	 int _id= Integer.parseInt((String)(listid.get(0).get("identity")));
     String _province=(String)(listid.get(0).get("province"));
	 String _city=(String)(listid.get(0).get("city"));
	 
     switch(_identity){
     case 0:if(_id==0){if(province.equals("")&&city.equals("")&&zone.equals("")){
		if(_mysql.add2MySQL(sql) != true)
			add_jsResult.put("error", 1);}
       else {add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "添加超级管理员，无需省市区信息！");}}
     else {add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "添加超级管理员，您无权限添加！");} ;break;
     
     case 1:if(!(province.equals(""))&&city.equals("")&&zone.equals("")){
	   if(_id==0){
		if(_mysql.add2MySQL(sql) != true)
			add_jsResult.put("error", 1);}
	    if(_id==1||_id==2||_id==3){add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "您无权添加省级管理员！");}}
     else{add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "输入有误，请重新输入！");};break;
     
     case 2:if(!(province.equals(""))&&!(city.equals(""))&&zone.equals("")){
	   if(_id==0){if(_mysql.add2MySQL(sql) != true)
			add_jsResult.put("error", 1);}
	   if(_id==1){if(_province.equals(province)){
		if(_mysql.add2MySQL(sql) != true)
			add_jsResult.put("error", 1);}else{add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "不能添加其他省份管理员！");}}
	    if(_id==2||_id==3){add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "您无权添加市级管理员！");}}
        else {add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "输入有误，请重新输入！");};break;
    
     case 3:if(!(province.equals(""))&&!(city.equals(""))&&!(zone.equals(""))){
    	     if(_id==3){add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "您无权添加区级管理员！");}
			 if(_id==0){if(_mysql.add2MySQL(sql) != true)
					add_jsResult.put("error", 1);}
			   if(_id==1){if(_province.equals(province)){
				if(_mysql.add2MySQL(sql) != true)
					add_jsResult.put("error", 1);}else{add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "不能添加其他省份管理员！");}}
			   if(_id==2){if(_province.equals(province)&&_province.equals(province)){
					if(_mysql.add2MySQL(sql) != true)
						add_jsResult.put("error", 1);
					}else{add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "不能添加其他省市管理员！");}}}
     else{add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "请确保输入信息正确！");
   if(_id==3){add_jsResult.put("error", 1);add_jsResult.put("errorMsg", "您无权添加区级管理员！");}};break;		
   }
 	if(_mysql.closeMySQL() != true)
		add_jsResult.put("error", 1);
	return SUCCESS;
}
//管理管理员信息
public String valid() throws Exception {
	System.out.println("valid admin");
	valid_jsResult = new JSONObject();
	valid_jsResult.put("valid", -1);
	if(_mysql.connectMySQL("smarthome")!= true)
		add_jsResult.put("error", 1);
	System.out.println(1);
	 String id = ServletActionContext.getRequest().getParameter("id");
		id = new String(id.getBytes("ISO-8859-1"), "gb2312");
		System.out.println(1);
	int total=_mysql.search2Count("select count(*) from admins where name= '"+id+"'");
	valid_jsResult.put("valid", total);
	System.out.println(total);
	if(_mysql.closeMySQL() != true)
		valid_jsResult.put("error", 1);
	return SUCCESS;
}
//管理管理员信息
public String edit() throws Exception {
	
	System.out.println("edit admin");
	int _id=Integer.parseInt(identity);
	String user=(String)ActionContext.getContext().getSession().get("user");
	List<Map<?, ?>> listid=new ArrayList<Map<?,?>>();;
	if(_mysql.connectMySQL("smarthome")!= true)
		add_jsResult.put("error", 1);
	_mysql.search2MySQL("select * from admins where name= '"+user+"'" , listid);
	System.out.println("listid in"+listid);
	 int _identity= Integer.parseInt((String)(listid.get(0).get("identity")));
     String _province=(String)(listid.get(0).get("province"));
	 String id = ServletActionContext.getRequest().getParameter("id");
		id = new String(id.getBytes("ISO-8859-1"), "gb2312");
	System.out.println(id);
	String sql = "update admins set name = '" + name + "',province = '"+province+"',city = '"+city+"',zone = '"+zone+"',identity = '"+identity+"' where name = '" + id + "'";
	System.out.println(sql);
	edit_jsResult = new JSONObject();
	
	edit_jsResult.put("error", 0);
	edit_jsResult.put("errorMsg", "编辑管理员失败！");
	switch(_identity){
	case 0:{if(_id==1&&city.equals("")&&!(province.equals(""))&&zone.equals("")){
	if(_mysql.update2MySQL(sql) != true)
		edit_jsResult.put("error", 1);}
	else if(_id==2&&!(city.equals(""))&&!(province.equals(""))&&zone.equals("")){
	if(_mysql.update2MySQL(sql) != true)
		edit_jsResult.put("error", 1);}
	else if(_id==3&&!(city.equals(""))&&!(province.equals(""))&&!(zone.equals(""))){
		if(_mysql.update2MySQL(sql) != true)
			edit_jsResult.put("error", 1);}
		else edit_jsResult.put("error", 1);};break;
	
	case 1:{if(_id==1||_id==0)edit_jsResult.put("error", 1);
	else if(_id==2){if(!(city.equals(""))&&_province.equals(province)&&zone.equals("")){	
		if(_mysql.update2MySQL(sql) != true)
			edit_jsResult.put("error", 1);
	}else edit_jsResult.put("error", 1);}
	else if(_id==3){if(!(city.equals(""))&&_province.equals(province)&&!(zone.equals(""))){
		if(_mysql.update2MySQL(sql) != true)
			edit_jsResult.put("error", 1);
	}else edit_jsResult.put("error", 1);}};break;
	
	case 2:{if(_id==0||_id==1||_id==2)edit_jsResult.put("error",1);
	else if(_id==3){if(!(city.equals("city"))&&_province.equals(province)&&!(zone.equals(""))){
		if(_mysql.update2MySQL(sql) != true)
			edit_jsResult.put("error", 1);
	}else edit_jsResult.put("error", 1);}};break;
		
	}
	if(_mysql.closeMySQL() != true)
		edit_jsResult.put("error", 1);
	return SUCCESS;
}

//删除管理员
public String remove() throws Exception {
	System.out.println("admin remove");
	remove_jsResult = new JSONObject();
	remove_jsResult.put("error", 0);
	remove_jsResult.put("errorMsg", "删除数据失败！！");	
	String id = ServletActionContext.getRequest().getParameter("id");
	id = new String(id.getBytes("ISO-8859-1"), "gb2312");
	System.out.println(id);
	String sql = "delete from admins where name = '" + id + "'";
	System.out.println(sql);	
	if(_mysql.connectMySQL("smarthome")!= true)
		remove_jsResult.put("error", 1);
	if(_mysql.delete2MySQL(sql)!= true)
		remove_jsResult.put("error", 1);
	if(_mysql.closeMySQL() != true)
		remove_jsResult.put("error", 1);
	return SUCCESS;
}
}