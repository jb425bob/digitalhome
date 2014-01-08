package action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.struts2.ServletActionContext;

import sql.CRUD_SQL;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Device_Action extends ActionSupport{


	private String sncode;
	
	private JSONObject show_jsResult;
	
	//getter and setter
	
	
	public JSONObject getShow_jsResult() {
		return show_jsResult;
	}
	public void setShow_jsResult(JSONObject show_jsResult) {
		this.show_jsResult = show_jsResult;
	}

	public String getSncode() {
		return sncode;
	}
	public void setSncode(String sncode) {
		this.sncode = sncode;
	}
	

	private  CRUD_SQL _mysql = new CRUD_SQL() ;//sql
	
	public String show() throws Exception { //显示网关信息
		
		
		String _sncode = (String)ServletActionContext.getRequest().getParameter("id");	
		System.out.println(_sncode);
		show_jsResult = new JSONObject();
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		if(_mysql.connectMySQL("smarthome")!= true){
			
			show_jsResult.put("error", 1);
			_mysql.closeMySQL();
			return SUCCESS;
		}
		String sql = "select control , monitor from family_devices where sncode='" + _sncode + "'";
		System.out.println(sql);
		if(_mysql.search2MySQL(sql, _lists) != true)
			show_jsResult.put("error", 1);
		if(_mysql.closeMySQL() != true)
			show_jsResult.put("error", 1);
		if(_lists.size() > 0){
			Map<String, String> map = (Map<String, String>) _lists.get(0);
			String control = map.get("control");
			JSONArray jsonArray = null;
			try{
				jsonArray= JSONObject.parseArray(control);
			}catch(Exception e){
				System.out.println("Parse control exception " + e);
			}
			Iterator <Object> itr = null;
			if(jsonArray != null){
				 itr=  jsonArray.iterator();
			}else{
				return SUCCESS;
			}
			HashMap<String,List<String>>hash = new HashMap<String, List<String>>();
			int count = 1;
			int id = 2;
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
			int keyLength = hash.size() + 2;
		
			JSONArray json_Result = new JSONArray();
			while(keyIterator.hasNext()){
				String key = keyIterator.next();
				jsonTemp = new JSONObject();
				jsonTemp.put("category", key);
				jsonTemp.put("id", id);
				jsonTemp.put("_parentId", 1);
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
			jsonTemp = new JSONObject();
			jsonTemp.put("category", "控制设备");
			jsonTemp.put("id", 1);
			jsonTemp.put("state", "closed");
			json_Result.add(jsonTemp);
			jsonTemp = new JSONObject();
			jsonTemp.put("category", "监控设备");
			jsonTemp.put("id", keyLength);
			jsonTemp.put("state", "closed");
			json_Result.add(jsonTemp);
			int parentId = keyLength ;
			keyLength ++;
			
			//控制设备
			String monitor = map.get("monitor");
			JSONArray jsonArray_monitor = null;
			try{
				jsonArray_monitor= JSONObject.parseArray(monitor);
			}catch(Exception e){
				System.out.println("Parse monitor exception " + e);
			}
			Iterator <Object> itr_monitor = null;
			if(jsonArray_monitor != null){
				 itr_monitor =  jsonArray_monitor.iterator();
					while(itr_monitor.hasNext()){
						JSONObject json = (JSONObject)itr_monitor.next();
						jsonTemp = new JSONObject();
						jsonTemp.put("name", json.getString("number"));
						jsonTemp.put("_parentId", parentId);
						jsonTemp.put("id", keyLength++);
						jsonTemp.put("category", json.getString("name"));
						json_Result.add(jsonTemp);
					}
			}
		/*	else{
				return SUCCESS;
			}*/
		
			//System.out.println(json_Result.toJSONString());
			show_jsResult.put("total", keyLength);
			show_jsResult.put("rows", json_Result);
			//System.out.println(show_jsResult.toJSONString());
		}
		
		return SUCCESS;
	}
	
	
}
