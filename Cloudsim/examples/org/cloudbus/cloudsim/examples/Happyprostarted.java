package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBroker1;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Happyprostarted
{
	private static myclass_datacenter datacenter1;
	private static DatacenterBroker1 broker;
	private static List<Vm> vmlist;

	public static void main(String args[])
	{
		try {
			int num_user = 1;
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;
			CloudSim.init(num_user, calendar, trace_flag);
			datacenter1 = createDatacenter("Datacenter_1");
			LinkedList<Vm> listVm = new LinkedList<Vm>();
			broker = datacenter1.getBroker();
			//int tempCurrentHost;
			vmlist = new ArrayList<Vm>();
			int i = 1;//,tempVmid =broker.VmId;
			broker.setCurrentHost(0);
			int tempHostId;
			broker.onlyForTesting = false;
			if(!broker.onlyForTesting)
			{
				for(;i<=10;i++)		//group number
				{
					for(int k=0;k<1;k++)	//vm number
					{
						int mips = 1000*(i);
						int brokerId = broker.getId();
						long size = 10000;
						int ram = 512;
						long bw = 1000;
						int pesNumber = 1;
						String vmm = "Xen";
						tempHostId = broker.getCurrentHost();
						Vm vm = new Vm(broker.VmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
					//Vm vm = new Vm(900, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
						vm.setHost(datacenter1.getHostList().get(((tempHostId++) % datacenter1.getHostList().size())));
						broker.Maxvm++;
						broker.setCurrentHost(tempHostId);
						vm.setGroupId(i);
						vm.setStartTime(100000);
						vmlist.add(vm);
						broker.numberVmsInGrp[i]++;
						broker.VmId++;
					}
				}
				broker.submitVmList(vmlist);
				vmlist.clear();
			}
			else
			{
				broker.vms = 100;				
				long size = 10000; //image size (MB)
				int ram = 512; //vm memory (MB)
				int mips1 = 1000;
				long bw = 1000;
				int pesNumber2 = 1; //number of cpus
				String vmm = "Xen"; //VMM name

				//create VMs
				Vm[] vm = new Vm[broker.vms];

				for(int counter=0;counter<broker.vms;counter++){
					tempHostId = broker.getCurrentHost();
					vm[counter] = new Vm(counter, broker.getId(), mips1, pesNumber2, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
					vm[counter].setHost(datacenter1.getHostList().get(((tempHostId++) % datacenter1.getHostList().size())));
					broker.Maxvm++;
					listVm.add(vm[counter]);
				}				
				broker.submitVmList(listVm);
				listVm.clear();
			}
		//	broker.VmId = tempVmid+1;
			CloudSim.startSimulation();
			CloudSim.stopSimulation();
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);
			Log.printLine("Happyprostarted finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	private static myclass_datacenter createDatacenter(String name) {

		List<Host> hostList = new ArrayList<Host>();

		List<Pe> peList = new ArrayList<Pe>();
		int mips = 10000;
		String vmm = "Xen";	
		
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList.add(new Pe(3, new PeProvisionerSimple(mips)));
		peList.add(new Pe(4, new PeProvisionerSimple(mips)));
		
		int hostId = 1;
		int hostRam = 16384; // host memory (MB)
		long storage = 1000000; // host storage
		int hostBw = 10000;
		Host newHost;
		//If more hostId needed change hostId< required and also change variable max in DatacenterBroker.java  
		while(hostId<=1000)
		{
			newHost = new Host(
				hostId,
				new RamProvisionerSimple(hostRam),
				new BwProvisionerSimple(hostBw),
				storage,
				peList,
				new VmSchedulerSpaceShared(peList)
			);
			hostList.add(newHost);
			hostId++;
		}
		String arch = "x86";
		String os = "Linux";
		double time_zone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.001; 
										
		double costPerBw = 0.0;
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		myclass_datacenter datacenter = null;
		try {
			datacenter = new myclass_datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
	}
	public static int wait =0,avewait;
	public static int negServiceTime = 1000, countRej = 0;
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;
		String indent = "    ";
		int count = 0;
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "DatacenterID" + indent + indent + "  Time" + indent
				+ "Start Time" + indent + "Finish Time" + indent + " Count " + indent + " deadline ");

		DecimalFormat dft = new DecimalFormat("###.##");
		int i;
		for (i = 0; i < size; i++)
		{			
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) 
			{
				Log.print("SUCCESS");}
			if (cloudlet.getCloudletStatus() == Cloudlet.INEXEC) 
			{
				Log.print("INEXEC");}
				Log.printLine(indent + indent + cloudlet.getResourceId()
						 + indent
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) 
						+ indent + indent + indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent + indent
						+ dft.format(cloudlet.getFinishTime()) + indent + indent + i + indent + indent + cloudlet.getDeadline()  + indent );//+ broker.getVmList().get(cloudlet.getVmId()).getHost().getId()/*+ indent + cloudlet.getMips() + indent + cloudlet.getVmId()*/);
				wait += cloudlet.getWaitingTime();
				if((cloudlet.getDeadline()+cloudlet.getSubmissionTime()) < cloudlet.getFinishTime())	count++;
			if((cloudlet.getFinishTime()-cloudlet.getSubmissionTime()) > negServiceTime) countRej++;		
		}
		System.out.println("Deadline missed " + count);		
		int tempCount;
		tempCount = (broker.cloudlets*10) - i;		
		if(!broker.onlyForTesting)
			System.out.println("rejection rate " + ((tempCount)/(broker.cloudlets*10)));
		else
			System.out.println("Rejection rate " + ((countRej)/broker.cloudlets*10));
		System.out.println("Max " + broker.Maxvm);
	}
}