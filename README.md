jcoSon
======

Json api for SAP JCO ( SAP Java Connector 3.x)


This will allow you to call SAP BAPIs and function modules using json.  
Both the parameters you pass to the function modules and the results you get back will be in json format.

Examples : 

1. Pass simple values to a BAPI
```javascript
{
  "BANKKEY": "083000108",
  "BANKCOUNTRY": "US"
} 
```
```java
jcoSon jco = new jcoSon(destination.getRepository().getFunction("BAPI_BANK_GETDETAIL"));
jco.setParameters("{\"BANKKEY\":\"083000108\",\"BANKCOUNTRY\":\"US\"}");
String resultJson = jco.execute(destination);
System.out.println("Results :"+ resultJson);
```

```javascript
{
  "BANK_ADDRESS": {
    "BANK_NAME": "First Union Bank & Trust",
    "REGION": "IL",
    "STREET": "500 Main Street",
    "CITY": "Chicago",
    "SWIFT_CODE": "",
    "BANK_GROUP": "",
    "POBK_CURAC": "",
    "BANK_NO": "083000108",
    "POST_BANK": "",
    "BANK_BRANCH": "Main Office",
    "ADDR_NO": ""
  },
  "BANK_DETAIL": {
    "CREAT_DATE": "Fri Jan 03 00:00:00 CET 1997",
    "CREATOR": "GRAVEN",
    "METHOD": "",
    "FORMATTING": "",
    "BANK_DELETE": ""
  }
}
```
2.Pass Structure to a BAPI
```javascript
{
  "USERNAME": "TESTUSER",
  "ADDRESS": 
  {
    "FIRSTNAME": "New First Name"
  },
  "ADDRESSX": 
  {
    "FIRSTNAME": "X"
  }
 } 
```
```java
jcoSon jco = new jcoSon(destination.getRepository().getFunction("BAPI_USER_CHANGE"));
jco.setParameters("{\"USERNAME\":\"TESTUSER\",\"ADDRESS\":{\"FIRSTNAME\":\"New First Name\"},\"ADDRESSX\":{\"FIRSTNAME\":\"X\"}}");
String resultJson = jco.execute(destination);
System.out.println("Results :"+ resultJson);
```

```javascript
{
  "RETURN": [
    {
      "TYPE": "S",
      "ID": "01",
      "NUMBER": "039",
      "MESSAGE": "User TESTUSER has changed",
      "LOG_NO": "",
      "LOG_MSG_NO": "000000",
      "MESSAGE_V1": "TESTUSER",
      "MESSAGE_V2": "",
      "MESSAGE_V3": "",
      "MESSAGE_V4": "",
      "PARAMETER": "",
      "ROW": 0,
      "FIELD": "",
      "SYSTEM": "XXXCLNT000"
    }
  ]
}
```

3.Pass Table to a BAPI
```javascript
{
            "QUERY_TABLE": "USR02",
            "DELIMITER": ",",
            "OPTIONS": [
              {
                "TEXT": "BNAME LIKE 'TEST%'"
              }
            ],
            "FIELDS": [
              {
                "FIELDNAME": "BNAME"
              },
              {
                "FIELDNAME": "CLASS"
              }
            ]
          }
```
```java
jcoSon jco = new jcoSon(destination.getRepository().getFunction("RFC_READ_TABLE"));
jco.setParameters("{\"QUERY_TABLE\":\"USR02\",\"DELIMITER\":\",\",\"OPTIONS\":[{\"TEXT\":\"BNAME LIKE 'TEST%'\"}],\"FIELDS\":[{\"FIELDNAME\":\"BNAME\"},{\"FIELDNAME\":\"CLASS\"}]}");
String resultJson = jco.execute(destination);
 System.out.println("Results :"+ resultJson);
```

```javascript
{
  "DATA": [
    {
      "WA": "TESTUSER     ,DEVELOPER"
    },
    {
      "WA": "TEST_001    ,TEST"
    },
    {
      "WA": "TEST_001    ,TEST"
    }
  ]
}
```
