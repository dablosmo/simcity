package housing.Interfaces;

import housing.personHome;
import housing.personHome.Appliance;
import housing.gui.OccupantGui;

import java.util.List;

import mainCity.PersonAgent;

public interface Occupant {


	//public personHome home;

	//Object needsWork = null;

	List<String> needsWork = null;
	//PersonAgent person = null;

	public abstract void msgAtDestination();

	public abstract void gotHungry();

	public abstract void msgFixed(String appName);

	public abstract void msgNeedFood(List<String> buyFood);

	public abstract void msgCookFood(String foodCh);

	public abstract void msgLeaveHome();

	public abstract personHome getHome();
	
	public abstract void applianceBroke();

	public abstract void msgNeedsMaintenance(String appl);

	

}