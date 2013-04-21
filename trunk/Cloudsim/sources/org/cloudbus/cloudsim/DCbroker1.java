package org.cloudbus.cloudsim;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

class DCbroker1 extends DatacenterBroker {

		private double delay;


		public DCbroker1(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

		static private float totaldelay;
	
		

	public static final int PERIODIC_EVENT = 67567; //choose any unused value you want to represent the tag.


	 @Override
	 protected void processOtherEvent(SimEvent ev) {
	   if (ev == null){
	     Log.printLine("Warning: "+CloudSim.clock()+": "+this.getName()+": Null event ignored.");
	   } else {
	     int tag = ev.getTag();
	     switch(tag){
	       case PERIODIC_EVENT: processPeriodicEvent(ev); break;
	       default: Log.printLine("Warning: "+CloudSim.clock()+":"+this.getName()+": Unknown event ignored. Tag:" +tag);
	     }
	   }
	 }

	 private void processPeriodicEvent(SimEvent ev) {
	   
		int userId = 3;
		int cloudlets = 10;
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		long length = 4000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}
		//broker.submitCloudletList(list);
	    totaldelay =+ 1000;
	    boolean generatePeriodicEvent;
	    if(totaldelay <= 10000)
	    	generatePeriodicEvent=true;
	    else
	    	generatePeriodicEvent=false;
		if (generatePeriodicEvent) 
			send(getId(),delay,PERIODIC_EVENT,ev);	    
	 }
	}