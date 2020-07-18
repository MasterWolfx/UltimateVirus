package techwolfx.ultimatevirus.utils;

import techwolfx.ultimatevirus.Ultimatevirus;

public class PlaceholderUtils {

    private static Ultimatevirus pl = Ultimatevirus.getInstance();

    /* Placeholders returns */
    public static String isInfectedReturnMsg(String pName){
        return pl.getRDatabase().isInfected(pName) ?
                pl.getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenTrue").replace("&", "§")
                :
                pl.getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenFalse").replace("&", "§");
    }

    public static String infectedTitleReturnMsg(String pName){
        return pl.getRDatabase().isInfected(pName) ?
                pl.getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenTrue").replace("&", "§")
                :
                pl.getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenFalse").replace("&", "§");
    }

    public static int getTotalInfected(){
        return pl.getRDatabase().getInfectedNumber();
    }

}
