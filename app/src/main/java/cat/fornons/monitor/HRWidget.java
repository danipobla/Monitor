package cat.fornons.monitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class HRWidget extends AppWidgetProvider {

    String valor="";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String valor) {

       // CharSequence widgetText = "@";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.hrwidget);
        views.setTextViewText(R.id.appwidget_text,valor);

/*
        Intent ConfigIntent;
        //if (){
            ConfigIntent= new Intent(context,CardiacActivity.class);
        //}else{
            ConfigIntent= new Intent(context,LoginActivity.class);
        //}


        PendingIntent ConfigPendingIntent = PendingIntent.getActivity(context, 0, ConfigIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_text, ConfigPendingIntent);
*/

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,valor);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created



    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        valor =intent.getStringExtra("valor");
        super.onReceive(context, intent);


    }
}

