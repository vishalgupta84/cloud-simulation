import java.util.*;
import org.cloudbus.cloudsim.*;
public class ScheduleTask {
//	private static List<Cloudlet> cloudletListArray;
	private static List<ArrayList<Cloudlet>> cloudletListArray ;
	private static List<List<Vm>> vmListArray;
	private static List<DatacenterBroker> brokerListArray;
	private static void Initialize() {
		int num_cloudlets=15,num_vms=10;
		int[] brokerId= new int[num_cloudlets];
		int temp_value_for_now=1;
		int vmid;
		int mips ;
		long size=10000; //image size (MB)
		int ram=512; //vm memory (MB)
		long bw=1000;
		int pesNumber=1; //number of cpus
		String vmm = "Xen"; //VMM name
		for(int i=0;i<num_cloudlets;i++) {
			DatacenterBroker broker=createBroker(i);
			brokerListArray.add(broker);
			brokerId[i]=brokerListArray.get(i).getId();
		}
		for(int i=0;i<num_vms;i++) {
			vmid=i;
			mips=(int) Math.floor(Math.random()*500);
			Vm vm = new Vm(vmid, temp_value_for_now, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			List<Vm> vms=new ArrayList<Vm>();
			vms.add(vm);
			vmListArray.add(vms);
		}
		for(int i=0;i<num_cloudlets;i++) {
			
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
}
