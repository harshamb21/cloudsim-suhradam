//	AVERAGE FOR 100 TO 180
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * DatacentreBroker represents a broker
 * acting on behalf of a user. It hides VM management,
 * as vm creation, sumbission of cloudlets to this VMs
 * and destruction of VMs.
 */
public class DatacenterBroker1 extends SimEntity {

	public static final int VM_CREATED = 56777;
	public ArrayList<DatacenterCharacteristics> datacenterC;
	public static int cloudletID;
	// TODO: remove unnecessary variables
	//static private float totaldelay;
	public static int totalDelay = 50;
	public static final int PERIODIC_EVENT = 67567;
	public boolean onlyForTesting;
	public static int maxVMs = 4000;
	/** The vm list. */
	private List<? extends Vm> vmList;

	/** The vms created list. */
	private List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	private List<? extends Cloudlet> cloudletList;

	/** The cloudlet submitted list. */
	private List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	private List<? extends Cloudlet> cloudletReceivedList;

	/** The cloudlets submitted. */
	private int cloudletsSubmitted;

	/** The vms requested. */
	private int vmsRequested;

	/** The vms acks. */
	private int vmsAcks;
	public int VmId;
	/** The vms destroyed. */
	private int vmsDestroyed;

	/** The datacenter ids list. */
	private List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	private List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	private Map<Integer, Integer> vmsToDatacentersMap;

	//private int brokerID; 
	/** The datacenter characteristics list. */
	private Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;
	
	private int currentHost = 1;
	//private ArrayList<Integer> queueVMs;
	/**
	 * Created a new DatacenterBroker object.
	 *
	 * @param name 	name to be associated with this entity (as
	 * required by Sim_entity class from simjava package)
	 *
	 * @throws Exception the exception
	 *
	 * @pre name != null
	 * @post $none
	 */
	public DatacenterBroker1(String name) throws Exception {
		super(name);

		setVmList(new ArrayList<Vm>());
		setVmsCreatedList(new ArrayList<Vm>());
		setCloudletList(new ArrayList<Cloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());
		this.onlyForTesting = false;
		cloudletsSubmitted=0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);
		datacenterC = new ArrayList<DatacenterCharacteristics>(); 
		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
	}

	/**
	 * This method is used to send to the broker the list with
	 * virtual machines that must be created.
	 *
	 * @param list the list
	 *
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of
	 * cloudlets.
	 *
	 * @param list the list
	 *
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends Cloudlet> list){
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 *
	 * @param cloudletId ID of the cloudlet being bount to a vm
	 * @param vmId the vm id
	 *
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId){
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
	}

    /**
     * Processes events available for this Broker.
     *
     * @param ev    a SimEvent object
     *
     * @pre ev != null
     * @post $none
     */
	@Override
	public void processEvent(SimEvent ev) {
		//Log.printLine(CloudSim.clock()+"[Broker]: event received:"+ev.getTag());
		switch (ev.getTag()){			
		case PERIODIC_EVENT:
			this.processPeriodicEvent(ev);
			break;
			// Resource characteristics request
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
				processResourceCharacteristicsRequest(ev);
				break;
			// Resource characteristics answer
	        case CloudSimTags.RESOURCE_CHARACTERISTICS:
	        	processResourceCharacteristics(ev);
	            break;
	        // VM Creation answer
	        case VM_CREATED:
	           	processVmCreate(ev);
	           	break;
	        //A finished cloudlet returned
	        case CloudSimTags.CLOUDLET_RETURN:
	        	processCloudletReturn(ev);
	            break;
	        case CloudSimTags.CLOUDLET_CANCEL:
	        	processcancel(ev);
	        	break;
	        // if the simulation finishes
	        case CloudSimTags.END_OF_SIMULATION:
	        	shutdownEntity();
	            break;
	        case CloudSimTags.VM_CREATE_ACK:
	        	processcreate();
            // other unknown tags are processed by this method
	        default:
	          //  processOtherEvent(ev);
	            break;
		}
	}
void processcreate()
{}
	/**
	 * Process the return of a request for the characteristics of a PowerDatacenter.
	 *
	 * @param ev a SimEvent object
	 *
	 * @pre ev != $null
	 * @post $none
	 */
	void processcancel(SimEvent ev)
	{
		
		Cloudlet cl = (Cloudlet)ev.getData();
		if(cl !=null){}
	}
	private static boolean beginning = true;
	protected int processResourceCharacteristics(SimEvent ev) 
	{
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		datacenterC.add(characteristics);
		if(beginning)
		{
			getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);
			if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) 
			{
				setDatacenterRequestedIdsList(new ArrayList<Integer>());
				createVmsInDatacenter(getDatacenterIdsList().get(0));
			}
			beginning = false;
			return 0;
		}
		else
		{
			return characteristics.getMipsOfOnePe();			
		}
	}
	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 *
	 * @param ev a SimEvent object
	 *
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) 
	{
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
		Log.printLine(CloudSim.clock()+": "+getName()+ ": Cloud Resource List received with "+getDatacenterIdsList().size()+" resource(s)");
		for (Integer datacenterId : getDatacenterIdsList()) 
		{
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 *
	 * @param ev a SimEvent object
	 *
	 * @pre ev != null
	 * @post $none
	 */
	//private boolean begin = true; 
	protected void processVmCreate(SimEvent ev) 
	{
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];
		if (result == CloudSimTags.TRUE) 
		{
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock()+": "+getName()+ ": VM #"+vmId+" has been created in Datacenter #" + datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} 
		else 
		{
			Log.printLine(CloudSim.clock()+": "+getName()+ ": Creation of VM #"+vmId+" failed in Datacenter #" + datacenterId);
		}

		incrementVmsAcks();this.submitCloudletsWithDeadline();
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) 
		{ // all the requested VMs have been created
		//	this.submitCloudletsWithDeadline();
			/*
			if(begin)
				begin = false;
			else
			{ 
				boolean flag1 = false,flag2 = false;	int i =0;
				int cloudletpervm =  this.getCloudletSubmittedList().size() / this.getVmList().size();
				for(Vm vm : getVmList())
				{
					flag1 = false; 
					int diff = vm.getvmQueue() - cloudletpervm;
					if(diff>0)
					{
						List<Cloudlet> CList = new ArrayList<Cloudlet>();
						List<Vm> VList = new ArrayList<Vm>();
						VList.addAll(getVmList());
						CList.addAll(getCloudletSubmittedList());
						for(Cloudlet cloudlet : CList)
						{
							if(cloudlet.getVmId() == vm.getId())
							{
								flag2 = false;
								
								if(!flag1){flag1 = true;continue;}
								else 
								{	
									int a[]  = new int [3];
									a[0] = cloudlet.getCloudletId();
									a[1] = cloudlet.getUserId();
									a[2] = vm.getId();
									this.sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_CANCEL, a);
									for(Vm vm1 : VList)
									{
										if(vm1.getvmQueue() < cloudletpervm)
										{
											flag2 = true;
											cloudlet.setVmId(vm1.getId());
											this.sendNow(getVmsToDatacentersMap().get(vm1.getId()), CloudSimTags.CLOUDLET_SUBMIT,cloudlet);
											vm1.setvmQueue(vm1.getvmQueue() + 1);
											vm.decrementVmQueue();
											diff--;
											break;
										}
									}
									if(!flag2)
									{
										if(i == vm.getId()) 
											i = (i+1)% this.getVmList().size();
										cloudlet.setVmId(i);
										this.sendNow(getVmsToDatacentersMap().get(i), CloudSimTags.CLOUDLET_SUBMIT,cloudlet);
										for(Vm vm1 : VList)
										{
											if(vm1.getId() == i)
											{
												vm1.setvmQueue(vm1.getvmQueue() + 1);
											}
										}
										vm.decrementVmQueue();
										diff--;
										i = (i+1) %this.getVmList().size();
										if(diff == 0)	break;
									}
								}	
							}				
							if(diff == 0)	break;
						}
					}
					//System.out.println("Queue size of VM with id = ." + vm.getId() + "is " + vm.getvmQueue());
				}
				/*for(Vm vm: this.getVmList())
					System.out.println("Queue size of VM with id = ." + vm.getId() + "is " + vm.getvmQueue());
				this.submitCloudlets();
			}*/
		} 
	/*	else 
		{
			if (getVmsRequested() == getVmsAcks()) 
			{ // all the acks received, but some VMs were not created
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) 
				{
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) 
					{
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}
				//all datacenters already queried
				if (getVmsCreatedList().size() > 0) { //if some vm were created
				//	submitCloudlets();
				} else { //no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName() + ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}*/
	}

	/**
	 * Process a cloudlet return event.
	 *
	 * @param ev a SimEvent object
	 *
	 * @pre ev != $null
	 * @post $none
	 */
	private static int vmMax = 0,vmMin = 0;
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();

		cloudlet.getVmId();
		for(Vm vm : this.getVmsCreatedList())
		{
			if(vm.getId() == cloudlet.getVmId() && cloudlet.getStatus()== Cloudlet.SUCCESS)
			{
				if(!this.getCloudletReceivedList().contains(cloudlet))
				{		
				vm.decrementVmQueue();
				vm.setStatus(false);
				this.getCloudletSubmittedList().remove(cloudlet);
				getCloudletReceivedList().add(cloudlet);
				vm.VmNumber--;
				}
			}
		}
			Log.printLine(cloudlet.getFinishTime()+": "+getName()+ ": Cloudlet "+cloudlet.getCloudletId()+" received with status "+ cloudlet.getCloudletStatusString()+ "from "+ cloudlet.getVmId());
		cloudletsSubmitted--;
		
		if (getCloudletList().size()==0&&cloudletsSubmitted==0) 
		{ //all cloudlets executed
			Log.printLine(CloudSim.clock()+": "+getName()+ ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} 
		else 
		{ //some cloudlets haven't finished yet
			if (getCloudletList().size()>0 && cloudletsSubmitted==0) 
			{
				//all the cloudlets sent finished. It means that some bount
				//cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}
		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker.
	 * This method is called by {@link #body()} for incoming unknown tags.
	 *
	 * @param ev   a SimEvent object
	 *
	 * @pre ev != null
	 * @post $none
	 */	static public int setHost;
		public int cloudlets;
		public int[] numberVmsInGrp = new int [11];
		
		/********************************************************************
		 * 
		 * @return
		 */
		public int getCurrentHost()
		{
			return currentHost;
		}
		/********************************************************************
		 * 
		 * @param tempCurrentHost
		 * @return
		 */
		public boolean setCurrentHost(int tempCurrentHost)
		{
			this.currentHost = tempCurrentHost;
			return true;
		}
		public int vms;
		public int currentMips;
		/*********************************************************************
		 * 
		 * @param ev
		 */
		 private void processPeriodicEvent(SimEvent ev) 
		 {		 		
			 int[] groupId;
			 groupId = new int[100];
			 for(int i=0;i<100;i++)	groupId[i] = 0;  // number of cloudlets in this group
			int deadlineC,type;
			boolean setOrNot = false;
			totalDelay += 50;       // for stopping condition
			Random r12 = new Random();
			Random r11 = new Random();
	// working for cloudlet
			if(!onlyForTesting)
				cloudlets = 100+r12.nextInt(80);
			else
				cloudlets = 170;
			Random r = new Random();
			Random r2 = new Random();
			type = (r2.nextInt(2));
			if(type == 1)						// type = 0 --> no deadline     ---> setOrNot false
				setOrNot = true;				// type = 1 --> with deadline   ---> setOrNot true
			//For testing
			//**********************************			
			//setOrNot = true;
			//**********************************
			deadlineC = (r.nextInt(32));
			if(deadlineC < 0)
					deadlineC*=-1;
			deadlineC+=20;
			LinkedList<Cloudlet> listCloudlet = new LinkedList<Cloudlet>();
			LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
			LinkedList<Cloudlet> list1 = new LinkedList<Cloudlet>();
			int mips;
			long length;
			long fileSize = 300;
			//int vmid;
			long outputSize = 300;
			int pesNumber = 1;			
			List<Vm> vmlist;
			vmlist = new ArrayList<Vm>();
			UtilizationModel utilizationModel = new UtilizationModelFull();
			Cloudlet[] cloudlet = new Cloudlet[cloudlets];
			
			for(int i = 0;i < cloudlets;cloudletID++, i++)
			{
				if(!onlyForTesting)
					length = (10+r11.nextInt(5))*10000;//140000;//+r11.nextInt(40000);
				else
					length = 140000;
				cloudlet[i] = new Cloudlet(cloudletID, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
				cloudlet[i].setUserId(this.getId());
				if(!this.onlyForTesting)
				{
					if(setOrNot == true)				
					{
						mips = (int)(length/deadlineC);
						mips /= 100;
						mips = (mips+1)*100;
			
			//	else
			//		mips = 1000;				
/*				long size = 10000; // image size (MB)
				int ram = 512; // vm memory (MB)
				long bw = 1000;
				int pesNumber1 = 0; // number of cpus
				if(mips>10000)
				{					
					if(mips%10000 > 0) pesNumber1 = 1;
					pesNumber1 = pesNumber1+(mips/10000) + 1;
				}
				else
				{				
					pesNumber1 = 1;
				}
				String vmm = "Xen"; // VMM name
			// create VM
				int tempHostId = this.getCurrentHost();
				Vm vm = new Vm(this.VmId, this.getId(), mips, pesNumber1, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
				this.VmId++;
				vm.setHost(datacenterC.get(0).getHostList().get((tempHostId++ % datacenterC.get(0).getHostList().size())));
				this.setCurrentHost(tempHostId);
		//Vm vm = new Vm(vmid, this.getId(), mips, pesNumber1, ram, bw, size, vmm, new CloudletSchedulerTimeShared());				
				vmlist.add(vm);
				this.submitVmList(vmlist);
				createVmsInDatacenter(2);*/
					//			this.getVmsCreatedList().clear();
				//length = 100000;
					
						setOrNot = cloudlet[i].setDeadline(deadlineC);
						int grp = cloudlet[i].setVmGrpId(mips);
				//int tempVmId = ((grp)*100)+groupId[grp];
				
						groupId[grp]++;
						cloudlet[i].setMips(mips);
						System.out.println("Mips " + mips);
				//cloudlet[i].setVmId(vmIndex);
						cloudlet[i].setVmId(-1);
						this.currentMips = cloudlet[i].getMips();
						System.out.println("vmid = " + cloudlet[i].getVmId() + " for " + cloudlet[i].getCloudletId());
						list.add(cloudlet[i]);vmIndex++;
						System.out.println("cloudlet[i] " + cloudlet[i].getVmGrpId() + "vmid " +cloudlet[i].getVmId() + " with deadline " + cloudlet[i].getDeadline());
					}
					else
					{					
						cloudlet[i].setVmId(-1);
						cloudlet[i].setDeadline(1000000);
						this.currentMips = cloudlet[i].getMips();
						list1.add(cloudlet[i]);
					}
				}
				else
				{					
					cloudlet[i].setDeadline(1000000);
					//******************************************
					//for testing
					/*cloudlet[i].setDeadline(deadlineC);
					mips = (int)(length/deadlineC);
					mips /= 100;
					mips = (mips+1)*100;
					cloudlet[i].setMips(mips);*/
					//*****************************************
					cloudlet[i].setVmId(-1);
					listCloudlet.add(cloudlet[i]);					
				}
			}
			vmlist.clear();
			//deleteVmsIfUnused();
			if(!this.onlyForTesting)
			{	if(list.size()>0)
				{
					this.setCloudletList(list);
					this.submitCloudletsWithDeadline();
				}
			
				if(list1.size()>0)
				{
					this.setCloudletList(list1);
					this.submitCloudlets();
				}
			}
			else
			{				
				this.setCloudletList(listCloudlet);
				this.submitCloudletsTesting();
			}
			 boolean generatePeriodicEvent;
		    if(totalDelay <= 500)
		    	generatePeriodicEvent=true;
		    else
		    	generatePeriodicEvent=false;
			if (generatePeriodicEvent)
				send(getId(),50,PERIODIC_EVENT,ev);        // for scheduling for next event	    
		 }
		 int negServiceTime = 1000;
		 int vmI = 0;
		 public void submitCloudletsTesting()
		 {			 
			 
             for (Cloudlet cloudlet : getCloudletList()) {
                     Vm vm;
                     if (cloudlet.getVmId() == -1) { //if user didn't bind this cloudlet and it has not been executed yet
                             vm = getVmsCreatedList().get(vmI);
                     } else { //submit to the specific vm
                             vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                             if (vm == null) { // vm was not created
                                     Log.printLine(CloudSim.clock()+": "+getName()+ ": Postponing execution of cloudlet "+cloudlet.getCloudletId()+": bount VM not available");
                                     continue;
                             }
                     }

                     Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm.getId());
                     cloudlet.setVmId(vm.getId());
                     sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                     cloudletsSubmitted++;
                     vmI = (vmI + 1) % getVmsCreatedList().size();
                     getCloudletSubmittedList().add(cloudlet);
             }

             // remove submitted cloudlets from waiting list
             for (Cloudlet cloudlet : getCloudletSubmittedList()) {
                     getCloudletList().remove(cloudlet);
             }
		 }
/*		 protected void submitCloudletsWithDeadline()
		 {
			 int vmIndex = 0;
				for (Cloudlet cloudlet : getCloudletList()) {
					Vm vm;
					if (cloudlet.getVmId() == -1) { //if user didn't bind this cloudlet and it has not been executed yet
						vm = getVmsCreatedList().get(vmIndex);
					} else { //submit to the specific vm
						vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
						if (vm == null) { // vm was not created
							Log.printLine(CloudSim.clock()+": "+getName()+ ": Postponing execution of cloudlet "+cloudlet.getCloudletId()+": bount VM not available");
							continue;
						}
					}

					Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm.getId());
					cloudlet.setVmId(vm.getId());
					sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
					cloudletsSubmitted++;
					vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
					getCloudletSubmittedList().add(cloudlet);
				}

				// remove submitted cloudlets from waiting list
				for (Cloudlet cloudlet : getCloudletSubmittedList()) {
					getCloudletList().remove(cloudlet);
				}

		 }*/
		 public int[] tempCounterGrp = new int [11];
		 public int VmNumber=0;
		 public int limit =30;
	 protected void submitCloudletsWithDeadline()
	 {
		 boolean highFreq = false;
		int tempGrpId;
		boolean submitted = false,flag11 = false;
		 int vmIndexLocal = 0;
		 int counter = 0;						// keeps track of number of vms in VmsCreatedList
		 int counter1 = 0;						// number of vms in required group
		 //***************************************************************************************************88
		 /*int freq = (10000 /this.currentMips);		 
		 if(freq <cloudlets)	highFreq = true;
		 int numVMsRequired = ((cloudlets)*(this.currentMips))/10000;
		 int cloudletSubmit = 10000/this.currentMips;
		// if(highFreq)
		 //{
			/* for(int i = 0;i<numVMsRequired;i++)
			 {
     		 	List<Vm> vmlist = new ArrayList<Vm>();
     		 	//boolean flag1 = false;
     		 	int mips = 10000;
     		 	int brokerId = this.getId();
     		 	long size = 10000;
     		 	int ram = 512;
     		 	long bw = 1000;
     		 	int pesNumber = 1;
     		 	String vmm = "Xen";
     		 	//int j=100;
     		 	Vm vm11 = new Vm(this.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
     		 	this.VmId++;                		 	
     		 	int tempHostId = this.currentHost;
     		 	vm11.setHost(datacenterC.get(0).getHostList().get(((tempHostId++) % datacenterC.get(0).getHostList().size())));
     		 	this.setCurrentHost(tempHostId);
     		 	vm11.setHost(datacenterC.get(0).getHostList().get(tempHostId));
     		 	vm11.setGroupId(10);
     		 	vm11.setStatus(false);
     		 	vmlist.add(vm11);
     		 	this.numberVmsInGrp[10]++;
     		 	this.submitVmList(vmlist);
     		 	sendNow(2, CloudSimTags.VM_CREATE_ACK, vm11);
			 }
			 for(Cloudlet cloudlet : getCloudletList())
			 {
				 if(cloudletSubmit > 0)
					 
				 
				 Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm.getId());         	 
            	 cloudlet.setVmId(vm.getId());
            	 sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            	 vm.setStatus(true);
            	 vm.VmNumber++;
				 cloudletsSubmitted++;
				 vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
				 getCloudletSubmittedList().add(cloudlet);
				 submitted = true;
			 }
		 }*/
		 //***********************************************************************************************************8
         for (Cloudlet cloudlet : getCloudletList()) 
         {flag11 = false;
         counter1 = 0;
        	 counter = 0;
                 Vm vm;
                 tempGrpId = cloudlet.getVmGrpId();
                 vm = getVmsCreatedList().get(0);
                 Vm vm1;int size1 =this.getVmsCreatedList().size();
                 if (cloudlet.getVmId() == -1)
                 { 															//if user didn't bind this cloudlet and it has not been executed yet
                	 for(Vm tempVm : this.getVmsCreatedList())
                	 {                		 
                		 if(tempVm.getGroupId() == tempGrpId)
                		 {                   			 
                			 counter1++;
                			 if(!tempVm.getStatus())
                			 {       
                				 vm = tempVm;
                				 break;
                			 }
                		 }
                		 counter++;
                		 if((counter == size1) && (counter1 < this.limit))
                		 {	
                 		 	Log.printLine(" REACHED HERE");
                			flag11 = true;
                		 	List<Vm> vmlist = new ArrayList<Vm>();
                		 	//boolean flag1 = false;
                		 	int mips = 1000*(tempGrpId);
                		 	int brokerId = this.getId();
                		 	long size = 10000;
                		 	int ram = 512;
                		 	long bw = 1000;
                		 	int pesNumber = 1;
                		 	String vmm = "Xen";
                		 	//int j=100;
                		 	vm1 = new Vm(this.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
                		 	this.VmId++;                		 	
                		 	int tempHostId = this.currentHost;
                		 	vm1.setHost(datacenterC.get(0).getHostList().get(((tempHostId++) % datacenterC.get(0).getHostList().size())));
                		 	this.setCurrentHost(tempHostId);
                		 	vm1.setHost(datacenterC.get(0).getHostList().get(tempHostId));
                		 	vm1.setGroupId(tempGrpId);
                		 	vm1.setStatus(false);
                		 	vmlist.add(vm1);
                		 	this.numberVmsInGrp[tempGrpId]++;
                		 	this.submitVmList(vmlist);
                		 	sendNow(2, CloudSimTags.VM_CREATE_ACK, vm1); 
                		 	
                		 	//CloudSim.pause(this.getId(),1);
                		 	Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm1.getId());
                		 	cloudlet.setVmId(vm1.getId());
                		 	sendNow(2, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                		 	vm1.setStatus(true);
                		 	vm1.VmNumber++;
                		 	cloudletsSubmitted++;
                		 	vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                		 	getCloudletSubmittedList().add(cloudlet);
                		 	submitted = true;
                		 }
                	 } 		 //vm = getVmsCreatedList().get(vmIndexLocal);
                 }/* else { //submit to the specific vm                	 
                         vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());                         
                         if (vm == null) { // vm was not created
                                 Log.printLine(CloudSim.clock()+": "+getName()+ ": Postponing execution of cloudlet "+cloudlet.getCloudletId()+": bount VM not available");
                                 continue;
                         }
                 }*/
                 if(!flag11)
                 {
                 if((!vm.getStatus()) && (cloudlet.getVmGrpId()== vm.getGroupId()))// && counter != size1)	//								// if VM not busy (free) submit
                 {   
                	 Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm.getId());         	 
                	 cloudlet.setVmId(vm.getId());
                	 sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                	 vm.setStatus(true);
                	 vm.VmNumber++;
    				 cloudletsSubmitted++;
    				 vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
    				 getCloudletSubmittedList().add(cloudlet);
    				 submitted = true;
                 }
                 else
                 {														// VM busy (not free)
                	 /*for(Vm vm2 : getVmList())							// run loop until free VM with same grp id is found
                	 {
                		 if(vm2.getGroupId() == tempGrpId)
                		 {
                			 if(!vm2.getStatus())						// if VM not busy (free) submit
                			 {
                				 cloudlet.setVmId(vm2.getId());
                				 sendNow(getVmsToDatacentersMap().get(vm2.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                				 cloudletsSubmitted++;
                				 vm2.VmNumber++;
                				 vm2.setStatus(true);
                				 vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                				 getCloudletSubmittedList().add(cloudlet);
                				 submitted = true;
                			 }
                			 else
                			 {
                				 continue;
                			 }
                		 }
                	 }*/
                	 if(!submitted)										// if not submitted
                	 {  
                		 System.out.println("temp GrpId " + tempGrpId);
                		 if(tempGrpId >= 0)
                		 {
                			 if(this.numberVmsInGrp[tempGrpId] >= this.limit)		// number of vms in a grp > 30
                			 {
                				 if(tempGrpId<50)
                				 {
                					for(Vm tempVm1 : this.getVmList())
                					{
                						if((tempVm1.getGroupId() == (tempGrpId*2))&&(tempVm1.VmNumber<2))
                						{ 						
               								cloudlet.setVmId(tempVm1.getId());
               								tempVm1.setStatus(true);
               								tempVm1.VmNumber++;
               								tempVm1.VmNumber = tempVm1.VmNumber % 3;
               								cloudlet.setVmId(tempVm1.getId());
               								sendNow(getVmsToDatacentersMap().get(tempVm1.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
               								cloudletsSubmitted++;
                           				 	vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                           				 	getCloudletSubmittedList().add(cloudlet);
                           				 	submitted = true;
               								break;
                						}
                					}
                				 }
                				 if(!submitted)
                				 {
                					 for(int i = cloudlet.getVmGrpId();i<=10;i++)
                					 {
                						 if(this.numberVmsInGrp[i]<this.limit)
                						 {
                							 	List<Vm> vmlist = new ArrayList<Vm>();
                	                		 	//boolean flag1 = false;
                	                		 	int mips = 1000*(i);
                	                		 	int brokerId = this.getId();
                	                		 	long size = 10000;
                	                		 	int ram = 512;
                	                		 	long bw = 1000;
                	                		 	int pesNumber = 1;
                	                		 	String vmm = "Xen";
                	                		 	//int j=100;
                	                		 	vm1 = new Vm(this.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
                	                		 	this.VmId++;
                	                		 	this.Maxvm++;
                	                		 	int tempHostId = this.currentHost;
                	                		 	vm.setHost(datacenterC.get(0).getHostList().get(((tempHostId++) % datacenterC.get(0).getHostList().size())));
                	                		 	this.setCurrentHost(tempHostId);
                	                		 	vm1.setHost(datacenterC.get(0).getHostList().get(tempHostId));
                	                		 	vm1.setGroupId(i);
                	                		 	vm1.setStatus(false);
                	                		 	vmlist.add(vm1);
                	                		 	this.numberVmsInGrp[i]++;
                	                		 	this.submitVmList(vmlist);
                	                		 	sendNow(2, CloudSimTags.VM_CREATE_ACK, vm1); 
                	                		 	
                	                		 	//CloudSim.pause(this.getId(),1);
                	                		 	Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm1.getId());
                	                		 	cloudlet.setVmId(vm1.getId());
                	                		 	sendNow(2, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                	                		 	vm1.setStatus(true);
                	                		 	vm1.VmNumber++;
                	                		 	cloudletsSubmitted++;
                	                		 	vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                	                		 	getCloudletSubmittedList().add(cloudlet);
                	                		 	submitted = true;
                						 }
                					 }
                				 }
                				 if(!submitted)
                				 {
                					 this.limit = this.limit+(this.limit/2);
                					 List<Vm> vmlist = new ArrayList<Vm>();
                         		 	//boolean flag1 = false;
                         		 	int mips = 1000*(tempGrpId);
                         		 	int brokerId = this.getId();
                         		 	long size = 10000;
                         		 	int ram = 512;
                         		 	long bw = 1000;
                         		 	int pesNumber = 1;
                         		 	String vmm = "Xen";
                         		 	//int j=100;
                         		 	vm1 = new Vm(this.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
                         		 	this.VmId++;
                         		 	
                         		 	int tempHostId = this.currentHost;
                         		 	vm.setHost(datacenterC.get(0).getHostList().get(((tempHostId++) % datacenterC.get(0).getHostList().size())));
                         		 	this.setCurrentHost(tempHostId);
                         		 	vm1.setHost(datacenterC.get(0).getHostList().get(tempHostId));
                         		 	vm1.setGroupId(tempGrpId);
                         		 	vm1.setStatus(false);
                         		 	vmlist.add(vm1);
                         		 	this.numberVmsInGrp[tempGrpId]++;
                         		 	this.submitVmList(vmlist);
                         		 	sendNow(2, CloudSimTags.VM_CREATE_ACK, vm1); 
                         		 	
                         		 	//CloudSim.pause(this.getId(),1);
                         		 	Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+vm1.getId());
                         		 	cloudlet.setVmId(vm1.getId());
                         		 	sendNow(2, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                         		 	vm1.setStatus(true);
                         		 	vm1.VmNumber++;
                         		 	cloudletsSubmitted++;
                         		 	vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                         		 	getCloudletSubmittedList().add(cloudlet);
                         		 	submitted = true;
                				 }
                			 }                			 
                		 /*else											// number of vms in a grp <=30
                		 {
                			 List<Vm> vmlist = new ArrayList<Vm>();
                			 boolean flag1 = false;
                			 int mips = 1000*(tempGrpId);
                			 int brokerId = this.getId();
                			 long size = 10000;
         					 int ram = 512;
         					 long bw = 1000;
         					 int pesNumber = 1;
         					 String vmm = "Xen";
         					 int j=100;
         					 //Vm vm1 = new Vm(this.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
         					 this.VmId++;
         					 int tempHostId = this.currentHost;
         					 vm.setHost(datacenterC.get(0).getHostList().get(((tempHostId++) % datacenterC.get(0).getHostList().size())));
         					 this.setCurrentHost(tempHostId);
         					 vm1.setHost(datacenterC.get(0).getHostList().get(tempHostId));
         					 vm1.setGroupId(tempGrpId);         						 
         					 vmlist.add(vm1);
         					 this.numberVmsInGrp[tempGrpId]++;
         					this.submitVmList(vmlist);
                   		 this.createVmsInDatacenter(2);
                		 }*/
                	 } }}   }    	 
         		//}
                 //sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                 //cloudletsSubmitted++;
                 //vmIndexLocal = (vmIndexLocal + 1) % getVmsCreatedList().size();
                 //getCloudletSubmittedList().add(cloudlet);
         }

         // remove submitted cloudlets from waiting list
         for (Cloudlet cloudlet1 : getCloudletSubmittedList()) {
                 getCloudletList().remove(cloudlet1);
         }
	}
	
    /**
     * Create the virtual machines in a datacenter.
     *
     * @param datacenterId Id of the chosen PowerDatacenter
     *
     * @pre $none
     * @post $none
     */
		 public int getmax()
		 {
			 return vmMax;
		 }
		 public int getmin()
		 {
			 return vmMin;
		 }
    protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}
		getDatacenterRequestedIdsList().add(datacenterId);
		
		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}  
    /**
     * Submit cloudlets to the created VMs.
     * @pre $none
     * @post $none
     */
    private int m, oldm, min = 1, max = maxVMs;
    private static int vmIndex=0;
    public static int NumberofVMs;
    //private static int countervm2 = 0;
    private static int tempCounter = 0;
    private static int tempMax = 1;
    public static int Maxvm = 0;
	 protected void submitCloudlets() 
	 {
		// boolean flag2 = false;
		 double k = 0;
		//int waitn = 0;
		int temp;
		int numberVMscounter;
		int vmid;
		Vm vm =null;
		vmIndex = vmIndex % getVmList().size();										//unique index for each VM
		System.out.println("GoT HERE");
		int negServiceTime = 3000;													//Ts
		List<Vm> vmWaitDelete = new ArrayList<Vm>();
		int id =0;
		//************************Creating VMs*******************************
		Cloudlet ncloudlet = this.getCloudletList().get(0);
		Vm nvm = this.getVmList().get(0);
		int nk = (int) (negServiceTime / (ncloudlet.getCloudletLength()/nvm.getMips()));
		double nm = cloudlets/(int) (nk/3);
		List<Vm> nlist = new ArrayList<Vm>();
		int j=0;
		int nmipsvm = 1000;
		int nbrokerId = this.getId();
		long nsize = 10000;
		int nram = 512;
		long nbw = 1000;
		int npesNumber = 1;
		String nvmm = "Xen";
		
		for(int i = 1; i <= nm; i++)
		{
			Vm vm2 = new Vm(this.VmId++, nbrokerId, nmipsvm, npesNumber, nram, nbw, nsize, nvmm, new CloudletSchedulerSpaceShared());
			vm2.setHost(datacenterC.get(0).getHostList().get((j++ % datacenterC.get(0).getHostList().size())));
			nlist.add(vm2);
			System.out.println("first:New VM " + i + " created");
			tempMax++;
			Maxvm++;
			if(tempMax>vmMax)
				vmMax = tempMax;
		}
		this.submitVmList(nlist);		
		nlist.clear();
		createVmsInDatacenter(2);
		//****************************************************************
		//int j =0;
		for (Cloudlet cloudlet : getCloudletList())							// for each cloudlet 
		{
			id = cloudlet.getCloudletId();
			//waitn += cloudlet.getWaitingTime();
//******************************Submit cloudlets to VM*******************************************		
			 //submit to specific vm			
			for(numberVMscounter = 0;numberVMscounter < getVmsCreatedList().size();numberVMscounter++)
			{// Allocating particular vm to a cloudlet
				
				vm = this.getVmList().get(vmIndex);
				if(vm.getDeleteStatus() == false)
				{		
					tempCounter = (int) (vmMax / 2.8);
					k = negServiceTime / (cloudlet.getCloudletLength()/vm.getMips());					//re
					vmid = vm.getId();
					temp = vm.getvmQueue();
					if(temp < k)
					{
						cloudlet.setVmId(vmid);	
						getCloudletSubmittedList().add(cloudlet);
						this.cloudletsSubmitted++;
						vm.VmNumber++;
						sendNow(2, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
						vm.setvmQueue(temp+1);
				//		if (vm == null)
				//		{ 
							// vm was not created
				//			Log.printLine(CloudSim.clock()+": "+getName()+ ": Postponing execution of cloudlet "+cloudlet.getCloudletId()+": bount VM not available");
				//		}
						break;
					}
					else
					{
						vmIndex = (vmIndex + 1) % getVmList().size();
					}
				}
			}
//**********************************Cloudlet submitted******************************************
			vmIndex = (vmIndex + 1) % getVmList().size();
			if(numberVMscounter >= getVmList().size())
			{
				int tempSize = this.getVmList().size();
				if(this.getVmList().get(tempSize - 1).getvmQueue() == k)
				{
					m = getVmList().size();
					if(m == max)
					{							//number of VMs crossed max limit 
						System.out.println("This cloudlet" + cloudlet.getCloudletId() +"can not be serviced right now please try again later");
						break;
					}
					oldm = getVmList().size();
					int arrRate = cloudlets;				// 100 - 180 cloudlets per second	//lamda

					double rho = arrRate * cloudlet.getCloudletLength() / vm.getMips();
					k = negServiceTime / (cloudlet.getCloudletLength()/vm.getMips());			//re
							//Ts					Tr
				//*************************RejRate Calculation********************************
					double rejRate = (1 - rho)*Math.pow(rho, k);
					rejRate = rejRate / (1 - (Math.pow(rho,k+1)));
				//**************************RejRateCalculated*********************************					 
					if(rejRate > 0)
					{
						min = m + 1;
						m = m + m/2;						 
						if(m > max)
							m = max;						 
					}
					if(m == oldm)	break;
					if(m > oldm)
					{
						if(vmWaitDelete.size() == 0)
						{
							List<Vm> list = new ArrayList<Vm>();
							int mipsvm = 1000;
							int brokerId = this.getId();
							long size = 10000;
							int ram = 512;
							long bw = 1000;
							int pesNumber = 1;
							String vmm = "Xen";
							int tempoSize = this.getVmList().size();
							for(int i = tempoSize; i < (tempoSize+m-oldm); i++)
							{
								Vm vm2 = new Vm(i, brokerId, mipsvm, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
								vm2.setHost(datacenterC.get(0).getHostList().get((j++ % datacenterC.get(0).getHostList().size())));
								list.add(vm2);
								tempMax++;
								Maxvm++;
								if(tempMax>vmMax)
								vmMax = tempMax;
								tempCounter = (int) (vmMax * 0.3579);
								System.out.println("tempCOunter " + tempCounter + " VMMax " + vmMax);
								System.out.println("next:New VM " + i + " created");
							}
							this.submitVmList(list);
							list.clear();
							int datacenterId = this.getDatacenterIdsList().get(0);
							createVmsInDatacenter(datacenterId);
							break;
						}
						else
						{
							int temporaryM = oldm;
							for(Vm newVm: vmWaitDelete)
							{
								newVm.setDeleteStatus(false);
								temporaryM++;
								if(m==temporaryM)	break;
							}
						}
					}
				}
			}
		}
		List<Cloudlet> CList = new ArrayList<Cloudlet>();
		CList.addAll(getCloudletList());
		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet1 : CList)
		{	if(cloudlet1.getCloudletId() == id )
			break;
			getCloudletList().remove(cloudlet1);			
		}
		// *********************************** Destroy calculations ******************************************
		
		m = this.getVmList().size();
		oldm = m;
		Vm vm1 = this.getVmList().get(0);
		vmMin = tempCounter;
		if(vm1.getvmQueue() < k/3)
		{
			max = m;
			m = min + (max - min)/2;
			if(m <= min)
			{
				m = oldm;
			}
		}			
		/*for(Vm vm4 : this.getVmList())
			System.out.println("VM id: " + vm4.getId() + " size: " + vm4.getvmQueue());*/
		//int difference = 0;
		//********************** Destroying ***************************
		//if(m < oldm)
		//{
			
		//}
		/*
			List<Vm> VList = new ArrayList<Vm>();
			difference = oldm - m;
			for(int y =0 ;y<(this.getVmList().size() - difference);y++)
			{
				VList.add(getVmList().get(y));
			}
			int cloudletpervm = ( this.getCloudletSubmittedList().size() / (this.getVmList().size() - difference));
			int d =0;
			int size = this.getVmList().size();
			for(int i =1 ;i <= difference; i++)
			{
				vm1 = (getVmList().get(size - i));
				if(vm1.getvmQueue() != 0)
				{
					List<Cloudlet> CList1 = new ArrayList<Cloudlet>();				
					CList1.addAll(getCloudletSubmittedList());
					for(Cloudlet cloudlet1 : CList1)
					{
						if(cloudlet1.getVmId() == vm1.getId())
						{
							flag2 = false;
							int a[]  = new int [3];
							a[0] = cloudlet1.getCloudletId();
							a[1] = cloudlet1.getUserId();
							a[2] = vm1.getId();
							this.sendNow(2, CloudSimTags.CLOUDLET_CANCEL, a);
							int x= countervm2;
							for(Vm vm2 : VList)
							{
								if(x > 0)	
								{
									x = x-1;
									continue;
								}
								if(vm2.getvmQueue() < cloudletpervm)
								{
									countervm2 = (countervm2 + 1) % VList.size();
									flag2 = true;
									
									cloudlet1.setVmId(vm2.getId());
									this.sendNow(2, CloudSimTags.CLOUDLET_SUBMIT,cloudlet1);
									vm2.setvmQueue(vm2.getvmQueue() + 1);
									vm1.decrementVmQueue();
									break;
								}
								else	flag2 = false;
							}
							if(!flag2)
							{
								if(d == vm1.getId())
									d = (d+1)% VList.size();
								cloudlet1.setVmId(d);
								this.sendNow(2, CloudSimTags.CLOUDLET_SUBMIT,cloudlet1);
								for(Vm vm2 : VList)
								{
									if(vm2.getId() == d)
									vm2.setvmQueue(vm2.getvmQueue() + 1);
								}
								d = (d+1) % VList.size();
							}								
						}
					}					
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm1.getId());
					sendNow(2, CloudSimTags.VM_DESTROY_ACK, vm1);
					tempMax--;
					System.out.println("tempMax " + tempMax);
					if(tempMax < vmMin)
						vmMin = tempMax;
					this.getVmList().remove(vm1);
					getVmsToDatacentersMap().remove(vm1.getId());
					this.getVmsCreatedList().remove(vm1);
				}
				else
					vm1.setRecentlyCreated(false);
			}
		}*/	
	 }		
	/**
	 * Destroy the virtual machines running in datacenters.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		/*for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();*/
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 *
	 * @pre $none
	 * @post $none
	 */
	private void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/* (non-Javadoc)
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
        Log.printLine(getName() + " is shutting down...");
	}

	/* (non-Javadoc)
	 * @see cloudsim.core.SimEntity#startEntity()
	 */       //          Schedule call kar 
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		for(int i=0;i<=10;i++)
			this.numberVmsInGrp[i] = 0;
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
		schedule(getId(), 50, PERIODIC_EVENT);
	}

	/**
	 * Gets the vm list.
	 *
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 *
	 * @param <T> the generic type
	 * @param vmList the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}


	/**
	 * Gets the cloudlet list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}


	/**
	 * Sets the cloudlet list.
	 *
	 * @param <T> the generic type
	 * @param cloudletList the new cloudlet list
	 */
	protected <T extends Cloudlet> void setCloudletList(List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
		return (List<T>) cloudletSubmittedList;
	}


	/**
	 * Sets the cloudlet submitted list.
	 *
	 * @param <T> the generic type
	 * @param cloudletSubmittedList the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 *
	 * @param <T> the generic type
	 * @param cloudletReceivedList the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 *
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 *
	 * @param <T> the generic type
	 * @param vmsCreatedList the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 *
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 *
	 * @param vmsRequested the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 *
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 *
	 * @param vmsAcks the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		this.vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 *
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 *
	 * @param vmsDestroyed the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 *
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 *
	 * @param datacenterIdsList the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}
	
	/**
	 * Gets the vms to datacenters map.
	 *
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 *
	 * @param vmsToDatacentersMap the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}
	/**
	 * Gets the datacenter characteristics list.
	 *
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}
	/**
	 * Sets the datacenter characteristics list.
	 *
	 * @param datacenterCharacteristicsList the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}
	/**
	 * Gets the datacenter requested ids list.
	 *
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}
	/**
	 * Sets the datacenter requested ids list.
	 *
	 * @param datacenterRequestedIdsList the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}
}