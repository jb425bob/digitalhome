package action;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;


import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.hyperic.sigar.Sigar;


import lab.es.sigar.CpuData;
import lab.es.sigar.DfIoData;
import lab.es.sigar.MemoryData;
import lab.es.sigar.NetData;
import com.alibaba.fastjson.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

public class Monitor_Action extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8813223478648786226L;
	private JSONObject show_jsResult;
	private JSONObject reset_jsResult;
	private JSONObject send_jsResult;
	private String email_add="";//邮件发送地址
	private static String log_add=Monitor_Action.class.getClassLoader().getResource("").getPath()+"log.txt";//默认log存储路径
	private static LinkedList<Double> cpuList=assign();//cpu使用率
	private static LinkedList<Double> memList=assign();//内存使用率
	private static LinkedList<Double> rxList=assign();//接收速率
	private static LinkedList<Double> txList=assign();//发送速率	
	private static LinkedList<Double> writeList=assign();//磁盘写速率
	private static LinkedList<Double> readList=assign();//磁盘读速率
	private static CpuData cpuData;
	private static MemoryData memoryData;
	private static NetData netData;
	private static DfIoData dfioData;
	private static Sigar sigar=new Sigar();
	private static DecimalFormat df = new DecimalFormat("#.00");
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Date date;	
	private static String aDate;
	private static double cpuPerc;
	private static double memPerc;
	private static double rxSpeed;
	private static double txSpeed;
	private static double writeSpeed;
	private static double readSpeed;
	private static double xnetNums;
	private static LinkedList<Double> assign() {
		// TODO Auto-generated method stub

		LinkedList<Double> init= new LinkedList<Double>();
		for(int i=0;i<10;i++)init.add((double) 0);
		return init;
	}

	public static LinkedList<Double> getCpuList() {
		return cpuList;
	}

	public static void setCpuList(LinkedList<Double> cpuList) {
		Monitor_Action.cpuList = cpuList;
	}
	public static LinkedList<Double> getMemList() {
		return memList;
	}
	public static void setMemList(LinkedList<Double> memList) {
		Monitor_Action.memList = memList;
	}

	public static LinkedList<Double> getRxList() {
		return rxList;
	}


	public static void setRxList(LinkedList<Double> rxList) {
		Monitor_Action.rxList = rxList;
	}

	public static LinkedList<Double> getTxList() {
		return txList;
	}

	public static void setTxList(LinkedList<Double> txList) {
		Monitor_Action.txList = txList;
	}

	public static LinkedList<Double> getWriteList() {
		return writeList;
	}

	public static void setWriteList(LinkedList<Double> writeList) {
		Monitor_Action.writeList = writeList;
	}

	public static LinkedList<Double> getReadList() {
		return readList;
	}

	public static void setReadList(LinkedList<Double> readList) {
		Monitor_Action.readList = readList;
	}

	public void setReset_jsResult(JSONObject reset_jsResult) {
		this.reset_jsResult = reset_jsResult;
	}
	public JSONObject getReset_jsResult() {
		return reset_jsResult;
	}

	public JSONObject getShow_jsResult() {
		return show_jsResult;
	}
	
	public void setShow_jsResult(JSONObject show_jsResult) {
		this.show_jsResult = show_jsResult;
	}


	public JSONObject getSend_jsResult() {
		return send_jsResult;
	}

	public void setSend_jsResult(JSONObject send_jsResult) {
		this.send_jsResult = send_jsResult;
	}

	public String getEmail_add() {
		return email_add;
	}

	public void setEmail_add(String email_add) {
		this.email_add = email_add;
	}

	public String show() throws Exception{
		System.out.println("show lookups");
		show_jsResult = new JSONObject();
		//System.out.println(log_add);
		//搜集数据以及当前时间
		cpuData=CpuData.gather(sigar);
		memoryData=MemoryData.gather(sigar); 
		netData=NetData.gather(sigar);
		dfioData=DfIoData.gather(sigar);
		date=new Date();	
		aDate = formatter.format(date);
		cpuPerc = Double.parseDouble(df.format(cpuData.getCpuPerc()));
		memPerc=Double.parseDouble(df.format(memoryData.getUsed()));
		rxSpeed=Double.parseDouble(df.format(netData.getRxspeed()));
		txSpeed=Double.parseDouble(df.format(netData.getTxspeed()));
		writeSpeed=Double.parseDouble(df.format(dfioData.getWriteSpeed()));
		readSpeed=Double.parseDouble(df.format(dfioData.getReadSpeed()));
		xnetNums=netData.getNetNums();
//		double cpuPerc =cpuData.getCpuPerc();
//		double memPerc=memoryData.getUsed();
//		double rxSpeed=netData.getRxspeed();
//		double txSpeed=netData.getTxspeed();
//		double writeSpeed=dfioData.getWriteSpeed();
//		double readSpeed=dfioData.getReadSpeed();
		//将最新数据存入数据链中
		cpuList.remove(0);
		cpuList.add(cpuPerc);
		
		memList.remove(0);
		memList.add(memPerc);
		
		txList.remove(0);
		txList.add(txSpeed);
		
		rxList.remove(0);
		rxList.add(rxSpeed);
	
		writeList.remove(0);
		writeList.add(writeSpeed);
		
		readList.remove(0);
		readList.add(readSpeed);
		
		show_jsResult.put("aDate", aDate);
		show_jsResult.put("cpuPerc", cpuPerc);
		show_jsResult.put("memPerc", memPerc);
		show_jsResult.put("rxSpeed", rxSpeed);
		show_jsResult.put("txSpeed", txSpeed);
		show_jsResult.put("writeDiskSpeed", writeSpeed);
		show_jsResult.put("readDiskSpeed", readSpeed);
		show_jsResult.put("xnetNums", xnetNums);
		//System.out.println(show_jsResult);
		//每隔半小时将数据写入log	
		if(aDate.endsWith(":00:00")||aDate.endsWith(":30:00")||aDate.endsWith(":00:01")||aDate.endsWith(":30:01")||aDate.endsWith(":00:02")||aDate.endsWith(":30:02")){	
		    PrintWriter log=null;
		    try {
		    	log = new PrintWriter(new FileWriter(log_add,true));
		    } catch (IOException e) {
		    	System.out.println("读取log失败");
		     	}
		 log.println(show_jsResult.toString());
		 log.close();
		 if(aDate.endsWith("00:00:00")||aDate.endsWith("00:00:01")||aDate.endsWith("00:00:02")){
			try {
				send();
				FileWriter relog=new FileWriter(new File(log_add));
				relog.write("");
				relog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		
   
		for(int i=0;i<10;i++){
			show_jsResult.put("cpuPerc"+i, cpuList.get(i));
			show_jsResult.put("memPerc"+i, memList.get(i));
			show_jsResult.put("txSpeed"+i, txList.get(i));
			show_jsResult.put("rxSpeed"+i, rxList.get(i));
			show_jsResult.put("readDiskSpeed"+i, readList.get(i));
			show_jsResult.put("writeDiskSpeed"+i, writeList.get(i));
		}
		System.out.println(show_jsResult);
		
		
		return SUCCESS;
	}
	//情况数据链中的数据
	public String reset() throws Exception{
		cpuList.clear();
		memList.clear();
		rxList.clear();
		txList.clear();
		writeList.clear();
		readList.clear();
		for(int i=0;i<10;i++){
		cpuList.add((double) 0);
		memList.add((double) 0);
		rxList.add((double) 0);
		txList.add((double) 0);
		writeList.add((double) 0);
		readList.add((double) 0);
		}
		return SUCCESS;
		
	}
	//发送邮件
	public String send() throws RowsExceededException, WriteException {
		send_jsResult = new JSONObject();
		send_jsResult.put("error", 1);
		email_add=(email_add.equals("")?"292560516@qq.com":email_add);
		System.out.println(email_add);
		Date xlsName=new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String  axlsName =Monitor_Action.class.getClassLoader().getResource("").getPath()+formatter.format(xlsName)+".xls";
		File tempFile=new File(axlsName);
		//System.out.println(axlsName);
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(tempFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("TestCreateExcel", 0); 

		//一些临时变量，用于写到excel中
		Label l=null;

		//System.out.println("1");
		//预定义的一些字体和格式，同一个Excel中最好不要有太多格式
		WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLUE); 
		WritableCellFormat headerFormat = new WritableCellFormat (headerFont); 

		WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 15, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.RED); 
		WritableCellFormat titleFormat = new WritableCellFormat (titleFont); 

		WritableFont detFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); 
		WritableCellFormat detFormat = new WritableCellFormat (detFont); 

		//剩下的事情，就是用上面的内容和格式创建一些单元格，再加到sheet中
		l=new Label(0, 0, "最近一天的信息采集", headerFormat);
		sheet.addCell(l);

		//add Title
		int column=0;
		l=new Label(column++, 2, "采集时间", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "CPU使用率", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "内存使用率", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "网络连接数", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "发送速率", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "接收速率", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "磁盘读取速率", titleFormat);
		sheet.addCell(l);
		l=new Label(column++, 2, "磁盘写入速率", titleFormat);
		sheet.addCell(l);

		int i=0;
		//String logFile = "./log.txt";
		//读入log数据，并写入excel中
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(log_add));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		String result[] = new String[8];
		String lines[]=new String[40];
		try {
			while ((line = in.readLine()) != null)
			{
			       lines=line.split("\"");
//	           System.out.println(lines.length);
//	           for(int j=0;j<lines.length;j++)
//	          System.out.println(lines[j]);
			
			result[0]=lines[3].substring(0, lines[3].length()-3);
			result[1]=lines[6].substring(1, lines[6].length()-1);
			result[2]=lines[8].substring(1, lines[8].length()-1);
			result[3]=lines[14].substring(1, lines[14].length()-1);
			result[4]=lines[12].substring(1, lines[12].length()-1);
			result[5]=lines[10].substring(1, lines[10].length()-1);
			result[6]=lines[16].substring(1, lines[16].length()-1);
			result[7]=lines[18].substring(1,lines[18].length()-1);
			
			column=0;
			
			l=new Label(column++, i+3, result[0], detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[1]+" %", detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[2]+" %", detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[7], detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[3]+"kb/s", detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[4]+"kb/s", detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[5]+"kb/s", detFormat);
			sheet.addCell(l);
			l=new Label(column++, i+3, result[6]+"kb/s", detFormat);
			sheet.addCell(l);
			i++;
				

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		//设置列的宽度
		column=0;
		sheet.setColumnView(column++, 20);

		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);
		sheet.setColumnView(column++, 20);

		try {
			workbook.write();
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("begin sending!");

		//开始发送邮件
		 MultiPartEmail email = new MultiPartEmail();  
		
	        String[] multiPaths = new String[] { axlsName };  
	        List<EmailAttachment> list = new ArrayList<EmailAttachment>();  
	        for (int j = 0; j < multiPaths.length; j++) {  
	            EmailAttachment attachment = new EmailAttachment();  
	            //判断当前这个文件路径是否在本地  如果是：setPath  否则  setURL;   
	            if (multiPaths[j].indexOf("http") == -1) {  
	                attachment.setPath(multiPaths[j]);  
	            } else {  
	                try {  
	                    attachment.setURL(new URL(multiPaths[j]));  
	                } catch (MalformedURLException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	            attachment.setDisposition(EmailAttachment.ATTACHMENT);  
	            attachment.setDescription("");  
	            list.add(attachment);  
	        }  
	        try {  
	            // 这里是发送服务器的名字：  
	            email.setHostName("smtp.sina.com.cn");  
	            // 编码集的设置  
	            email.setCharset("utf-8");  
	            // 收件人的邮箱                 
	            email.addTo(email_add);  
	            // 发送人的邮箱  
	            email.setFrom("jzsmarthome@sina.com");  
	            // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码  
	            email.setAuthentication("jzsmarthome@sina.com", "123456789");  
	            email.setSubject("今天的信息采集情况");  
	            // 要发送的信息  
	            email.setMsg("详情请见附件");  
	  
	            for (int a = 0; a < list.size(); a++) //添加多个附件     
	            {  
	                email.attach(list.get(a));  
	            }  
	            // 发送  
	            email.send();
	       
	            System.out.println("sending sussess");
	            //删除已经发送的excel
	            if(tempFile.exists())
	            	tempFile.delete();
				send_jsResult.put("error", 0);
	        } catch (EmailException e) {  
	            e.printStackTrace();  
	        }  
	     
	       return SUCCESS;
		
		
	    } 
	
	public static void main(String args[]) throws Exception{
		Monitor_Action a =new Monitor_Action();
		a.send();
	}
}
