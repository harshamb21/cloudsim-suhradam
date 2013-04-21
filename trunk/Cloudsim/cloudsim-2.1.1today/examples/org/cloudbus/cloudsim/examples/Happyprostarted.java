package org.cloudbus.cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
//import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
//import org.cloudbus.cloudsim.Datacenter;
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
			
			broker = datacenter1.getBroker();
			
			vmlist = new ArrayList<Vm>();
			
			int mips = 1000;
			int brokerId = broker.getId();
			long size = 10000;
			int ram = 512;
			long bw = 1000;
			int pesNumber = 1;
			String vmm = "Xen";
			int j=0;
			int t = 0;
			Vm vm = new Vm(0, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			vm.setHost(datacenter1.getHostList().get((t = j++ % datacenter1.getHostList().size())));
			vmlist.add(vm);				
			broker.submitVmList(vmlist);
			CloudSim.startSimulation();
			CloudSim.stopSimulation();
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);
			datacenter1.printDebts();
			Log.printLine("Happyprostarted finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	private static myclass_datacenter createDatacenter(String name) {

		List<Host> hostList = new ArrayList<Host>();

		List<Pe> peList = new ArrayList<Pe>();
		int mips = 1000;
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
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;
		String indent = "    ";
		
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "  Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) 
		{
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) 
			{
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent// + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) 
						+ indent + indent + indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent + indent
						+ dft.format(cloudlet.getFinishTime()));// + indent + indent + indent
				wait += cloudlet.getWaitingTime();
			}
		}
		System.out.println("Size " + size);
	//	avewait = wait / size;
		System.out.println("Max = " + broker.getmax() + " Min " + broker.getmin());// + " Avewaiting " + avewait);
	}

}