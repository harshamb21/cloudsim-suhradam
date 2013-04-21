

package org.cloudbus.cloudsim.Project.MM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.lists.PowerVmList;

/**
 * MyVmAllocationPolicySingleThreshold is an VMAllocationPolicy that
 * chooses a host with the least power increase due to utilization increase
 * caused by the VM allocation.
 
 */
public class VmAllocationMinimizationOfMigration extends VmAllocationPolicySimple {

	/** The hosts PEs. */
	protected Map<String, Integer> hostsPes;

	/** The hosts utilization. */
	protected Map<Integer, Double> hostsUtilization;

	/** The hosts memory. */
	protected Map<Integer, Integer> hostsRam;

	/** The hosts bw. */
	protected Map<Integer, Long> hostsBw;

	/** The old allocation. */
	private List<Map<String, Object>> savedAllocation;

	/** The utilization threshold. */
	private double maxUtilizationThreshold;
	
	private double minUtilizationThreshold;
	
	private static boolean flag = false;

	/**
	 * Instantiates a new vM provisioner mpp.
	 *
	 * @param list the list
	 * @param maxUtilizationThreshold the utilization bound
	 */
	public VmAllocationMinimizationOfMigration(List<? extends PowerHost> list, double maxUtilizationThreshold ,double minUtilizationThreshold) {
		super(list);
		setSavedAllocation(new ArrayList<Map<String,Object>>());
		setmaxUtilizationThreshold(maxUtilizationThreshold);
		setminUtilizationThreshold(minUtilizationThreshold);
	}

	/**
	 * Determines a host to allocate for the VM.
	 *
	 * @param vm the vm
	 *
	 * @return the host
	 */
	public PowerHost findHostForVm(Vm vm) {
		double minPower = Double.MAX_VALUE;
		PowerHost allocatedHost = null;

		for (PowerHost host : this.<PowerHost>getHostList()) {
			if (host.isSuitableForVm(vm)) {
				double maxUtilization = getMaxUtilizationAfterAllocation(host, vm);
				if ((!vm.isRecentlyCreated() && maxUtilization > getmaxUtilizationThreshold()) || (vm.isRecentlyCreated() && maxUtilization > 1.0)) {
					continue;
				}
				try {
					double powerAfterAllocation = getPowerAfterAllocation(host, vm);
					if (powerAfterAllocation != -1) {
						double powerDiff = powerAfterAllocation - host.getPower();
						if (powerDiff < minPower) {
							minPower = powerDiff;
							allocatedHost = host;
						}
					}
				} catch (Exception e) {
				}
			}
		}

		return allocatedHost;
	}

	/**
	 * Allocates a host for a given VM.
	 *
	 * @param vm VM specification
	 *
	 * @return $true if the host could be allocated; $false otherwise
	 *
	 * @pre $none
	 * @post $none
	 */
	@Override
	public boolean allocateHostForVm(Vm vm) {
		PowerHost allocatedHost = findHostForVm(vm);
		if (allocatedHost != null && allocatedHost.vmCreate(vm)) { //if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), allocatedHost);
			if (!Log.isDisabled()) {
				Log.print(String.format("%.2f: VM #" + vm.getId() + " has been allocated to the host #" + allocatedHost.getId() + "\n", CloudSim.clock()));
			}
			return true;
		}
		return false;
	}

	/**
	 * Releases the host used by a VM.
	 *
	 * @param vm the vm
	 *
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVm(Vm vm) {
		if (getVmTable().containsKey(vm.getUid())) {
			PowerHost host = (PowerHost) getVmTable().remove(vm.getUid());
			if (host != null) {
				host.vmDestroy(vm);
			}
		}
	}

	/**
	 * Optimize allocation of the VMs according to current utilization.
	 *
	 * @param vmList the vm list
	 * @param maxUtilizationThreshold the utilization bound
	 *
	 * @return the array list< hash map< string, object>>
	 */
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		List<Map<String, Object>> migrationMap = new ArrayList<Map<String, Object>>();
		if (vmList.isEmpty()) {
			return migrationMap;
		}
		saveAllocation(vmList);
		List<Vm> vmsToRestore = new ArrayList<Vm>();
		vmsToRestore.addAll(vmList);

		List<Vm> vmsToMigrate = new ArrayList<Vm>();
		List<PowerHost>hst = this.<PowerHost>getHostList();
		List<Vm> hostvmsToMigrate = null;List<Vm> ls = new ArrayList<Vm>();
		boolean failflag= false;
		List<PowerHost> temphost= new ArrayList<PowerHost>();
		if(getflag()){
			for (PowerHost phost : hst) {
				
				hostvmsToMigrate = new ArrayList<Vm>();
				double maxUtilization = phost.getMaxUtilization();
			
				if(maxUtilization > getmaxUtilizationThreshold()){
					hostvmsToMigrate = phost.getVmList();
				
					PowerVmList.sortByCpuUtilization(hostvmsToMigrate);
				
					for(Vm vm : hostvmsToMigrate){
						if (vm.isRecentlyCreated() || vm.isInMigration()) 
							continue;
						vmsToMigrate.add(vm);
						//vm.getHost().vmDestroy(vm);
						if(phost.getMaxUtilization() <= getmaxUtilizationThreshold())
							break;
					}
				}	
				else if(maxUtilization < getminUtilizationThreshold()){
					//temphost=phost;
					hostvmsToMigrate = phost.getVmList();
					failflag = true;
					for(Vm vm : hostvmsToMigrate)
					{
						if (vm.isRecentlyCreated() || vm.isInMigration()){
							failflag=false;
							continue;
						}
						vmsToMigrate.add(vm);
					} 
					if(failflag==true){
						temphost.add(phost);
						failflag=false;
					}
				}
			}
			for (Vm vm:vmsToMigrate)
				vm.getHost().vmDestroy(vm);
			for(PowerHost tph: temphost) {
				tph.setFailed(true);
			}
		}
		else {
			for (Vm vm : vmList) {
				if (vm.isRecentlyCreated() || vm.isInMigration()) {
					continue;
				}
				vmsToMigrate.add(vm);
				vm.getHost().vmDestroy(vm);			
			}
			setflag();
		}
		
		PowerVmList.sortByCpuUtilization(vmsToMigrate);
		
		for (PowerHost host : this.<PowerHost>getHostList()) {
			host.reallocateMigratingVms();
		}

		for (Vm vm : vmsToMigrate) {
			PowerHost oldHost = (PowerHost) getVmTable().get(vm.getUid());
			PowerHost allocatedHost = findHostForVm(vm);
			if (allocatedHost != null && allocatedHost.getId() != oldHost.getId()) {
				allocatedHost.vmCreate(vm);
				Log.printLine("VM #" + vm.getId() + " allocated to host #" + allocatedHost.getId());

				Map<String, Object> migrate = new HashMap<String, Object>();
				migrate.put("vm", vm);
				migrate.put("host", allocatedHost);
				migrationMap.add(migrate);
			}
		}

		restoreAllocation(vmsToRestore, getHostList());

		return migrationMap;
	}

	/**
	 * Save allocation.
	 *
	 * @param vmList the vm list
	 */
	protected void saveAllocation(List<? extends Vm> vmList) {
		getSavedAllocation().clear();
		for (Vm vm : vmList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("vm", vm);
			map.put("host", vm.getHost());
			getSavedAllocation().add(map);
		}
	}

	/**
	 * Restore allocation.
	 *
	 * @param vmsToRestore the vms to restore
	 */
	protected void restoreAllocation(List<Vm> vmsToRestore, List<Host> hostList) {
		for (Host host : hostList) {
			host.vmDestroyAll();
			host.reallocateMigratingVms();
		}
		for (Map<String, Object> map : getSavedAllocation()) {
			Vm vm = (Vm) map.get("vm");
			PowerHost host = (PowerHost) map.get("host");
			if (!vmsToRestore.contains(vm)) {
				continue;
			}
			if (!host.vmCreate(vm)) {
				Log.printLine("Something is wrong, the VM can's be restored");
				System.exit(0);
			}
			getVmTable().put(vm.getUid(), host);
			Log.printLine("Restored VM #" + vm.getId() + " on host #" + host.getId());
		}
	}

	/**
	 * Gets the power after allocation.
	 *
	 * @param host the host
	 * @param vm the vm
	 *
	 * @return the power after allocation
	 */
	protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
		List<Double> allocatedMipsForVm = null;
		PowerHost allocatedHost = (PowerHost) vm.getHost();

		if (allocatedHost != null) {
			allocatedMipsForVm = allocatedHost.getAllocatedMipsForVm(vm);
		}

		if (!host.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
			return -1;
		}

		double power = host.getPower();

		host.deallocatePesForVm(vm);

		if (allocatedHost != null && allocatedMipsForVm != null) {
			vm.getHost().allocatePesForVm(vm, allocatedMipsForVm);
		}

		return power;
	}

	/**
	 * Gets the power after allocation.
	 *
	 * @param host the host
	 * @param vm the vm
	 *
	 * @return the power after allocation
	 */
	protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
		List<Double> allocatedMipsForVm = null;
		PowerHost allocatedHost = (PowerHost) vm.getHost();

		if (allocatedHost != null) {
			allocatedMipsForVm = vm.getHost().getAllocatedMipsForVm(vm);
		}

		if (!host.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
			return -1;
		}

		double maxUtilization = host.getMaxUtilizationAmongVmsPes(vm);

		host.deallocatePesForVm(vm);

		if (allocatedHost != null && allocatedMipsForVm != null) {
			vm.getHost().allocatePesForVm(vm, allocatedMipsForVm);
		}

		return maxUtilization;
	}

	protected double getMaxUtilizationAfterDeallocation(Vm vm) {
		List<Double> allocatedMipsForVm = null;
		PowerHost allocatedHost = (PowerHost) vm.getHost();
		
		if (allocatedHost != null) {
			allocatedMipsForVm = vm.getHost().getAllocatedMipsForVm(vm);
		}
		allocatedHost.deallocatePesForVm(vm);
		
		double maxUtilization = allocatedHost.getMaxUtilization();
		
		if (allocatedHost != null && allocatedMipsForVm != null) {
			allocatedHost.allocatePesForVm(vm, allocatedMipsForVm);
		}
		
		return maxUtilization ;
	}
	/**
	 * Gets the saved allocation.
	 *
	 * @return the saved allocation
	 */
	protected List<Map<String, Object>> getSavedAllocation() {
		return savedAllocation;
	}

	/**
	 * Sets the saved allocation.
	 *
	 * @param savedAllocation the saved allocation
	 */
	protected void setSavedAllocation(List<Map<String, Object>> savedAllocation) {
		this.savedAllocation = savedAllocation;
	}

	/**
	 * Gets the utilization bound.
	 *
	 * @return the utilization bound
	 */
	protected double getmaxUtilizationThreshold() {
		return maxUtilizationThreshold;
	}

	/**
	 * Sets the utilization bound.
	 *
	 * @param maxUtilizationThreshold the new utilization bound
	 */
	protected void setmaxUtilizationThreshold(double maxUtilizationThreshold) {
		this.maxUtilizationThreshold = maxUtilizationThreshold;
	}
	
	protected double getminUtilizationThreshold() {
		return minUtilizationThreshold;
	}

	/**
	 * Sets the utilization bound.
	 *
	 * @param minUtilizationThreshold the new utilization bound
	 */
	protected void setminUtilizationThreshold(double minUtilizationThreshold) {
		this.minUtilizationThreshold = minUtilizationThreshold;
	}

	protected boolean getflag() {
		return flag;
	}
	
	protected void setflag() {
		this.flag = true;
	}
}
