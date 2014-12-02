package be.quodlibet.jcoSon;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoDestination;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import org.json.simple.JSONValue;
/**
 *
 * jcoSon will allow you to call SAP BAPIs and function modules using json.
 * Both the parameters you pass to the function modules and the results you get back will be in json format.
 * Homepage : http://dhorions.github.io/jcoSon
 * Examples : https://github.com/dhorions/jcoSon/blob/master/README.md
 * @author Dries Horions <dries@quodlibet.be>
 */
public class jcoSon
{

    JCoFunction function;
    JSONParser parser;
    ContainerFactory containerFactory;
    /**
     *
     * @param function A jco function
     */
    public jcoSon(JCoFunction function )
    {
        this.function = function;
        createJsonParser();
    }

    private void createJsonParser()
    {
        this.parser = new JSONParser();
        containerFactory = new ContainerFactory()
        {
            @Override
            public List creatArrayContainer()
            {
              return new LinkedList();
            }
            @Override
            public Map createObjectContainer()
            {
              return new LinkedHashMap();
            }
        };
    }
    /**
     *
     * @param destination A valid JCo Destination
     * @return json Representation of the JCo Function call
     * @throws JCoException
     */
    public String execute(JCoDestination destination) throws JCoException
    {
        LinkedHashMap resultList = new LinkedHashMap();
        try
        {
            function.execute(destination);
        }
        catch(AbapException e)
        {
           resultList.put(e.getKey(),e);
        }
        catch (JCoException ex)
        {
            resultList.put(ex.getKey(),ex);
        }
        //Export Parameters
        if(function.getExportParameterList()!= null)
        {
            getFields(function.getExportParameterList().getFieldIterator(),resultList);
        }
        //Changing parameters
        if(function.getChangingParameterList()!= null)
        {
            getFields(function.getChangingParameterList().getFieldIterator(),resultList);
        }
        //Table Parameters
        if(function.getTableParameterList()!= null)
        {
            getFields(function.getTableParameterList().getFieldIterator(),resultList);
        }
        return JSONValue.toJSONString(resultList);
    }

    private void getFields(JCoFieldIterator iter,LinkedHashMap resultList)
    {
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            LinkedHashMap map = new LinkedHashMap();
            map.put(f.getName(), f.getValue());
            if(f.isTable())
            {
                resultList.put(f.getName(),getTableParameter(f));
            }
            else if (f.isStructure())
            {
                resultList.put(f.getName(),getStructureParameter(f));
            }
            else
            {
                resultList.put(f.getName(),f.getValue());
            }
         }
    }
    private LinkedList getTableParameter(JCoField table)
    {
        LinkedList l = new LinkedList();
        JCoTable t = table.getTable();
        for (int i = 0; i < t.getNumRows(); i++)
        {
            t.setRow(i);
            JCoFieldIterator iter = t.getFieldIterator();
            LinkedHashMap m = new LinkedHashMap();
            while(iter.hasNextField())
            {
                JCoField f = iter.nextField();
                m.put(f.getName(), t.getValue(f.getName()));
            }
            l.add(m);
        }
        return l;
    }
    private LinkedHashMap getStructureParameter(JCoField structure)
    {
            JCoFieldIterator iter = structure.getStructure().getFieldIterator();
            LinkedHashMap m = new LinkedHashMap();
            while(iter.hasNextField())
            {
                JCoField f = iter.nextField();
                m.put(f.getName(), structure.getStructure().getValue(f.getName()));
            }
            return m;
    }
    /**
     *  Sets the functions parameters from a json String
     * @param jsonParameters a json representation of the function parameters
     * @throws ParseException
     */
    public void setParameters(String jsonParameters) throws ParseException
    {
        Map params = (Map)parser.parse(jsonParameters, containerFactory);
        setParameters(params);
    }
    /**
     * Sets the functions parameters from a Map
     * @param Parameters a Map of parameters of type LinkedHashMap or LinkedList)
     *
     */
    public void setParameters(Map Parameters)
    {
        Iterator iter = Parameters.entrySet().iterator();
        while(iter.hasNext())
        {
            Entry parameter = (Map.Entry)iter.next();
            setParameter(parameter.getKey().toString(),parameter.getValue());
        }
    }
    /**
     * Sets a single parameter
     * @param name the name of the parameter
     * @param value the value of the parameter (String, Integer, LinkedHashMap or LinkedList)
     */
    public void setParameter(String name, Object value)
    {
        if(value instanceof LinkedList)
        {
            setTableParameter(name,(LinkedList)value);
        }
        else if (value instanceof LinkedHashMap)
        {
            setStructureParameter(name,(LinkedHashMap)value);
        }
        else
        {
            setSimpleParameter(name,value);
        }
    }
    /**
     * Sets a single Importing or Changing parameter that is not a structure
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public void setSimpleParameter(String name, Object value)
    {
        //Find Simple, non structure or table parameter with this name and set the appropriate value
        //Importing Parameters
       if(function.getImportParameterList()!= null)
        {
             setSimpleParameterValue(function.getImportParameterList().getFieldIterator(),name, value);
        }
        //Changing Parameters
        if(function.getChangingParameterList()!= null)
        {
             setSimpleParameterValue(function.getChangingParameterList().getFieldIterator(),name, value);
        }
        

    }
    private void setSimpleParameterValue(JCoFieldIterator iter,String name, Object value)
    {

         while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            if(f.getName().equals(name) &!f.isStructure() &!f.isTable())
            {
                f.setValue(value);
            }
        }
    }
    /**
     * Sets a single Importing or Changing parameter that is a structure
     * @param name the name of the parameter
     * @param map the value of the parameter
     */
    public void setStructureParameter(String name, LinkedHashMap map)
    {
        //Find structure parameter with this name and set the appropriate values
        JCoFieldIterator iter = function.getImportParameterList().getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            if(f.getName().equals(name) & f.isStructure())
            {
                Iterator fieldIter = map.entrySet().iterator();
                JCoStructure structure = f.getStructure();
                while(fieldIter.hasNext())
                {
                     Entry field = (Map.Entry)fieldIter.next();
                     structure.setValue(field.getKey().toString(), field.getValue().toString());
                }
            }
        }
    }
    /**
     *  Sets a single Table parameter that is a structure
     * @param name the name of the parameter
     * @param list The value of the parameter (A LinkedList of LinkedHashmaps)
     */
    public void setTableParameter(String name, LinkedList list)
    {
        //Find table parameter with this name and set the appropriate valies
        JCoFieldIterator iter = function.getTableParameterList().getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            if(f.getName().equals(name) & f.isTable() )
            {
                Iterator recordIter = list.listIterator();
                JCoTable table = f.getTable();
                while(recordIter.hasNext())
                {
                   table.appendRow();
                   LinkedHashMap fields = (LinkedHashMap)recordIter.next();
                   Iterator fieldIter = fields.entrySet().iterator();
                   while(fieldIter.hasNext())
                   {
                         Entry field = (Map.Entry)fieldIter.next();
                         table.setValue(field.getKey().toString(), field.getValue().toString());
                   }
                }
            }
        }
    }
}
