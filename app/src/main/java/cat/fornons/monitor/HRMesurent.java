package cat.fornons.monitor;

public class HRMesurent {
    String data;
    String hr;
    String estat;
    String intensitat;
    String comentari;
    public HRMesurent(){
        this.data="";
        this.hr="";
        this.estat="";
        this.intensitat="";
        this.comentari="";
    }

    public void setHRM(String data, String hr, String estat, String intensitat, String comentari){
        this.data=data;
        this.hr=hr;
        this.estat=estat;
        this.intensitat=intensitat;
        this.comentari=comentari;
    }

}
