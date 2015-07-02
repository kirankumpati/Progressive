package com.triumvir.progressive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sailpoint.api.ObjectUtil;
import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.rest.BaseResource;
import sailpoint.service.AccountUnlockData;
import sailpoint.tools.GeneralException;

@Path("/pims")
public class PIMSPasswordManagementResource extends BaseResource {
	
	Logger mylogger;
	SailPointContext myContext;
	Identity spAccount;
	Application ADProg1App;
	JSONObject json ;
	String PriSec;
	String appName;
	Link link;
	List list;
	List count = new ArrayList();
	Iterator iterator;
	String generatedPassword;
	AccountUnlockData unLockData ;
	String instance = null;
	int authAnswers = 0;
	int reqAnswers = 0;
	
	public PIMSPasswordManagementResource() throws GeneralException, JSONException {
		
		super();
		json = new JSONObject();
		mylogger = Logger.getLogger("PIMS Password. Management Resource");
		mylogger.setLevel(Level.DEBUG);
		myContext = SailPointFactory.getCurrentContext();
		appName = "DEV.Active Directory.PROG1";
		reqAnswers = 2;
		
	}
	
	@GET
	@Path("{accountName}")
	@Produces("text/plain")
	public String checkSSPRAuthorization(@PathParam("accountName") String accountName) throws JSONException {
		
		try
		{
		spAccount = myContext.getObjectByName(Identity.class,accountName);
		ADProg1App = myContext.getObjectByName(Application.class, appName);
		list = spAccount.getLinks(ADProg1App);
		authAnswers = spAccount.getAuthenticationAnswers().size();
		
			if(authAnswers >= reqAnswers)
			{
				System.out.println("Authentication answers satisfied");
			}
			else
			{
				count.add("Authentication answers not satisfied");
			}
			
			if (list != null )
			{
	    		iterator= list.iterator();
	    		while(iterator.hasNext())
	    		{
	    		link = (Link) iterator.next();
	    		if(ObjectUtil.getIdentityFromLink(myContext, ADProg1App, instance, link.getNativeIdentity())!=null)
	    		{
	    			PriSec = (String)link.getAttribute("PriSec");
	    		
	    			if(PriSec.equalsIgnoreCase("Primary")) 
					{
					System.out.println("The account is primary account");
					
						if(spAccount.isInactive()==false)
						{
							System.out.println("The account is enabled ");
							 																
							if(((String) spAccount.getAttribute("Disabled_In_SPA")).equalsIgnoreCase("true"))
							{
								System.out.println("Disabled_In_SPA : " + spAccount.getAttribute("Disabled_In_SPA"));
							}
								else
								{
									count.add("Disabled_In_SPA is not TRUE");
								}
							if(((String) spAccount.getAttribute("SSPR_Registration_Completed")).equalsIgnoreCase("true"))
							{
								System.out.println("SSPR_Registration_Completed  : " + spAccount.getAttribute("SSPR_Registration_Completed"));
							}
								else
								{
									count.add("SSPR_Registration_Completed is not TRUE");
								}
							if(((String) spAccount.getAttribute("SSPR_Enabled")).equalsIgnoreCase("true"))
							{
								System.out.println("SSPR_Enabled  : " + spAccount.getAttribute("SSPR_Enabled"));
								System.out.println("success");
							}
								else
								{
									count.add("SSPR_Enabled is not TRUE");
								}
						}	
						else
							{
							count.add("AD account not Enabled");
							}
					}
					else
					{
					count.add("AD account is not primary account");
					}
	    		}
			
	    		}   // end of while
			}
		else
			{
				count.add("Link not available");
			}
		}  // end of try
    	catch(Exception e)
		{
			System.out.println("The exception : "+ e.getMessage());
			System.out.println(count);
		}
		
		if(count.isEmpty())
		{
			json.put("authorized", "success");	
			return json.toString();
		}
		else
		{	
			System.out.println(count);
			json.put("authorized", "false");	
			return json.toString();
		}
	}
	
	@GET
	@Path("{accountName}/{password}")
	@Produces("text/plain")
	public String resetPassword(@PathParam("accountName") String accountName,@PathParam("password") String password) throws GeneralException {
		
		spAccount = myContext.getObjectByName(Identity.class,accountName);
		ADProg1App = myContext.getObjectByName(Application.class, appName);
		
		link = spAccount.getLink(ADProg1App);
		
		PriSec = (String) link.getAttribute("PriSec");
		
		if(PriSec.equalsIgnoreCase("Primary"))
		{


			ProvisioningPlan myPlan = new ProvisioningPlan();
			myPlan.setIdentity(spAccount);
							
			AccountRequest myAcctReq = new AccountRequest();    

			myAcctReq.setApplication(link.getApplicationName());
			System.out.println("native identity " + link.getNativeIdentity());
			myAcctReq.setNativeIdentity(link.getNativeIdentity());	
			myAcctReq.setOperation(AccountRequest.Operation.Modify);

			AttributeRequest myAttributeRequest = new AttributeRequest("password",
					ProvisioningPlan.Operation.Set, "CN=Guest,CN=Users,DC=progsandbox,DC=lcl");
		
			myAttributeRequest.setName("password");
			
			myAttributeRequest.setValue(password); 
		
			myAcctReq.add(myAttributeRequest); 
			myPlan.add(myAcctReq);
			
			Provisioner provisioner = new Provisioner(myContext);
			provisioner.execute(myPlan);	
			
			spAccount.setPassword(password);
		    myContext.saveObject(spAccount);
	        myContext.commitTransaction();
	    
		}
		else
		{
			return "code 400";
		}
		
		return "code 200";
	
	}

	@GET
	//@Path("{accountName}")
	@Produces("text/plain")
	public String setSSPRLock(@PathParam("accountName") String accountName) throws JSONException, GeneralException {
		
		spAccount = myContext.getObjectByName(Identity.class,accountName);
		ADProg1App = myContext.getObjectByName(Application.class, appName);
		
		link = spAccount.getLink(ADProg1App);
	
		if(ObjectUtil.getIdentityFromLink(myContext, ADProg1App, instance, link.getNativeIdentity())!=null)
		{
			spAccount.setAttribute("SSPR_Enabled", "false");
			myContext.saveObject(spAccount);
	        myContext.commitTransaction();
	        json.put("success", "true");
			return json.toString();
		}
		else
		{
			json.put("success", "false");
			return json.toString();
		}
		
	}

}
