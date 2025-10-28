package com.example.gamebugs.ui.components

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.gamebugs.R
import com.example.gamebugs.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit
import com.example.gamebugs.network.service.CbApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory


class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.example.gamebugs.ACTION_REFRESH"
        const val PREFS_NAME = "GoldWidgetPrefs"
        const val KEY_GOLD_PRICE = "gold_price"
        const val KEY_LAST_UPDATE = "last_update"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_REFRESH -> {
                refreshGoldPrice(context)
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, NewAppWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    override fun onEnabled(context: Context) {
        refreshGoldPrice(context)
    }

    override fun onDisabled(context: Context) {}

    private fun refreshGoldPrice(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Прямой запрос к API из виджета
                val newPrice = fetchGoldPriceFromApi()
                updateAllWidgets(context, newPrice)

            } catch (e: Exception) {
                e.printStackTrace()
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val savedPrice = prefs.getFloat(KEY_GOLD_PRICE, 7500.0f)
                updateAllWidgets(context, savedPrice.toDouble())
            }
        }
    }

    private suspend fun fetchGoldPriceFromApi(): Double {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        val apiService = retrofit.create(CbApiService::class.java)
        val currDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val response = apiService.getGoldPrices(currDate, currDate)
        val goldRecord = response.records.find { it.isGold() }

        return goldRecord?.toDouble() ?: 7500.0
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val prefs = context.getSharedPreferences(NewAppWidget.PREFS_NAME, Context.MODE_PRIVATE)
    val goldPrice = prefs.getFloat(NewAppWidget.KEY_GOLD_PRICE, 7500.0f)
    val lastUpdate = prefs.getString(NewAppWidget.KEY_LAST_UPDATE, "") ?: ""

    updateWidgetViews(context, appWidgetManager, appWidgetId, goldPrice.toDouble(), lastUpdate)
}

internal fun updateAllWidgets(
    context: Context,
    price: Double
) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, NewAppWidget::class.java)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    val prefs = context.getSharedPreferences(NewAppWidget.PREFS_NAME, Context.MODE_PRIVATE)

    val lastUpdate = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date())

    prefs.edit {
        putFloat(NewAppWidget.KEY_GOLD_PRICE, price.toFloat())
        putString(NewAppWidget.KEY_LAST_UPDATE, lastUpdate)
    }

    for (appWidgetId in appWidgetIds) {
        updateWidgetViews(context, appWidgetManager, appWidgetId, price, lastUpdate)
    }
}

private fun updateWidgetViews(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    price: Double,
    lastUpdate: String
) {
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)

    // Форматируем цену
    val formattedPrice = String.format(Locale.getDefault(), "%.2f руб/г", price)
    views.setTextViewText(R.id.gold_price_text, formattedPrice)

    if (lastUpdate.isNotEmpty()) {
        views.setTextViewText(R.id.refresh_text, "Обнов: $lastUpdate")
    }

    val refreshIntent = Intent(context, NewAppWidget::class.java).apply {
        action = NewAppWidget.ACTION_REFRESH
    }
    val refreshPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        refreshIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.refresh_text, refreshPendingIntent)

    val appIntent = Intent(context, MainActivity::class.java)
    val appPendingIntent = PendingIntent.getActivity(
        context,
        0,
        appIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.gold_price_text, appPendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}