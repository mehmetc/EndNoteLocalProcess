/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exlibris.primo.utils.formats;

import com.exlibris.primo.srvinterface.PnxConstants;
import com.exlibris.primo.srvinterface.RecordDocDTO;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mehmetc
 */
public class Ris {    
    public static HashMap<String, List> outputMapping = new HashMap<String, List>();
    public static ArrayList tagsThatNeedToBeSplit = new ArrayList();
    
    private Ris() {

    }
                        
    public static synchronized String fromRecordDocDTO(RecordDocDTO record) {
        initializeMapping();        
   //     System.out.println("\n\nTAGHEADINGS:" + record.getValuesNoHL().keySet().toString() + "\n\n");        
        Logger.getLogger(Ris.class.getName()).log(Level.INFO, "\n\nTAGHEADINGS:{0}\n\n", record.getValuesNoHL().keySet().toString());
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter out = new PrintWriter(stringWriter);
            
            String[] type = (String[]) record.getValuesNoHL().get(PnxConstants.TYPE);
            String[] ris_type = (String[]) record.getValuesNoHL().get(PnxConstants.RIS_TYPE);            
            
            //Write mandatory header
            if (ris_type != null) {
                out.println(addHeader(ris_type[0]));
            } else {
                out.println(addHeader(type[0]));
            }
            
            //Map all tags                          
             for (String key : outputMapping.keySet().toArray(new String[outputMapping.size()])) {                  
                boolean pnxAdd = true;
                List outputMap = outputMapping.get(key);
                int outputMapSize = outputMap.size();
                
                if (outputMapSize > 0 && (outputMapSize % 2) == 0) {
                
                    for(int k=0;k < outputMapSize; k += 2) {
                        String[] data = null;
                        String pnxKey = (String) outputMap.get(0+k);
                        Integer pnxKeyContinue = (Integer) outputMap.get(1+k);

                        if (pnxAdd) {
                            pnxAdd = false;
                            data = (String[]) record.getValuesNoHL().get(pnxKey); 
                            if (data != null) {
                                for (int i = 0; i < data.length; i++) {
                                    if (tagsThatNeedToBeSplit.contains(key)) {
                                        String[] d = data[i].split("; ");
                                        for (int j = 0; j < d.length; j++) {                                
                                            out.println(addTag(key, d[j]));
                                        }                            
                                    } else {
                                        out.println(addTag(key, data[i]));
                                    }
                                }
                            } else {
                                if (pnxKeyContinue.intValue() == 1){
                                    pnxAdd = true;
                                }
                            }                                                   
                        }                    
                    }
                }
            }
                                                                      
            //Write mandatory footer
            out.println(addFooter());            
            out.flush();
            out.close();
            return stringWriter.toString();
            
        }catch(Exception ex) {
            Logger.getLogger(Ris.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return "";
    }

    private static void initializeMapping(){                    
        //FORMAT is "TAG, [PNXTag, continue when not found, PNXTag, continue when not found, ...]
            if (outputMapping.isEmpty()){                    
                outputMapping.put("ID", Arrays.asList(PnxConstants.ID, 0));
                outputMapping.put("T1", Arrays.asList(PnxConstants.TITLE,0));
                outputMapping.put("T2", Arrays.asList(PnxConstants.SECONDERY_TITLE,0));
                outputMapping.put("T3", Arrays.asList(PnxConstants.TITLE_SERIES,0));
                outputMapping.put("AU", Arrays.asList(PnxConstants.AUTHOR,1,PnxConstants.CREATOR,0));
                outputMapping.put("A1", Arrays.asList(PnxConstants.CREATOR,0));
                outputMapping.put("A2", Arrays.asList(PnxConstants.SECONDERY_AUTHOR,0));
                outputMapping.put("Y1", Arrays.asList(PnxConstants.CREATION_DATE,0));
                outputMapping.put("N1", Arrays.asList(PnxConstants.NOTES,1,PnxConstants.DESCRIPTION,0));
                outputMapping.put("N2", Arrays.asList(PnxConstants.ABSTRACT,0));
                outputMapping.put("KW", Arrays.asList(PnxConstants.SUBJECT,0));
                outputMapping.put("JF", Arrays.asList(PnxConstants.PERIODICAL_FULL,0));
                outputMapping.put("JA", Arrays.asList(PnxConstants.PERIODICAL_ABBREV,0));
                outputMapping.put("VL", Arrays.asList(PnxConstants.VOLUME,0));
                outputMapping.put("IS", Arrays.asList(PnxConstants.ISSUE,0));
                outputMapping.put("SP", Arrays.asList(PnxConstants.START_PAGE,0));
                outputMapping.put("EP", Arrays.asList(PnxConstants.OTHER_PAGES,0));
                outputMapping.put("PB", Arrays.asList(PnxConstants.PUBLISHER,0));
                outputMapping.put("CY", Arrays.asList(PnxConstants.CITY_OF_PUBLICATION,0));
                outputMapping.put("SN", Arrays.asList(PnxConstants.ISSN,1,PnxConstants.IDENTIFIER,0));            
                outputMapping.put("ET", Arrays.asList(PnxConstants.EDITION,0)); 
                outputMapping.put("OP", Arrays.asList(PnxConstants.IS_PART_OF,0)); 
                outputMapping.put("LA", Arrays.asList(PnxConstants.LANGUAGE,0)); 
                outputMapping.put("DB", Arrays.asList(PnxConstants.SOURCE,0)); 
                outputMapping.put("M3", Arrays.asList(PnxConstants.TYPE,0)); 
            }
                                                    
            if (tagsThatNeedToBeSplit.isEmpty()){
                tagsThatNeedToBeSplit.addAll(Arrays.asList(new String[]{"AU", "A1", "N1", "SN", "PY", "KW"}));
            }
    }
    
    private static String addHeader(String type){        
        return "\n" + addTag("TY", type);        
    }
    
    private static String addFooter(){
        return addTag("ER", "");
    }
    
    private static String addTag(String tag, String data) {
       /*
        String[] tags = {"TY", "ER", "ID", "TI", "T2", "T3", "AU", "A2", "A3", "PY", "Y2", "N1", "N2", "KW", "RP", "AV",
                         "SP", "EP", "JO", "JF", "J1", "J2", "VL", "IS", "CY", "PB", "SN", "AD", "UR", "U1", "U2", "U3", "U4", "U5",
                         "M1", "M2", "M3", "M4", "M5", "LA", "DB", "ET", "OP"};
*/
        //ArrayList<String> availableTags = new ArrayList<String>();
        //availableTags.addAll(Arrays.asList(tags));
        
        
      //  if (availableTags.contains(tag)) {
            return tag.toUpperCase() + "  - " + data;
        //}
       // return "";
    }
    
}
