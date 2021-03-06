package role.bank;

import role.Role;
import role.bank.BankTellerRole.ClientState;
import role.bank.BankTellerRole.myClient;
import mainCity.bank.BankAccounts;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.gui.BankerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.Banker;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import agent.Agent;


public class BankerRole extends Role implements WorkerRole, Banker {
	
	
	public BankAccounts bankaccounts;
	String name;
	public myClient myclient;
	BankerGui bGui;
	private PersonAgent person;
	public enum ClientState{none,wantsLoan, wantsAccount,done}
	private boolean onDuty;
	
	public class myClient{
		public PersonAgent p;
	    public BankCustomer bc;
	    String mcname;
	    double accountnumber;
	    double amount;
	    
	    public ClientState cs=ClientState.none;
	}
	
	
	public BankerRole(PersonAgent p, String name){
		super(p);
		this.person=p;
		this.name=name;
		log("Banker initiated");
		onDuty=true;
	}
	

	public void setBankAccounts(BankAccounts singular){
		this.bankaccounts=singular;
	}
	
	
//Messages


	public void msgGoOffDuty(double d){
		addToCash(d);
		onDuty=false;
		stateChanged();
	}
	


	public void msgGoToWork(){
		
		log("Banker at station");
		onDuty=true;
		stateChanged();
	}
	

	
	public void msgIWantALoan(BankCustomer b, double accnum, double amnt){
		log("Recieved msgIWantALoan from customer");
		myclient=new myClient();
		myclient.bc=b;
		myclient.amount=amnt;
		myclient.cs=ClientState.wantsLoan;
		myclient.accountnumber=accnum;
		stateChanged();
	}
	

	public void msgIWantNewAccount(PersonAgent p, BankCustomer b, String name, double amnt){
		log("Recieved msgIWantNewAccount from customer");
		myclient=new myClient();
		myclient.mcname=name;
		myclient.p=p;
		myclient.bc=b;
		myclient.amount=amnt;
		myclient.cs=ClientState.wantsAccount;
		stateChanged();
	}
	
	
	

	public boolean pickAndExecuteAnAction() {
		if(onDuty){
			doGoToWork();
		}
	
		
		if(myclient!=null){
			
			
			if(myclient.cs==ClientState.wantsAccount){
				createAccount(myclient);
				myclient.cs=ClientState.done;
				return true;
			}
			
			if(myclient.cs==ClientState.wantsLoan){
				processLoan(myclient);
				myclient.cs=ClientState.done;
				return true;
			}
			
			
		}
		
		if(!onDuty && myclient ==null){
			doLeaveWork();
			return false;
			
		}
		
		
		
		return false;
	}
	
//Actions
	private void doGoToWork(){
		bGui.doGoToWork();
			
			
		}
		
	private void doLeaveWork(){
			bGui.doLeaveWork();
			setInactive();
			onDuty=true;
		}
		
		
	
	private void createAccount(myClient mctemp){
		log("Creating new account");
		double temp =bankaccounts.getNumberOfAccounts();
		bankaccounts.addAccount(mctemp.mcname, mctemp.amount, mctemp.p, temp);
		mctemp.bc.msgAccountCreated(temp);
		
		mctemp.bc.msgRequestComplete(mctemp.amount*-1, mctemp.amount);
		myclient=null;
	}
	
	
	private void processLoan(myClient mctemp){
		log("processing loan");
		if(mctemp.accountnumber==-1){
			createAccount(mctemp);
		}
		for(BankAccount b: bankaccounts.accounts){
			if(mctemp.accountnumber==b.accountNumber){
				if(b.debt>=500){
					b.creditScore-=20;
				}
				if(b.debt<=500){
					b.creditScore+=20;
				}
				if(b.creditScore>=600){
					b.debt+=mctemp.amount;
					mctemp.bc.msgLoanApproved(mctemp.amount);
					Do("Loan approved");
					myclient=null;
					return;
				}
		
			}		
		}//end for
		//else
			
			mctemp.bc.msgLoanDenied(mctemp.amount);
			Do("Loan denied");
			return;
		
	}

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_BANKER, this.getName(), s);
	}
	

	public void setGui(BankerGui gui){
		this.bGui=gui;
	}
	
	
	
}
