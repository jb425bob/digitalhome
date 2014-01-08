package lab.es.sigar;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class CpuData {   
    private double cpuPerc;  
    
    public CpuData() {  
    	super();
    }  
  
    public void populate(Sigar sigar) throws SigarException {  
        CpuPerc[] percList = sigar.getCpuPercList();  
        double total=0;
        int cpuNum=percList.length;
        for(int i=0;i<percList.length;i++)
        	total+=percList[i].getCombined();
        cpuPerc=total*100/cpuNum;

    }  
  
    public static CpuData gather(Sigar sigar) throws SigarException {  
        CpuData data = new CpuData();  
        data.populate(sigar);  
        return data;  
    }




	public double getCpuPerc() {
		return cpuPerc;
	}

	public void setCpuPerc(double cpuPerc) {
		this.cpuPerc = cpuPerc;
	}

	public static void main(String[] args) throws InterruptedException{
		
		Sigar si=new Sigar();
		
		while(true){try {
			System.out.println(CpuData.gather(si).cpuPerc);
			Thread.sleep(2000);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	
}}