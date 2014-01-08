package lab.es.sigar;


import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class DfIoData {   
    long writeBytes;  
    long readBytes;  
    double writeSpeed;
	double readSpeed;
    static long lastwriteBytes;  
    static long lastreadBytes;  
	static boolean flag;
    public long getWriteBytes() {
		return writeBytes;
	}

	public void setWriteBytes(long writeBytes) {
		this.writeBytes = writeBytes;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public void setReadBytes(long readBytes) {
		this.readBytes = readBytes;
	}



	public double getWriteSpeed() {
		return writeSpeed;
	}

	public void setWriteSpeed(double writeSpeed) {
		this.writeSpeed = writeSpeed;
	}

	public double getReadSpeed() {
		return readSpeed;
	}

	public void setReadSpeed(double readSpeed) {
		this.readSpeed = readSpeed;
	}


	public DfIoData() {  
    	super();
    }  
  
    public void populate(Sigar sigar) throws SigarException {  
        FileSystem fslist[] = sigar.getFileSystemList();
        FileSystemUsage Usage=null; 
   
        for(int i=0;i<fslist.length;i++){
        	if(fslist[i].getType()==2){
        	Usage=sigar.getFileSystemUsage(fslist[i].getDirName());
        	//System.out.println(fslist[i].getDirName());
        	readBytes+=Usage.getDiskReadBytes();
        	writeBytes+=Usage.getDiskWriteBytes();}
        }
        if(flag){
        	//System.out.println(readBytes);
        	//System.out.println(lastreadBytes);
    		readSpeed=(double)(readBytes-lastreadBytes)/2048.0;
    		writeSpeed=(double)(writeBytes-lastwriteBytes)/2048.0;
    		//System.out.println(readSpeed);
    		//System.out.println(writeSpeed);
    		}else
    		{
    			readSpeed=0;
    			writeSpeed=0;
    			flag=true;
    		}
    		lastreadBytes=readBytes;
    		lastwriteBytes=writeBytes;

    }  
  
    public static DfIoData gather(Sigar sigar) throws SigarException {  
        DfIoData data = new DfIoData();  
        data.populate(sigar);  
        return data;  
    }


	public static void main(String[] args) throws InterruptedException{
		
		Sigar si=new Sigar();
		
		while(true){try {
			DfIoData.gather(si);
			Thread.sleep(2000);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	
}}