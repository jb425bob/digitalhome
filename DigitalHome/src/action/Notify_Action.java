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

import org.apache.struts2.ServletActionContext;

import notify.BroadcastPushNotify;
import notify.PushNotify;
import notify.TagPushNotify;
import notify.UnicastPushNotify;

import sql.CRUD_SQL;

import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Notify_Action extends ActionSupport{

	
	//notify
	PushNotify pushNotify;
	//POST parameters;
	private String page;
	private String rows;
	private String tag;
	private String content;
	private String type;
	
	//Response parameters;
	
	private JSONObject sendNotify_jsResult;
	private JSONObject showGroup_jsResult;
	private JSONObject showUser_jsResult;
	private JSONObject showNotifies_jsResult;
	private JSONObject delete_jsResult;
	
	
	
	private CRUD_SQL _mysql = new CRUD_SQL();
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JSONObject getSendNotify_jsResult() {
		return sendNotify_jsResult;
	}
	public void setSendNotify_jsResult(JSONObject sendNotify_jsResult) {
		this.sendNotify_jsResult = sendNotify_jsResult;
	}
	public JSONObject getShowGroup_jsResult() {
		return showGroup_jsResult;
	}
	public void setShowGroup_jsResult(JSONObject showGroup_jsResult) {
		this.showGroup_jsResult = showGroup_jsResult;
	}
	public JSONObject getShowUser_jsResult() {
		return showUser_jsResult;
	}
	public void setShowUser_jsResult(JSONObject showUser_jsResult) {
		this.showUser_jsResult = showUser_jsResult;
	}
	public JSONObject getShowNotifies_jsResult() {
		return showNotifies_jsResult;
	}
	public void setShowNotifies_jsResult(JSONObject showNotifies_jsResult) {
		this.showNotifies_jsResult = showNotifies_jsResult;
	}
	
	
	
	public JSONObject getDelete_jsResult() {
		return delete_jsResult;
	}
	public void setDelete_jsResult(JSONObject delete_jsResult) {
		this.delete_jsResult = delete_jsResult;
	}
	
	
	public String sendNotify() throws Exception {
		//type : 1 所有人
		//type ： 2 组
		//type ： 3 个人
		System.out.println("type" + type);
		System.out.println("content: " + content);
		System.out.println("tag " + tag);
		sendNotify_jsResult = new JSONObject();
		sendNotify_jsResult.put("error", 0);
		String tags = tag;
		int result = 0;
		int nType = Integer.parseInt(type);
		int send_state = 1;
		System.out.println("nType:" + nType);
		if(nType == 1){
			pushNotify = new BroadcastPushNotify();
			pushNotify.setDeviceType(3);
			result = pushNotify.sendNotify(content);//推送
			pushNotify.setDeviceType(4);
			result = pushNotify.sendNotify(content);//推送
			tags = "所有人";
		} else if(nType == 2){
			pushNotify = new TagPushNotify(tag);
			pushNotify.setDeviceType(3);
			result = pushNotify.sendNotify(content);//推送
			pushNotify.setDeviceType(4);
			result = pushNotify.sendNotify(content);//推送
		} else if(nType == 3){
			String [] strs = tag.split(":");
			pushNotify = new UnicastPushNotify(strs[1], strs[0]);
			pushNotify.setDeviceType(3);
			result = pushNotify.sendNotify(content);//推送
			pushNotify.setDeviceType(4);
			result = pushNotify.sendNotify(content);//推送
		} else{
			System.out.println("buhui daozheli ba");
			return SUCCESS;
		}
		if(result == -1){
			send_state = 0;
			sendNotify_jsResult.put("error", 1);
		}
		if(_mysql.connectMySQL("smarthome")!= true){
			System.out.println("connect");
					_mysql.closeMySQL();
					return SUCCESS;
		}
		String sql01 = "insert into family_notifies(content,state,tag,ranges) values('" + content +
				"'," + send_state + ",'" + tags + "'," + type + ")";
		System.out.println(sql01);
		if(_mysql.add2MySQL(sql01) != true)
			sendNotify_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			sendNotify_jsResult.put("error", 1);
		
		return SUCCESS;
	}
	public String showGroup() throws Exception {
		
		
		int nPage = Integer.parseInt(page);
		int nRows = Integer.parseInt(rows);
		int offset = (nPage - 1) * nRows;

		String sql_limit = " limit " + offset + "," + rows;
		showGroup_jsResult = new JSONObject();
		int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			showGroup_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		if( (nNum = _mysql.search2Count("select count(distinct sntag) from family_notify_lists" )) != -1)
			showGroup_jsResult.put("total", nNum);
		if(_mysql.search2MySQL("select snuser,sncode from family_gates where snuser in (select sntag from family_notify_lists)" + sql_limit, _lists) != true)
			showGroup_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			showGroup_jsResult.put("error", 1);
		
		if(_lists != null){
			
				showGroup_jsResult.put("rows", _lists);
			   // System.out.println(show_jsResult.toJSONString());
			}
		return SUCCESS;
	}
	public String showUser() throws Exception {
		int nPage = Integer.parseInt(page);
		int nRows = Integer.parseInt(rows);
		int offset = (nPage - 1) * nRows;

		String sql_limit = " limit " + offset + "," + rows;
		showUser_jsResult = new JSONObject();
		int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			showUser_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		if( (nNum = _mysql.search2Count("select count(*) from family_notify_lists " )) != -1)
			showUser_jsResult.put("total", nNum);
		if(_mysql.search2MySQL("select sntag,channelid,userid from family_notify_lists " + sql_limit, _lists) != true)
			showUser_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			showUser_jsResult.put("error", 1);
		
		if(_lists != null){
			
				showUser_jsResult.put("rows", _lists);
			   // System.out.println(show_jsResult.toJSONString());
			}
		return SUCCESS;
		
	}
	public String showNotifies() throws Exception {
		int nPage = Integer.parseInt(page);
		int nRows = Integer.parseInt(rows);
		int offset = (nPage - 1) * nRows;

		String sql_limit = " limit " + offset + "," + rows;
		showNotifies_jsResult = new JSONObject();
		int nNum = -1;
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			showNotifies_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		if( (nNum = _mysql.search2Count("select count(*) from family_notifies " )) != -1)
			showNotifies_jsResult.put("total", nNum);
		if(_mysql.search2MySQL("select id, content, sendtime, state, tag, ranges from family_notifies " + sql_limit, _lists) != true)
			showNotifies_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			showNotifies_jsResult.put("error", 1);
		
		if(_lists != null){
			
				showNotifies_jsResult.put("rows", _lists);
			   // System.out.println(show_jsResult.toJSONString());
			}
		return SUCCESS;
		
	}
	public String delete()throws Exception{
		delete_jsResult = new JSONObject();
		
		delete_jsResult.put("error", 0);
		delete_jsResult.put("errorMsg", "删除推送消息失败！！");
		String id = (String)ServletActionContext.getRequest().getParameter("id");
		
		if(_mysql.connectMySQL("smarthome")!= true){
			
			delete_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql = "delete from family_notifies where id=" + id;
		System.out.println(sql);
		if(_mysql.update2MySQL(sql) != true)
			delete_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			delete_jsResult.put("error", 1);
		
		return SUCCESS;
	}

}
