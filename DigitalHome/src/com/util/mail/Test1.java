package com.util.mail;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;



public class Test1 {
	/** 
	*用spring mail 发送邮件,依赖jar：spring.jar，activation.jar，mail.jar  
	 * @throws MessagingException 
	*/  
	  public static void main(String args[]) throws MessagingException{
		  Test1 test=new Test1();
		  test.sendMutiMessage();
	  }
//	public static void sendFileMail() throws MessagingException {  
//	        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();  
//	  
//	        // 设定mail server  
//	        senderImpl.setHost("smtp.sina.com.cn");  
//	        senderImpl.setUsername("kimichen123@sina.com");  
//	        senderImpl.setPassword("kimi3213.1415926");  
//	        // 建立HTML邮件消息  
//	        MimeMessage mailMessage = senderImpl.createMimeMessage();  
//	        // true表示开始附件模式  
//	        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");  
//	  
//	        // 设置收件人，寄件人  
//	        messageHelper.setTo("292560516@qq.com");  
//	        messageHelper.setFrom("kimichen123@sina.com");  
//	        messageHelper.setSubject("测试邮件！");  
//	        // true 表示启动HTML格式的邮件  
//	        messageHelper.setText("<html><head></head><body><h1>你好：附件！！</h1></body></html>", true);  
//	  
//	      
//	        FileSystemResource file = new FileSystemResource(new File("d:/log.txt"));  
// 
//	          
//	        try {  
//	            //附件名有中文可能出现乱码  
//	            messageHelper.addAttachment(MimeUtility.encodeWord("log.txt"), file);  
//	        } catch (UnsupportedEncodingException e) {  
//	            e.printStackTrace();  
//	            throw new MessagingException();  
//	        }  
//	        // 发送邮件  
//	        senderImpl.send(mailMessage);  
//	        System.out.println("邮件发送成功.....");  
//	  
//	    }  
	public static void sendMutiMessage() {  
		  
        MultiPartEmail email = new MultiPartEmail();  
 
        String[] multiPaths = new String[] { "d:/output.xls" };  
  
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
            attachment.setDescription("Picture of John");  
            list.add(attachment);  
        }  
  
        try {  
            // 这里是发送服务器的名字：  
            email.setHostName("smtp.sina.com.cn");  
            // 编码集的设置  
            email.setCharset("utf-8");  
            // 收件人的邮箱                 
            email.addTo("292560516@qq.com");  
            // 发送人的邮箱  
            email.setFrom("kimichen123@sina.com");  
            // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码  
            email.setAuthentication("kimichen123@sina.com", "kimi3213.1415926");  
            email.setSubject("设备监控数据");  
            // 要发送的信息  
            email.setMsg("详情请见附件");  
  
            for (int a = 0; a < list.size(); a++) //添加多个附件     
            {  
                email.attach(list.get(a));  
            }  
            // 发送  
            email.send();  
        } catch (EmailException e) {  
            e.printStackTrace();  
        }  
    } 
}
