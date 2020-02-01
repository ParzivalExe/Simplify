package com.bluebird.apps.simplify.notifications

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlin.collections.ArrayList

class Notification(var context: Context, var smallIcon: Int) {

    var messageTitle: String = "ValuesNotSet"
    var messageText: String = "Set a messageTitle and messageText for this notification to get rid of this default-text"
    var channelId: String? = null
    var actions: ArrayList<Action> = arrayListOf()
    var contentIntent: PendingIntent? = null
    var iconTint: Int? = null


    constructor(context: Context, smallIcon: Int, messageTitle: String, messageText: String): this(context, smallIcon) {
        this.messageTitle = messageTitle
        this.messageText =  messageText
    }

    fun toNotificationCompat() : Notification {
        var builder = NotificationCompat.Builder(context).apply {
            setSmallIcon(smallIcon)
            setContentTitle(messageTitle)
            setContentText(messageText)
            if(iconTint != null) {
                color = iconTint!!
            }
        }
        if(channelId != null) {
            builder.setChannelId(channelId!!)
        }
        if(contentIntent != null) {
            builder.setContentIntent(contentIntent)
        }
        for(action in actions) {
            builder.addAction(action.actionIcon, action.actionText, action.getIntent())
        }

        return builder.build()
    }

    fun showNotification(notificationId: Int, manager: NotificationManager) {
        manager.notify(notificationId, toNotificationCompat())
    }




    @Suppress("UNCHECKED_CAST", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    class Action {

        var broadcastReceiver: Class<BroadcastReceiver>? = null
        var activity: Class<Activity>? = null
        var actionId: String
        var actionIcon: Int
        var actionText: String
        var context: Context
        var intentExtras: Bundle? = null


        constructor(actionClass: Class<Object>, actionId: String, actionIcon: Int, actionText: String, context: Context) {
            if(actionClass is Activity) {
                this.activity = actionClass as Class<Activity>
            }else if(actionClass is BroadcastReceiver){
                this.broadcastReceiver = actionClass as Class<BroadcastReceiver>
            }
            this.actionId = actionId
            this.actionIcon = actionIcon
            this.actionText = actionText
            this.context = context
        }

        constructor(actionClass: Class<Object>, actionId: String, actionIcon: Int, actionText: String, context: Context, extras: Bundle) : this(actionClass, actionId, actionIcon, actionText, context) {
            this.intentExtras = extras
        }

        fun getIntent(): PendingIntent? {
            if(activity != null) {
                val intent = Intent(context, activity).apply {
                    action = actionId
                    if(intentExtras != null) {
                        putExtras(intentExtras!!)
                    }
                }
                return PendingIntent.getActivity(context, 0, intent, 0)
            }else if(broadcastReceiver != null) {
                val intent = Intent(context, broadcastReceiver).apply {
                    action = actionId
                    if(intentExtras != null) {
                        putExtras(intentExtras!!)
                    }
                }
                return PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            return null
        }

    }

}