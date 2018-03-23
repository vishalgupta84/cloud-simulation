import java.util.*;
import org.cloudbus.cloudsim.*;
public class ScheduleTask {
//	private static List<Cloudlet> cloudletListArray;
	private static List<List<Cloudlet>> cloudletListArray ;
	private static List<List<Vm>> vmListArray;
	private static List<DatacenterBroker> brokerListArray;
	private static List<List<Double>> matrix,efficiency;
	private static List<Integer> Mips;
	private static List<Long> Length;
	private static int num_cloudlets,num_vms,numScheduledTask;
	private static List<Double> availableTime;
	
	private static void Set_Grid() {
		matrix=new ArrayList<List<Double>>(num_vms);
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++) {
				Double execution_time=((double)(Length.get(j)/Mips.get(i)));
				matrix.get(i).add(execution_time);
			}
		}
	}
	private static void Set_Efficiency() {
		efficiency=new ArrayList<List<Double>>(num_vms);
		Double[]min_efficiency=new Double[num_vms];
		for(int i=0;i<num_vms;i++) {
			min_efficiency[i]=matrix.get(i).get(0);
		}
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++)
				if(min_efficiency[i]<matrix.get(i).get(j))
					min_efficiency[i]=matrix.get(i).get(j);
		}
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++)
					efficiency.get(i).add(min_efficiency[i]/matrix.get(i).get(j));
		}
		for(int i=0;i<num_vms;i++) {
			availableTime.add(0.0);
		}
	}
	private static void Initialize() {
		num_cloudlets=15;
		num_vms=10;
		numScheduledTask=0;
		int[] brokerId= new int[num_cloudlets];
//		int broker_temp_value_for_now=1; // this needs to be figured out
		int vmid,cloudletId;
		int mips ;
		long size=10000; //image size (MB)
		int ram=512; //vm memory (MB)
		long bw=1000;
		long length = 40000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber=1; //number of cpus
		String vmm = "Xen"; //VMM name
		for(int i=0;i<num_vms;i++) {
			DatacenterBroker broker=createBroker(i);
			brokerListArray.add(broker);
			brokerId[i]=brokerListArray.get(i).getId();
		}
		for(int i=0;i<num_vms;i++) {
			vmid=i;
			mips=(int) Math.floor(Math.random()*512)+512;
			Vm vm = new Vm(vmid, brokerId[i], mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			List<Vm> vms=new ArrayList<Vm>();
			vms.add(vm);
			vmListArray.add(vms);
			Mips.add(mips);
		}
		UtilizationModel utilizationModel = new UtilizationModelFull();
		for(int i=0;i<num_cloudlets;i++) {
			cloudletId=i;
			length=(int) Math.floor(Math.random()*40000)+20000;
			fileSize=(int) Math.floor(Math.random()*300)+300;
			outputSize=(int) Math.floor(Math.random()*300)+300;
			Cloudlet cloudlet = new Cloudlet(cloudletId, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet.setUserId(broker_temp_value_for_now);
			List<Cloudlet> cloudlets=new ArrayList<Cloudlet>();
			cloudlets.add(cloudlet);
			cloudletListArray.add(cloudlets);
			Length.add(length);
		}
	}
	private static DatacenterBroker createBroker(int id){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker"+id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	public static void main(String[] args) {
		try {
			Initialize();
			Set_Grid();
			Set_Efficiency();
		}
		catch (Exception e){
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
}
