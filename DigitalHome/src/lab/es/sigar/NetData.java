package lab.es.sigar;



import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Tcp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @author flyingsheep
 * @member netinfo,netconfig
 * @function Gather information of network interface
 */
public class NetData{

	String ip;
	String name;
	long rxbytes;
	long txbytes;
	double rxspeed;
	double txspeed;
	static long  lastrxbytes;
	static long  lasttxbytes;
	static boolean flag;
	long netNums;
	Tcp request;
	
	public double getRxspeed() {
		return rxspeed;
	}
	public void setRxspeed(long rxspeed) {
		this.rxspeed = rxspeed;
	}
	public double getTxspeed() {
		return txspeed;
	}
	public void setTxspeed(long txspeed) {
		this.txspeed = txspeed;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getRxbytes() {
		return rxbytes;
	}
	public void setRxbytes(long rxbytes) {
		this.rxbytes = rxbytes;
	}
	public long getTxbytes() {
		return txbytes;
	}
	public void setTxbytes(long txbytes) {
		this.txbytes = txbytes;
	}
	
	public long getNetNums() {
		return netNums;
	}
	public void setNetNums(long netNums) {
		this.netNums = netNums;
	}
	public void setRxspeed(double rxspeed) {
		this.rxspeed = rxspeed;
	}
	public void setTxspeed(double txspeed) {
		this.txspeed = txspeed;
	}
	public NetData() throws SigarException {
		super();
		
	}
	public void populate(Sigar sigar) throws SigarException {
		String[] netList = sigar.getNetInterfaceList();
		
		int count = 0;
	
		request=sigar.getTcp();
		
		netNums=request.getCurrEstab();
		//System.out.println(netNums);
		for(int i=0;i<netList.length;i++){
			NetInterfaceStat netStat = sigar.getNetInterfaceStat(netList[i]);
			NetInterfaceConfig netName = sigar.getNetInterfaceConfig(netList[i]);
			if(netName.getAddress().equals("127.0.0.1") || netName.getAddress().equals("0.0.0.0"))
				continue;
		
		txbytes+=netStat.getTxBytes();
		rxbytes+=netStat.getRxBytes();
		}
	
		
		
		if(flag){
		txspeed=(double)(txbytes-lasttxbytes)/2048.0;
		rxspeed=(double)(rxbytes-lastrxbytes)/2048.0;
		}else
		{
			txspeed=0;
			rxspeed=0;
			flag=true;
		}
		lasttxbytes=txbytes;
		lastrxbytes=rxbytes;
		//System.out.println("netinterface COUNT:"+count);
		//================================================================================================================
		
	//	System.out.println(JSON.toJSONString(LastNetwork.lastNetwork));
			
	}  
	public static NetData gather(Sigar sigar) throws SigarException{  
        NetData data = new NetData();
        data.populate(sigar); 
        return data;  
} 
	public static void main(String[] args) throws SigarException {
	JSONObject memory=new JSONObject();

	try {
		while(true){
			Sigar si = new Sigar();
			String s = JSON.toJSONString(NetData.gather(si));
		
			  System.out.println(s);
			//System.out.println(s);
			Thread.sleep(2000);
		}
	} catch (SigarException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}}


