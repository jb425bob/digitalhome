package io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sql.CRUD_SQL;

public class GateWayWritable implements Writable{

	private byte [] snNumber = new byte[28];
	private byte CMD ;
	private byte [] content = null;
	private int length;
	private int utf8_length = 0;
	private CRUD_SQL _mysql = new CRUD_SQL();
	private final String init_version = "[{name:\"control\",version:\"00.00.00.00\"},{name:\"room\",version:\"00.00.00.00\"},{name:\"monitor\",version:\"00.00.00.00\"},{name:\"scene\",version:\"00.00.00.00\"}]";
	public GateWayWritable(int length){
		this.length =length;
		content = new byte[length-29];
	}
	@Override
	public void write(DataOutput out) throws IOException {
	}

	private byte[] str2UTF8bytes(String str){
		int tempLength = str.length();
		byte[] bytes = new byte[tempLength * 3];
		int l = 0; 
	   	 for(int i=0; i<tempLength;i++){
	   		 
	   		 int code = str.charAt(i);
	   		 if(code >= 0x01 && code <= 0x7f){
	   			 bytes[l++] = (byte)code;
	   		 }else if(code <= 0x07FF){
	   			 bytes[l++] = (byte)(0xC0 | ((code >> 6) & 0x1F));
	   			 bytes[l++] = (byte)(0x80 | code & 0x3F);
	   		 }else {
	   			 bytes[l++] = (byte)(0xE0 | ((code >> 12) & 0x0F) );
	   			 bytes[l++] = (byte)(0x80 | ((code >> 6) & 0x3F));
	   			 bytes[l++] = (byte)(0x80 | (code & 0x3F));
	   		 }
	   	 }
	   	 utf8_length = l;
	   	 return bytes;
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
		CMD = in.readByte();
		//0x01 网关请求版本信息
		//0x02 网关发送新的版本信息version.json
		//0x03网关发送control.json
		//0x04网关发送monitor.json
		in.readFully(snNumber, 0, 28);
		if(CMD != (byte)0x01 ){
			in.readFully(content);
			System.out.println("received message : " + new String(content, "utf-8"));
			
		}
	}

	@Override
	public void process(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		List<Map<?,?>> _lists = new ArrayList<Map<?,?>>();
		String version = null;
		byte []bytes;
		if(_mysql.connectMySQL("smarthome")!= true){
			_mysql.closeMySQL();
			System.out.println("connect mysql smarthome exception");
		}
		 
		if(CMD == (byte)0x01){
			//获取版本信息
			if(_mysql.search2MySQL("select version from family_devices where sncode = '" + new String(snNumber) + "'", _lists) != true){
				_mysql.closeMySQL();
			System.out.println("select version exception");
			}
			if(_lists.size() < 1){
				String sql = "insert into family_devices(sncode,version) values ('" + new String(snNumber) + "','" + init_version + "')";
				System.out.println(sql);
				if(_mysql.add2MySQL(sql) != true)
					System.out.println("insert initial version exception");
				bytes = str2UTF8bytes(init_version);
			}else{
				Map<String, String> map = (Map<String, String>) _lists.get(0);
				version = map.get("version");
				bytes = str2UTF8bytes(version);
			}
			int send_length = utf8_length + 36;
			//System.out.println(send_length);
			byte [] send_bytes = new byte [send_length];
			send_bytes[0] = (byte)0x9d;
			send_bytes[1] = (byte)0x9d;
			send_bytes[2] = (byte)0x01;
			send_bytes[3] = (byte)((send_length >> 24)& 0xFF);
		    send_bytes[4] = (byte)((send_length >> 16 )& 0xFF);
			send_bytes[5] = (byte)((send_length >> 8) & 0xFF);
			send_bytes[6] = (byte)(send_length & 0xFF);
			send_bytes[7] = (byte)0x10;
			//System.out.printf("%x,%x,%x,%x", send_bytes[3],send_bytes[4],send_bytes[5],send_bytes[6]);
			for(int k=0; k<28;k++){
				send_bytes[8+k] = snNumber[k];
			}
			for(int k=0; k<utf8_length;k++){
				send_bytes[36+k] = bytes[k];
			}
			/*out.write(new byte[]{(byte)0x9d, (byte)0x9d, (byte)0x01});
			out.writeInt(utf8_length + 28 + 7 + 1);
			out.write(new byte[]{(byte)0x10});
			out.write(snNumber, 0, 28);*/
			out.write(send_bytes, 0, utf8_length + 36);
			System.out.println("send data length: " + (utf8_length + 28 + 7 + 1));
			System.out.println("write success");
		}else if(CMD == (byte)0x02){
			//版本信息反馈version.json
			String sql = "update family_devices set version='" + new String(content,"utf-8") + "' where sncode = '" + new String(snNumber) + "'";
			if(_mysql.update2MySQL(sql) != true)
				System.out.println("update version exception");
			
		}else if(CMD == (byte)0x03){
			//控制信息更新control.json
			String sql = "update family_devices set control='" + new String(content, "utf-8") + "' where sncode = '" + new String(snNumber) + "'";
			if(_mysql.update2MySQL(sql) != true)
				System.out.println("update control exception");
		}else if(CMD == (byte)0x04){
			//监控信息更新monitor.json
			String sql = "update family_devices set monitor='" + new String(content, "utf-8") +  "' where sncode = '" + new String(snNumber) + "'";
			if(_mysql.update2MySQL(sql) != true)
				System.out.println("update monitor exception");
		}
		if(_mysql.closeMySQL() != true){
			System.out.println("close exception");
		}
		/*try{
			String str = new String(content, 0, content.length, "utf-8");
			System.out.println(str);
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}*/
		
	}

}
