package com.exlibris.primo.utils;

import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exlibris.primo.interfaces.PushToInterface;
import com.exlibris.primo.srvinterface.RecordDocDTO;
import com.exlibris.primo.srvinterface.PnxConstants;
import com.exlibris.primo.xsd.commonData.PrimoResult;
import java.text.SimpleDateFormat;
import java.util.Date;

/* This plug-in enables you to export records in .ris format to EndNote Local from PRIMO
   Original code by Alessandro Fasoli (alessandro dot fasoli at exlibrisgroup dot com)
         http://www.exlibrisgroup.org/display/PrimoCC/Plug-in+for+Citavi%2C+Endnote+and+Zotero+%28RIS+Export%29
   updated to export multiple records by Mehmet Celik (mehmet dot celik at libis dot be)

   License: BSD style
   Use, modification and distribution of the code are permitted provided the copyright notice, list of conditions and disclaimer appear in all related material.

   LIBRARIES:
         ~/p3_1/ng/primo/home/system/thirdparty/openserver/server/search/lib/jaguar-client.jar
         ~/p3_1/ng/primo/home/system/search/client/primo_library-common.jar
         ~/p3_1/ng/primo/home/system/search/client/primo_common-infrastructure.jar
         ~/p3_1/ng/primo/home/system/thirdparty/openserver/server/search/lib/xbean.jar
         ~/p3_1/ng/primo/home/system/thirdparty/openserver/server/search/lib/javax.servlet.jar
         primo-utils.jar

   How to make primo-utils.jar
               ---------------
   Log on to your FrontEnd server
    $ fe_web
    $ cd WEB-INF
    $ cd classes
    $ jar zcvf /tmp/primo-utils.jar ./*

    INSTALL
    -------
    - Copy EndNoteLocalProcess.class to the FrontEnd server and place file in
      ~/p3_1/ng/primo/home/system/thirdparty/openserver/server/search/deploy/primo_library-app.ear/primo_library-libweb.war/WEB-INF/classes/com/exlibris/primo/utils

    - Go to the Back Office, Primo Home>Advanced configuration>All mapping table and select SubSystem: Adaptors, TableName:Pushto Adaptors

    - Add a new row related to the EndNoteLocalProcess class:
      Adaptor Identifier: EndNoteLocal
      Key: Class
      Value: com.exlibris.primo.utils.EndNoteLocalProcess

    - Deploy the Mapping Tables

    - Go to Back Office, Primo Home>Advanced configuration>All code Table and choose SubSystem: Front End, TableName: Keeping this item Tile.
      For each language relevant to you, add a new Code Table row:
      Code: default.fulldisplay.command.pushto.option.EndNoteLocal
      Description : EndNote Local
      Language: <your_code>
      Display order: highest number + 1

    - Deploy "All Code Tables"

    - Restart FrontEnd service
 */

public class EndNoteLocalProcess implements PushToInterface {
    
    public String pushTo(HttpServletRequest request, HttpServletResponse response, PrimoResult[] record, boolean fromBasket) throws Exception {

        int i;
        int j;

        if (request.getParameter("encode") == null) {

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<script language=\"JavaScript\">");
            out.println("<!--");
            out.println("{window.resizeTo(330,250)}");
            out.println("-->");
            out.println("</script>");
            out.println("<head>");
            out.println("<style>");
            out.println("body {background-color: #ffffff; color: #32322f;margin: 0px; padding: 0px; font-family: 'Arial Unicode MS',Arial,verdana,serif;font-size: 100%;}");
            out.println("h2 {float: left;width:100%; padding: 0.3em 0; color:#606f7f;}");
            out.println("th {color: #8c8d8c; font-weight: bold; font-size: 120%; white-space: nowrap;}");
            out.println("select {color: black; font-size: 90%; white-space: nowrap;font-family: 'Arial Unicode MS',Arial,verdana,serif;}");
            out.println("input {color: black; font-size: 90%; white-space: nowrap;font-family: 'Arial Unicode MS',Arial,verdana,serif;}");
            out.println("</style>");
            out.println("<title>Select your encoding charset</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Import to EndNote Local</h2>");
            out.println("<br/><br/>");
            out.println("<form method=\"POST\" action=\"#\">");
            out.println("<table cellspacing=\"10\" style=\"float:left;\"> ");
            out.println("<tr><th>Encoding : </th>");
            out.println("<td height=\"30\"> ");
            out.println("<select name=\"encode\">");
            out.println("<option value=\"UTF-8\">UTF-8</option>");
            out.println("<option value=\"ISO-8859-1\">ISO-8859-1</option>");
            out.println("<option value=\"ASCII\">ASCII</option>");
            out.println("<option value=\"WINDOWS-1251\">WINDOWS-1251</option>");
            out.println("</select>");
            out.println("</td></tr><tr>");
            out.println("<th></th><td>");
            out.println("<input type=\"submit\" value=\"Save\"></td></tr>");
            out.println("</table><br>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");

        } else {
            PrintWriter out = response.getWriter();
            String encode = request.getParameter("encode");
            Date now = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyMMddhhmmss");
            
            request.setCharacterEncoding(encode);

            response.setCharacterEncoding(encode);
            response.setContentType("mimetype: application/octet-stream");
            response.setContentType("text/text; charset=" + encode);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + df.format(now) + ".ris\"");

            for(int k=0; k< record.length; k++){
                RecordDocDTO searchResultFullDoc = new RecordDocDTO(request, record[k], 0);
                //String[] _doc = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.CONTROL_RECORDID);

                //Type of reference MANDATORY
                String[] _type = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.TYPE);
                String[] _ris_type = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.RIS_TYPE);
                if (_ris_type != null) {
                    out.println("TY  - " + _ris_type[0]);
                } else {
                    if (_type != null) {
                        out.println("TY  - " + _type[0]);
                    }
                }

                //Author Secondary
                String[] _con = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.CONTRIBUTOR);
                if (_con != null) {
                    for (i = 0; i < _con.length; i++) {
                        _con[i] = _con[i].replaceAll("<span class=\"searchword\">", "");
                        _con[i] = _con[i].replaceAll("</span>", "");
                        String[] _contributor = _con[i].split("; ");
                        for (j = 0; j < _contributor.length; j++) {
                            out.println("A2  - " + _contributor[j]);
                        }
                    }
                }

                //Author Primary
                String[] _au = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.AUTHOR);
                if (_au != null) {
                    for (i = 0; i < _au.length; i++) {
                        _au[i] = _au[i].replaceAll("<span class=\"searchword\">", "");
                        _au[i] = _au[i].replaceAll("</span>", "");
                        String[] _author = _au[i].split("; ");
                        for (j = 0; j < _author.length; j++) {
                            out.println("A1  - " + _author[j]);

                        }
                    }
                }

                //Author Primary
                String[] _cre = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.CREATOR);
                if (_cre != null && _au == null) {
                    for (i = 0; i < _cre.length; i++) {
                        _cre[i] = _cre[i].replaceAll("<span class=\"searchword\">", "");
                        _cre[i] = _cre[i].replaceAll("</span>", "");
                        String[] _creator = _cre[i].split("; ");
                        for (j = 0; j < _creator.length; j++) {
                            out.println("A1  - " + _creator[j]);
                        }
                    }
                }

                //Title Primary
                String[] _p_title = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.PRIMARY_TITLE);
                if (_p_title != null) {
                    out.println("T1  - " + _p_title[0]);
                } else {
                    String[] _title = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.TITLE);
                    if (_title != null) {
                        out.println("TI  - " + _title[0]);
                    }
                }

                String[] _s_title = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.SECONDERY_TITLE);
                if (_s_title != null) {
                    out.println("T2  - " + _s_title[0]);
                }


                String[] _date = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.CREATION_DATE);
                if (_date != null) {
                    out.println("PY  - " + _date[0]);
                }

                String[] _subj = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.SUBJECT);
                if (_subj != null) {
                    String[] _subjects = _subj[0].split("; ");
                    for (i = 0; i < _subjects.length; i++) {
                        out.println("KW  - " + _subjects[i]);
                    }
                }

                String[] _ab = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.ABSTRACT);
                if (_ab != null) {
                    String[] _abstract = _ab[0].split("; ");
                    for (i = 0; i < _abstract.length; i++) {
                        out.println("AB  - " + _abstract[i]);
                    }
                }

                String[] _desc = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.DESCRIPTION);
                if (_desc != null) {
                    out.println("N1  - " + _desc[0]);
                }


                String[] _pub = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.PUBLISHER);
                if (_pub != null) {
                    out.println("PB  - " + _pub[0]);
                }


                String[] _issue = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.ISSUE);
                if (_issue != null) {
                    out.println("IS  - " + _issue[0]);
                }

                String[] _volume = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.VOLUME);
                if (_volume != null) {
                    out.println("VL  - " + _volume[0]);
                }

                String[] _id = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.ID);
                if (_id != null) {
                    out.println("ID  - " + _id[0]);
                }

                String[] _issn = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.ISSN);
                if (_issn != null) {
                    out.println("SN  - " + _issn[0]);
                }

                String[] _isbn = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.ISBN);
                if (_isbn != null) {
                    out.println("SN  - " + _isbn[0]);
                }

                String[] _cob = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.CITY_OF_PUBLICATION);
                if (_cob != null) {
                    out.println("SN  - " + _cob[0]);
                }

                String[] _nt = (String[]) searchResultFullDoc.getValuesNoHL().get(PnxConstants.NOTES);
                if (_nt != null) {
                    String[] _notes = _nt[0].split("; ");
                    for (i = 0; i < _notes.length; i++) {
                        out.println("N1  - " + _notes[i]);
                    }
                }

                //mandatory!!
                out.println("ER  - ");
                out.println("");

            } //for

            out.close();
        }

        return null;
    }

    public String getContent(HttpServletRequest request, boolean fromBasket) {
        return null;
    }

    public String getFormAction() {
        return null;
    }
}
