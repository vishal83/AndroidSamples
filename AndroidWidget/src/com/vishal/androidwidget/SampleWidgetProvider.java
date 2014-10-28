
package com.vishal.androidwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

public class SampleWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.i("VISHAL", "Widget:onUpdate" + Arrays.asList(appWidgetIds));

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
            views.setImageViewResource(R.id.imageView, R.drawable.ic_launcher);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
