package mainCity.market.interfaces;



public interface EmployeeGuiInterface {	
	public abstract void DoGoToStation();
    public abstract void DoGoToCashier();
    public abstract void DoFulfillOrder();
    public abstract void DoGoToDeliveryMan();
    
    public abstract void DoPickUpWaitingCustomer(int x, int y);
    
}