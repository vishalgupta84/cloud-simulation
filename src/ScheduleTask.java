import java.text.DecimalFormat;
import java.util.*;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
public class ScheduleTask {
//	private static List<Cloudlet> cloudletListArray;
	private static List<List<Cloudlet>> cloudletListArray;
	private static List<List<Vm>> vmListArray;
	private static List<DatacenterBroker> brokerListArray;
	private static List<List<Double>> matrix,efficiency;
	private static List<Integer> Mips;
	private static List<Long> lengths;
	private static int num_cloudlets,num_vms,numScheduledTask;
	private static List<Double> availableTime;
	private static double effThreshold=0;
	private static double INT_MAX=1000000,INT_MIN=-1;
	
	ScheduleTask() {
		
	}
	
	private static DatacenterBroker createBroker(int id){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker"+id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		System.out.println(broker);
		return broker;
	}
	
	private static void Set_Grid() {
		matrix=new ArrayList<List<Double>>();
		for(int i=0;i<num_vms;i++) {
			matrix.add(new ArrayList<Double>());
		}
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++) {
				Log.printLine("length = "+ j+ " " + lengths.get(j)); 
				Double execution_time=((double)(lengths.get(j)/Mips.get(i)));
				matrix.get(i).add(execution_time);
			}
		}
		
	}
	
	
	private static void Set_Efficiency() {
		efficiency=new ArrayList<List<Double>>();
		availableTime=new ArrayList<Double>();
		for(int i=0;i<num_vms;i++) {
			efficiency.add(new ArrayList<Double>());
		}

		Double[] min_efficiency=new Double[num_vms];
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
		try {
			num_cloudlets=5;
			num_vms=1;
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
//			cloudletListArray=new ArraList<>
			cloudletListArray=new ArrayList<List<Cloudlet>>();
			vmListArray=new ArrayList<List<Vm>>();
			brokerListArray=new ArrayList<DatacenterBroker>();
			Mips=new ArrayList<Integer>();
			lengths=new ArrayList<Long>();
			for(int i=0;i<num_vms;i++) {
				DatacenterBroker broker=createBroker(i);
//				System.out.println(broker);
	//			broker.submitVmList(vmListArray.get(i));
				brokerListArray.add(broker);
				brokerId[i]=brokerListArray.get(i).getId();
			}
			
			for(int i=0;i<num_vms;i++) {
				vmid=i;
//				mips=(int) Math.floor(Math.random()*512)+512;
				mips=250;
				Vm vm = new Vm(vmid, brokerId[i], mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
				List<Vm> vms=new ArrayList<Vm>();
				vms.add(vm);
				vmListArray.add(vms);
				Mips.add(mips);
			}
			
			for(int i=0;i<num_vms;i++) {
				brokerListArray.get(i).submitVmList(vmListArray.get(i));
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
				lengths.add(length);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static Boolean Completed() {
		if(numScheduledTask<num_cloudlets)
			return false;
		return true;
	}
	
	private static void addTask(int processor_number,int task_number) {
		numScheduledTask++;
		cloudletListArray.get(task_number).get(0).setUserId(processor_number);
		brokerListArray.get(processor_number).submitCloudletList(cloudletListArray.get(task_number));
		double new_time = availableTime.get(processor_number) + matrix.get(processor_number).get(task_number) ;
		availableTime.set(processor_number, new_time);
	}
	
	
	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips=1000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		//4. Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;


		//in this example, the VMAllocatonPolicy in use is SpaceShared. It means that only one VM
		//is allowed to run on each Pe. As each Host has only one Pe, only one VM can run on each Host.
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		); // This is our first machine

		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	public static void print() {
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++) {
				Log.print(matrix.get(i).get(j)+ " "); 
//				Double execution_time=((double)(lengths.get(j)/Mips.get(i)));
//				matrix.get(i).add(execution_time);
			}
			Log.print("\n");
		}
		for(int i=0;i<num_vms;i++) {
			for(int j=0;j<num_cloudlets;j++) {
					Log.print(efficiency.get(i).get(j)+ " ");
			}
			Log.print("\n");
		}
	}
	public static void main(String[] args) {
		try {
			ScheduleTask schedular=new ScheduleTask();
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events
			int num_user=1;
			// Initialize the CloudSim library
			
			CloudSim.init(num_user, calendar, trace_flag);
			Log.printLine("starting");
			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
//			@SuppressWarnings("unused")
//			Datacenter datacenter1 = createDatacenter("Datacenter_1");
//			@SuppressWarnings("unused")
//			Datacenter datacenter2 = createDatacenter("Datacenter_2");
//			@SuppressWarnings("unused")
//			Datacenter datacenter3 = createDatacenter("Datacenter_3");
//			@SuppressWarnings("unused")
//			Datacenter datacenter4 = createDatacenter("Datacenter_4");

			Initialize();
			Set_Grid();       
			Set_Efficiency();
			print();
//			Log.printLine("\n"+efficiency.get(0).get(9));
			while(!Completed()) {
				int pmin=0;
				int effmax=0;
				for(int i=0;i<num_vms;i++) {
					if(availableTime.get(i)<availableTime.get(pmin)) {
						pmin=i;
					}
				}
				for(int col=0;col<num_cloudlets;col++) {
					if(efficiency.get(pmin).get(col)>efficiency.get(pmin).get(effmax)) {
						effmax=col;
					}
				}
//				Log.printLine("number ofg.printL task= "+numScheduledTask+" " +efficiency.get(pmin).get(effmax));
				Log.printLine(pmin+" "+effmax + " "+efficiency.get(pmin).get(effmax));
				if(efficiency.get(pmin).get(effmax)<effThreshold) {
					availableTime.set(pmin,INT_MAX);
					continue;
				}
				for(int i=0;i<num_vms;i++) {
					efficiency.get(i).set(effmax,INT_MIN);
				}
				addTask(pmin,effmax);
				
			}
			CloudSim.startSimulation();
			List<List<Cloudlet>> newList=new ArrayList<List<Cloudlet>>();
			for(int i=0;i<num_vms;i++) {
				newList.add(brokerListArray.get(i).getCloudletReceivedList());
			}
			for(int i=0;i<num_vms;i++) {
				Log.print("=============> User "+brokerListArray.get(i).getId()+"    ");
				printCloudletList(newList.get(i));
			}
		}
		catch (Exception e){
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
}
