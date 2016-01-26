package cat.fornons.monitor;

import org.json.JSONException;
import org.json.JSONObject;

public class HRMesurent {
    String date;
    String hr;
    String state;
    String intensity;
    String comment;
    int size;

    public HRMesurent(){
        this.date="";
        this.hr="";
        this.state ="";
        this.intensity ="";
        this.comment ="";
        this.size=0;
    }


    public Object getJSON() {
        JSONObject hrm = new JSONObject();

        try {
            hrm.put("date", date);
            hrm.put("hr", hr);
            hrm.put("state", state);
            hrm.put("intensity", intensity);
            hrm.put("comment", comment);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hrm;
    }

    public void setHRM(String valor, String data) {
        this.size = this.size++;
        this.date =data;
        this.hr=valor;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}
