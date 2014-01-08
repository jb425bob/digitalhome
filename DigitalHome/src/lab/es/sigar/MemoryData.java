package lab.es.sigar;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class MemoryData {
	private double used;
	private double total;

	private JSONObject memory;
	public MemoryData() {
		super();
	}

	public JSONObject getMemory() {
		return memory;
	}

	public void setMemory(JSONObject memory) {
		this.memory = memory;
	}

	public void populate(Sigar sigar) throws SigarException {  
	       Mem memInfo = sigar.getMem();
	       used = memInfo.getUsedPercent();
	       total = memInfo.getTotal()/1024/1024;
	      
	       
	}  
	  
	public static MemoryData gather(Sigar sigar) throws SigarException {  
	    MemoryData memData = new MemoryData();
	    memData.populate(sigar);
	  
	    return memData;
	    
	}

	public double getUsed() {
		return used;
	}

	public void setUsed(double used) {
		this.used = used;
	}  
	
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total; 
	}

	public static void main(String[] args) {
		JSONObject memory=new JSONObject();
		try {
			while(true){
				Sigar si = new Sigar();
				String s = JSON.toJSONString(MemoryData.gather(si));
				  memory.put("used",MemoryData.gather(si).used);
				  memory.put("total",MemoryData.gather(si).total);
				  System.out.println(memory);
				//System.out.println(s);
				Thread.sleep(3000);
			}
		} catch (SigarException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		Sigar sigar = new Sigar();
//		try {
//			System.out.println("Memory usage:"+MemoryData.gather(sigar).used+"%");
//			System.out.println("Memory sum:"+MemoryData.gather(sigar).total/1024/1024+"M");
//		} catch (SigarException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
}	
