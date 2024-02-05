package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import co.gov.ugpp.parafiscales.servicios.liquidador.entity.CobParamGeneral;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.CobFlex;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.AportanteLIQ;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.CobSbsis;

//@Name("cacheService")
//@Scope(ScopeType.APPLICATION)
//@AutoCreate
public class CacheService implements Serializable
{
   private static final long serialVersionUID = -3005181377642023009L;

   public static final String REGION_COBPARAMGENERAL = "cob_param_general";
   public static final String REGION_COBFLEX = "cob_flex";
   public static final String REGION_APORTANTELIQ = "aportante_liq";
   public static final String REGION_COBSBSIS = "cob_sbsis";   

   public Map<String, CobParamGeneral> cobParamGeneral;
   public Map<Short, CobFlex> cobFlex;
   public Map<Short, AportanteLIQ> aportanteLiq;
   public Map<Short, CobSbsis> cobSbsis;   

   //@Create
   public void createInstance()
   {
      this.cobParamGeneral = new ConcurrentHashMap<String, CobParamGeneral>();
      this.cobFlex = new ConcurrentHashMap<Short, CobFlex>();
      this.aportanteLiq = new ConcurrentHashMap<Short, AportanteLIQ>();
      this.cobSbsis = new ConcurrentHashMap<Short, CobSbsis>();      
   }

   public void put(String region, Object key, Object object)
   {
      if (REGION_COBPARAMGENERAL.equals(region)){
    	  if (!cobParamGeneral.containsKey((String) key)){
    		  cobParamGeneral.put((String) key, (CobParamGeneral) object);	  
    	  }
      }      
      else if (REGION_COBFLEX.equals(region))
         cobFlex.put((Short) key, (CobFlex) object);
      else if (REGION_APORTANTELIQ.equals(region))
         aportanteLiq.put((Short) key, (AportanteLIQ) object);
      else if (REGION_COBSBSIS.equals(region)){
    	  if (!cobSbsis.containsKey((Integer) key)) {
    		  cobSbsis.put((Short) key, (CobSbsis) object);
		}
      }   

   }

   public void putAll(String region, List<?> list)
   {
      for (Object obj : list)
      {
         if (REGION_COBPARAMGENERAL.equals(region)){
        	 if (((CobParamGeneral) obj).getIdParametro()!=null){
            	 put(region, ((CobParamGeneral) obj).getIdParametro(), obj);        		 
        	 }
         }
            
         else if (REGION_COBFLEX.equals(region))
            put(region, ((CobFlex) obj).getId(), obj);
         else if (REGION_APORTANTELIQ.equals(region))
            put(region, ((AportanteLIQ) obj).getId(), obj);
         else if (REGION_COBSBSIS.equals(region)){
        	 if (((CobSbsis) obj).getId()!=null) {
        		 put(region, ((CobSbsis) obj).getId(), obj);
			}
         }         
      }
   }

   public void remove(String region, Object key)
   {
      if (REGION_COBPARAMGENERAL.equals(region))
         cobParamGeneral.remove((Short) key);
      else if (REGION_COBFLEX.equals(region))
         cobFlex.remove((Short) key);
      else if (REGION_APORTANTELIQ.equals(region))
         aportanteLiq.remove((Short) key);
      else if (REGION_COBSBSIS.equals(region))
          cobSbsis.remove((Long) key);      

   }

   public Object get(String region, Object key)
   {
      if (REGION_COBPARAMGENERAL.equals(region))
         return cobParamGeneral.get(key);
      else if (REGION_COBFLEX.equals(region))
         return cobFlex.get(key);
      else if (REGION_APORTANTELIQ.equals(region))
         return aportanteLiq.get(key);
      else if (REGION_COBSBSIS.equals(region))
          return cobSbsis.get(key);      

      return null;
   }

}
