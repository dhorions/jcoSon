/*
 Copyright(c)  Security Weaver, LLC.  All Rights Reserved.
 */
package be.quodlibet.jcoSon;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class jcoSonTest
{
    static JCoDestination destination;
    public jcoSonTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws JCoException
    {
        destination = JCoDestinationManager.getDestination("SAPSYSTEM");
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Test
    public void testConnection() throws JCoException
    {
        // Test that a connection to SAPSYSTEM (specified in file SAPSYSTEM.jcoDestination) can be made
        assertTrue(destination.isValid());
    }
    @Test
    public void testSimpleParametersJson() throws JCoException, ParseException
    {
        // Test that parameters to a SAP BAPI can be set using a json object
        jcoSon jco = new jcoSon(destination.getRepository().getFunction("BAPI_BANK_GETDETAIL"));
        /*
         {
            "BANKKEY": "083000108",
            "BANKCOUNTRY": "US"
          } 
         */
        jco.setParameters("{\"BANKKEY\":\"083000108\",\"BANKCOUNTRY\":\"US\"}");
        String resultJson = jco.execute(destination);
        System.out.println("Results :"+ resultJson);
        assertNotNull(resultJson);
    }
    @Test
    public void testStructureParametersJson() throws JCoException, ParseException
    {
        // Test that importing or changing parameters to a SAP BAPI can be set using a json object
        //CAUTION : this will change the first name of the user TESTUSER in your SAP system
        jcoSon jco = new jcoSon(destination.getRepository().getFunction("BAPI_USER_CHANGE"));
        /*
         {
            "USERNAME": "TESTUSER",
            "ADDRESS": {
              "FIRSTNAME": "New First Name"
            },
            "ADDRESSX": {
              "FIRSTNAME": "X"
            }
          }
         */
        jco.setParameters("{\"USERNAME\":\"TESTUSER\",\"ADDRESS\":{\"FIRSTNAME\":\"New First Name\"},\"ADDRESSX\":{\"FIRSTNAME\":\"X\"}}");
        String resultJson = jco.execute(destination);
        System.out.println("Results :"+ resultJson);
        assertNotNull(resultJson);
    }
     @Test
    public void testTableParametersJson() throws JCoException, ParseException
    {
        // Test that Table parameters to a SAP BAPI can be set using a json object

        jcoSon jco = new jcoSon(destination.getRepository().getFunction("RFC_READ_TABLE"));
        /*
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
         */
        jco.setParameters("{\"QUERY_TABLE\":\"USR02\",\"DELIMITER\":\",\",\"OPTIONS\":[{\"TEXT\":\"BNAME LIKE 'TEST%'\"}],\"FIELDS\":[{\"FIELDNAME\":\"BNAME\"},{\"FIELDNAME\":\"CLASS\"}]}");
        String resultJson = jco.execute(destination);
        System.out.println("Results :"+ resultJson);
        assertNotNull(resultJson);
    }
}