package cat.fornons.monitor;

import org.json.JSONException;
import org.json.JSONObject;

public class HRMesurent {
    String date;
    String hr;
    String state;
    String intensity;
    String comment;
    int temp,num;

    public HRMesurent(){
        this.date="";
        this.hr="";
        this.state ="";
        this.intensity ="";
        this.comment ="";
        this.temp=0;
        this.num=0;
    }


    public JSONObject getJSON() {
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
        this.date =data;
        this.hr=valor;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public void setTemp(Integer temp) {
        this.temp=this.temp+temp;
        this.num++;
    }

    public int getTemp() {
        Integer valor = this.temp/this.num;
        this.temp=0;
        this.num=0;
        return valor;
    }

    public void setComment(String comment) {
        this.comment=comment;
    }

}
