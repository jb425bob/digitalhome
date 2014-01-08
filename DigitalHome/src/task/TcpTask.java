package task;

import java.io.IOException;

public class TcpTask implements Runnable{

	public Server tcpServer;
	public TcpTask(Server server){
		this.tcpServer = server;
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			tcpServer.start();
			tcpServer.join();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("TcpTask IOException " + e);
		} catch (InterruptedException e){
			//e.printStackTrace();
			System.out.println("TcpTask InterruptedException " + e);
		}
		System.out.println("TcpTask is exiting");
	}

}
