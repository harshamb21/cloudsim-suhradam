package org.cloudbus.cloudsim.core;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

//import java.util.Map;
//import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
//import org.cloudbus.cloudsim.core.predicates.PredicateType;
//import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.Datacenter;

import org.cloudbus.cloudsim.*;

class myclass_datacenter extends Datacenter{

	private static List<Cloudlet> cloudletList;
	
//	private DatacenterCharacteristics characteristics;

	public static final int PERIODIC_EVENT = 67546;
	
	//private String regionalCisName;

//	private VmAllocationPolicy vmAllocationPolicy;

	//private double lastProcessTime;

	private DatacenterBroker datacenterBroker;
	
//	private Map<Integer, Double> debts;

//	private List<Storage> storageList;

//	private List<? extends Vm> vmList;

//	private double schedulingInterval;
	
	private float totalDelay = 0;
	public myclass_datacenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);

		setCharacteristics(characteristics);
		setVmAllocationPolicy(vmAllocationPolicy);
		setLastProcessTime(0.0);
		setDebts(new HashMap<Integer,Double>());
		setStorageList(storageList);
		setVmList(new ArrayList<Vm>());
		setSchedulingInterval(schedulingInterval);
		datacenterBroker = createbroker();
		for (Host host: getCharacteristics().getHostList()) {
			host.setDatacenter(this);
		}

        // If this resource doesn't have any PEs then no useful at all
        if (getCharacteristics().getPesNumber() == 0) {
            throw new Exception(super.getName() + " : Error - this entity has no PEs. Therefore, can't process any Cloudlets.");
        }

        // stores id of this class
        getCharacteristics().setId(super.getId());
	}

	public void processEvent(SimEvent ev) {
        int srcId = -1;
        //Log.printLine(CloudSim.clock()+"[PowerDatacenter]: event received:"+ev.getTag());

        switch (ev.getTag()) {
            // Resource characteristics inquiry
        	case PERIODIC_EVENT:
        		process_event(ev);
        		break;
        	case CloudSimTags.RESOURCE_CHARACTERISTICS:
                srcId = ((Integer) ev.getData()).intValue();
                sendNow(srcId, ev.getTag(), getCharacteristics());
                break;

                // Resource dynamic info inquiry
            case CloudSimTags.RESOURCE_DYNAMICS:
                srcId = ((Integer) ev.getData()).intValue();
                sendNow(srcId, ev.getTag(), 0);
                break;

            case CloudSimTags.RESOURCE_NUM_PE:
                srcId = ((Integer) ev.getData()).intValue();
                int numPE = getCharacteristics().getPesNumber();
                sendNow(srcId, ev.getTag(), numPE);
                break;

            case CloudSimTags.RESOURCE_NUM_FREE_PE:
                srcId = ((Integer) ev.getData()).intValue();
                int freePesNumber = getCharacteristics().getFreePesNumber();
                sendNow(srcId, ev.getTag(), freePesNumber);
                break;

                // New Cloudlet arrives
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev, false);
                break;

                // New Cloudlet arrives, but the sender asks for an ack
            case CloudSimTags.CLOUDLET_SUBMIT_ACK:
                processCloudletSubmit(ev, true);
                break;

                // Cancels a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudlet(ev, CloudSimTags.CLOUDLET_CANCEL);
                break;

                // Pauses a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE);
                break;

                // Pauses a previously submitted Cloudlet, but the sender
                // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE_ACK);
                break;

                // Resumes a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME);
                break;

                // Resumes a previously submitted Cloudlet, but the sender
                // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME_ACK);
                break;

                // Moves a previously submitted Cloudlet to a different resource
            case CloudSimTags.CLOUDLET_MOVE:
                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE);
                break;

                // Moves a previously submitted Cloudlet to a different resource
            case CloudSimTags.CLOUDLET_MOVE_ACK:
                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE_ACK);
                break;

                // Checks the status of a Cloudlet
            case CloudSimTags.CLOUDLET_STATUS:
                processCloudletStatus(ev);
                break;

                // Ping packet
            case CloudSimTags.INFOPKT_SUBMIT:
                processPingRequest(ev);
                break;

           	case CloudSimTags.VM_CREATE:
        		processVmCreate(ev, false);
        		break;

        	case CloudSimTags.VM_CREATE_ACK:
        		processVmCreate(ev, true);
        		break;

           	case CloudSimTags.VM_DESTROY:
           		processVmDestroy(ev, false);
           		break;

           	case CloudSimTags.VM_DESTROY_ACK:
        		processVmDestroy(ev, true);
        		break;

           	case CloudSimTags.VM_MIGRATE:
           		processVmMigrate(ev, false);
           		break;

           	case CloudSimTags.VM_MIGRATE_ACK:
           		processVmMigrate(ev, true);
           		break;

           	case CloudSimTags.VM_DATA_ADD:
           		processDataAdd(ev, false);
           		break;

           	case CloudSimTags.VM_DATA_ADD_ACK:
           		processDataAdd(ev, true);
           		break;

           	case CloudSimTags.VM_DATA_DEL:
           		processDataDelete(ev, false);
           		break;

           	case CloudSimTags.VM_DATA_DEL_ACK:
           		processDataDelete(ev, true);
           		break;

           	case CloudSimTags.VM_DATACENTER_EVENT:
           		updateCloudletProcessing();
            	checkCloudletCompletion();
           		break;

           		// other unknown tags are processed by this method
            default:
                processOtherEvent(ev);
                break;
        }
    }
	
	private static DatacenterBroker createbroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	public void process_event(SimEvent ev)
	{
		boolean generatePeriodicEvent;
		totalDelay =+1000;
		if(totalDelay <= 10000)	generatePeriodicEvent = true;
		else	generatePeriodicEvent = false;
	
		cloudletList = new ArrayList<Cloudlet>();
		int pesNumber = 1;
		long length = 400000;
		long fileSize = 3000;
		long outputSize = 3000;
		int brokerId = datacenterBroker.getId();
		UtilizationModel utilizationModel = new UtilizationModelFull();
		for(int id = 0;id < 10;id++)
		{
			Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet.setUserId(brokerId);

			cloudletList.add(cloudlet);
		}
		if(generatePeriodicEvent)
			send(getId(),1000,CloudSimTags.CLOUDLET_SUBMIT,cloudletList);
	}
	
	
}