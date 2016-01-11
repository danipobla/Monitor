package cat.fornons.monitor;

import org.json.JSONException;
import org.json.JSONObject;

public class HRMesurent {
    String data;
    String hr;
    String estat;
    String intensitat;
    String comentari;
    int size;

    public HRMesurent(){
        this.data="";
        this.hr="";
        this.estat="";
        this.intensitat="";
        this.comentari="";
        this.size=0;
    }


    public Object setJSON() {
        JSONObject hrm = new JSONObject();

        try {
            hrm.put("data", data);
            hrm.put("hr", hr);
            hrm.put("estat", estat);
            hrm.put("intensitat", intensitat);
            hrm.put("comentari", comentari);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hrm;
    }

    public void setHRM(String valor, String data) {
        this.size = this.size++;
        this.data=data;
        this.hr=valor;
    }
}
