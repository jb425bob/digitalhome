package contextListener;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import task.Server;
import task.TcpTask;

public class MonitorService implements ServletContextListener{

	public Server tcpServer = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("server close");
		Enumeration<Driver> ed = DriverManager.getDrivers();
		while(ed.hasMoreElements()){
			Driver d = ed.nextElement();
			try{
				DriverManager.deregisterDriver(d);
				
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(tcpServer != null)
			tcpServer.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		 try {
			tcpServer = new Server("10.2.7.2", 20000, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 new Thread(new TcpTask(tcpServer)).start();
		
	}

}
