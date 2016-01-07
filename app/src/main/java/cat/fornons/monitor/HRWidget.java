package cat.fornons.monitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.
 */
public class HRWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        //Obtenim les preferencies
        SharedPreferences sharedPref = context.getSharedPreferences("preferencies", Context.MODE_PRIVATE);
        String widgetText = sharedPref.getString("hr","--");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hrwidget);
        views.setTextViewText(R.id.tvWidget, widgetText);


        /*Intent configIntent = new Intent(context, CardiacActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tvWidget, configPendingIntent);*/



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them




        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //Obtenim les preferencies
            SharedPreferences sharedPref = context.getSharedPreferences("preferencies", Context.MODE_PRIVATE);
            String widgetText = sharedPref.getString("hr","--");

            // Construct the RemoteViews object

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hrwidget);
            views.setTextViewText(R.id.tvWidget, widgetText);

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

}

