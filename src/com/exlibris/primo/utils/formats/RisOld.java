/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exlibris.primo.utils.formats;

import com.exlibris.primo.srvinterface.PnxConstants;
import com.exlibris.primo.srvinterface.RecordDocDTO;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mehmetc
 */
public class RisOld {

    private RisOld() {
    }

    public static synchronized String fromRecordDocDTO(RecordDocDTO record) {
        try {
            int i = 0;
            int j = 0;

            StringWriter stringWriter = new StringWriter();
            PrintWriter out = new PrintWriter(stringWriter);

            //Type of reference MANDATORY
            String[] _type = (String[]) record.getValuesNoHL().get(PnxConstants.TYPE);
            String[] _ris_type = (String[]) record.getValuesNoHL().get(PnxConstants.RIS_TYPE);
            if (_ris_type != null) {
                out.println("TY - " + _ris_type[0]);
            } else {
                if (_type != null) {
                    out.println("TY - " + _type[0]);
                }
            }

            //Author Secondary
            String[] _con = (String[]) record.getValuesNoHL().get(PnxConstants.CONTRIBUTOR);
            if (_con != null) {
                for (i = 0; i < _con.length; i++) {
                    _con[i] = _con[i].replaceAll("<span class=\"searchword\">", "");
                    _con[i] = _con[i].replaceAll("</span>", "");
                    String[] _contributor = _con[i].split("; ");
                    for (j = 0; j < _contributor.length; j++) {
                        out.println("A2 - " + _contributor[j]);
                    }
                }
            }

            //Author Primary
            String[] _au = (String[]) record.getValuesNoHL().get(PnxConstants.AUTHOR);
            if (_au != null) {
                for (i = 0; i < _au.length; i++) {
                    _au[i] = _au[i].replaceAll("<span class=\"searchword\">", "");
                    _au[i] = _au[i].replaceAll("</span>", "");
                    String[] _author = _au[i].split("; ");
                    for (j = 0; j < _author.length; j++) {
                        out.println("A1 - " + _author[j]);

                    }
                }
            }

            //Author Primary
            String[] _cre = (String[]) record.getValuesNoHL().get(PnxConstants.CREATOR);
            if (_cre != null && _au == null) {
                for (i = 0; i < _cre.length; i++) {
                    _cre[i] = _cre[i].replaceAll("<span class=\"searchword\">", "");
                    _cre[i] = _cre[i].replaceAll("</span>", "");
                    String[] _creator = _cre[i].split("; ");
                    for (j = 0; j < _creator.length; j++) {
                        out.println("A1 - " + _creator[j]);
                    }
                }
            }

            //Title Primary
            String[] _p_title = (String[]) record.getValuesNoHL().get(PnxConstants.PRIMARY_TITLE);
            if (_p_title != null) {
                out.println("T1 - " + _p_title[0]);
            } else {
                String[] _title = (String[]) record.getValuesNoHL().get(PnxConstants.TITLE);
                if (_title != null) {
                    out.println("TI - " + _title[0]);
                }
            }

            String[] _s_title = (String[]) record.getValuesNoHL().get(PnxConstants.SECONDERY_TITLE);
            if (_s_title != null) {
                out.println("T2 - " + _s_title[0]);
            }


            String[] _date = (String[]) record.getValuesNoHL().get(PnxConstants.CREATION_DATE);
            if (_date != null) {
                out.println("PY - " + _date[0]);
            }

            String[] _subj = (String[]) record.getValuesNoHL().get(PnxConstants.SUBJECT);
            if (_subj != null) {
                String[] _subjects = _subj[0].split("; ");
                for (i = 0; i < _subjects.length; i++) {
                    out.println("KW - " + _subjects[i]);
                }
            }

            String[] _ab = (String[]) record.getValuesNoHL().get(PnxConstants.ABSTRACT);
            if (_ab != null) {
                String[] _abstract = _ab[0].split("; ");
                for (i = 0; i < _abstract.length; i++) {
                    out.println("AB - " + _abstract[i]);
                }
            }

            String[] _desc = (String[]) record.getValuesNoHL().get(PnxConstants.DESCRIPTION);
            if (_desc != null) {
                out.println("N1 - " + _desc[0]);
            }


            String[] _pub = (String[]) record.getValuesNoHL().get(PnxConstants.PUBLISHER);
            if (_pub != null) {
                out.println("PB - " + _pub[0]);
            }


            String[] _issue = (String[]) record.getValuesNoHL().get(PnxConstants.ISSUE);
            if (_issue != null) {
                out.println("IS - " + _issue[0]);
            }

            String[] _volume = (String[]) record.getValuesNoHL().get(PnxConstants.VOLUME);
            if (_volume != null) {
                out.println("VL - " + _volume[0]);
            }

            String[] _id = (String[]) record.getValuesNoHL().get(PnxConstants.ID);
            if (_id != null) {
                out.println("ID - " + _id[0]);
            }

            String[] _issn = (String[]) record.getValuesNoHL().get(PnxConstants.ISSN);
            if (_issn != null) {
                out.println("SN - " + _issn[0]);
            }

            String[] _isbn = (String[]) record.getValuesNoHL().get(PnxConstants.ISBN);
            if (_isbn != null) {
                out.println("SN - " + _isbn[0]);
            }

            String[] _cob = (String[]) record.getValuesNoHL().get(PnxConstants.CITY_OF_PUBLICATION);
            if (_cob != null) {
                out.println("SN - " + _cob[0]);
            }

            String[] _nt = (String[]) record.getValuesNoHL().get(PnxConstants.NOTES);
            if (_nt != null) {
                String[] _notes = _nt[0].split("; ");
                for (i = 0; i < _notes.length; i++) {
                    out.println("N1 - " + _notes[i]);
                }
            }

            //mandatory!!
            out.println("ER - ");
            out.println("");
            out.flush();
            out.close();
            return stringWriter.toString();

        } catch (Exception ex) {
            Logger.getLogger(RisOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
