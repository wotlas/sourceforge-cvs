package wotlas.libs.persistence;

import java.lang.reflect.Method;

/** An Object Dump Utility.
 * outputs all the properties - which have a public getter - of an object
 * 
 * Example:
 * import wotlas.common.universe.WorldMap;
 * WorldMap myWorldMap = new WorldMap();
 * ObjectDump.dumpObjectByInstance(myWorldMap);
 *
 * @author Petrus
 */

public class ObjectDump
{
  /** Constructor
   */
  public ObjectDump() {}
  
  /** Dump an object
   */
  public static boolean dumpObjectByInstance(Object obj)
  {  
    //System.out.println("Object to be dumped: " + obj.toString() + "\n");
    
    System.out.println("<");
    if (obj == null)
    {
      System.out.println("object null");
    }
    else
    {
      Class c = obj.getClass();
      Method[] m = c.getMethods();
      dumpMethods(m, obj);
    }
    System.out.println(">");
    
    return true;
  }
  
  protected static boolean dumpMethods(Method[] array, Object obj)
  {
    int i,j;
    String methodName;
    Method m;
    Object o;
    Object[] os;
    
    try
    {
      for (i=0; i<array.length; i++)
      {
        m = array[i];
        methodName = m.getName();
        if (methodName.startsWith("get"))
        {
          // System.out.print(methodName + ": ");
          o = m.invoke(obj, (Object[]) null);
          if (o != null )
          {
            /*if (o.getClass().isPrimitive()) {
              System.out.println(methodName.substring(3) + "=" + os);
            }*/
            if (o.getClass().isArray()) {
              os = (Object[]) o;
              for (j=0; j<os.length; j++)
              {
                System.out.println(os[j].getClass().getName() + "[" + j + "]");
                dumpObjectByInstance(os[j]);
              }
            } else {
              System.out.println(methodName.substring(3) + "=" + o);
            }
          } else {
            System.out.println(methodName.substring(3) + "=" + o);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("IllegalAccessException: " + e.toString());
    }
    
    return true;
  }
}