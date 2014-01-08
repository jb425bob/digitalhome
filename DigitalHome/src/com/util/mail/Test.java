package com.util.mail;

public class Test {
	public static void main(String[] args){  
        //这个类主要是设置邮件  
     MailSenderInfo mailInfo = new MailSenderInfo();   
     mailInfo.setMailServerHost("smtp.sina.com.cn");   
     mailInfo.setMailServerPort("25");   
     mailInfo.setValidate(true);   
     mailInfo.setUserName("kimichen123@sina.com");   
     mailInfo.setPassword("kimi3213.1415926");//您的邮箱密码   
     mailInfo.setFromAddress("kimichen123@sina.com");   
     mailInfo.setToAddress("292560516@qq.com");   
     mailInfo.setSubject("监控信息");   
     mailInfo.setContent("设置邮箱内容");   
        //这个类主要来发送邮件  
     SimpleMailSender sms = new SimpleMailSender();  
         sms.sendTextMail(mailInfo);//发送文体格式   
         sms.sendHtmlMail(mailInfo);//发送html格式  
   } 
}
