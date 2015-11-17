This page shows code samples about the ToleradoSObject. Tolerado SObject simplifies the life, by avoid all hassles in using Apache Axis MessageElement or SOject.

# Code Samples #
## Create new Sobject - Sample ##
```


// Create new Sobject by just passing in the SobjectType
ToleradoSobject sobj = new ToleradoSobject("Contact");
// use setAttribute() method to set values for any Sobject fields
sobj.setAttribute("FirstName", "Abhinav");
sobj.setAttribute("LastName", "Gupta");

// Once you are done with updates fetch the updated Sobejct
SObject updatedSObject = sobj.getUpdatedSObject();
SObject[] sObjects = new SObject[] { updatedSObject };

// Partner Stub will be needed when working with ToleradoSobject
ToleradoPartnerStub partnerStub = new ToleradoPartnerStub(new Credential("userName", "password"));
// Call the create via the Same Partner Stub
// SaveResult is the same Apache Axis class we all know
SaveResult[] saveResults = partnerStub.create(sObjects);

```

## Query/Update SObject - Sample ##

```
// replace it with your contact sobject's id
String sObjectId = "<Contact Id>";
ToleradoPartnerStub partnerStub = new ToleradoPartnerStub(new Credential("userName", "password"));
// Do normal Soql Call
QueryResult qr = partnerStub.query("Select Id, FirstName, LastName from Contact where id = '" + sObjectId + "'");
if (ArrayUtils.isEmpty(qr.getRecords())) {
    throw new ToleradoException("Failed to query the desired Sobject");
}

SObject sobj = qr.getRecords(0);
// QUERY Sobject

// To get the ToleradoSobject, just wrap the newly fetched Sobject as shown below.
ToleradoSobject tSobj = new ToleradoSobject(sobj);
// See the ease by which you can query the fields like FirstName and LastName
System.out.println("FirstName:" + tSobj.getTextValue("FirstName"));
System.out.println("LastName:" + tSobj.getTextValue("LastName"));

// UPDATION of Sobject

// Simply call the setAttribute to update the desired fields
tSobj.setAttribute("FirstName", "Abhinav2");
// Get the updated Apache Axis Sobject back
SObject updatedSObject = tSobj.getUpdatedSObject();
SObject[] sObjects = new SObject[] { updatedSObject };
// Use the Tolerado partner stub's update call 
// SaveResult is again the same ApacheAxis class we know
SaveResult[] saveResults = partnerStub.update(sObjects);
```

## Extending ToleradoSObject - Sample ##
You might want to extend ToleradoSObject for more reusable java classes per Sobject. For code sample we have created ContactSObject with two fields extending ToleradoSobject. Note how the getter/setters are added for FirstName and LastName.

You can view complete source of this class [here](http://code.google.com/p/tolerado-salesforce-web-services-client-wrappers/source/browse/trunk/Tolerado-Samples/src/com/tgerm/tolerado/samples/axis14/sobject/ContactSObject.java)

Plus, we have added another sample class that uses ContactSobject for Salesforce CRUD operations, source code is available [here](http://code.google.com/p/tolerado-salesforce-web-services-client-wrappers/source/browse/trunk/Tolerado-Samples/src/com/tgerm/tolerado/samples/axis14/sobject/ExtendingSObjectSample.java)


```
public class ContactSObject extends ToleradoSobject {
	public ContactSObject() {
		// Just pass the Sobject type
		super("Contact");
	}

	public ContactSObject(SObject sobj) {
		super(sobj);
	}

	public String getFirstName() {
		// delegates to getTextValue for fetching the correct field value
		return getTextValue("FirstName");
	}

	public String getLastName() {
		return getTextValue("LastName");
	}

	public void setFirstName(String val) {
		// delegates to setAttribute for setting the correct attribute.
		setAttribute("FirstName", val);
	}

	public void setLastName(String val) {
		setAttribute("LastName", val);
	}
}

```