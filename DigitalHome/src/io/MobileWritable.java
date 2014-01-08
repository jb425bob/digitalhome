package io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;

import sql.CRUD_SQL;

public class MobileWritable implements Writable{

	private byte[] content = null;
	private int length = 0;
	private CRUD_SQL _mysql = new CRUD_SQL();
	private String sntag;
	private String channelid;
	private String userid;
	public MobileWritable(int length){
		this.length =length;
		content = new byte[length];
	}
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		//System.out.println("MobileWritable readFields");
		in.readFully(content);
	}

	@Override
	public void process(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		try{
			String str = new String(content, 0, length, "utf-8");
			JSONObject jsonMobile = JSONObject.parseObject(str);
			System.out.println(str);
			//需要用户名 userName
			//需要deviceType
		    sntag = jsonMobile.getString("tag");
		    channelid = jsonMobile.getString("channelid");
		    userid = jsonMobile.getString("userid");
			
		} catch(UnsupportedEncodingException e){
			//e.printStackTrace();
			System.out.println("MobileWritable encode exception " + e);
		} catch (Exception e){
			System.out.println("MobileWritable parse JSON exception " + e);
			return ;
		}
		if(_mysql.connectMySQL("smarthome")!= true){
			_mysql.closeMySQL();
			System.out.println("connect mysql smarthome exception");
		}
		String sql = "insert into  family_notify_lists(sntag,channelid,userid) values ('" + sntag + "','" +
		channelid + "','" + userid + "')";
		if(_mysql.add2MySQL(sql) != true)
			System.out.println("add notify_lists exception");
		if(_mysql.closeMySQL() != true){
			System.out.println("close exception");
		}
	}

}
