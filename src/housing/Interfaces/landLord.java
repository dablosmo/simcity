package housing.Interfaces;

import housing.OccupantRole;
import housing.LandlordRole;
import housing.gui.LandlordGui;

public interface landLord {


	public abstract void msgPleaseFix(OccupantRole occp, String appName);

	public abstract void msgAtDestination();

	public abstract void setRenter(OccupantRole occupant);

	public abstract void setGui(LandlordGui landLordGui);





}