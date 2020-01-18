package com.bluebird.apps.simplify.notifications

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Notification(var context: Context, var smallIcon: Int) {

    var messageTitle: String = "ValuesNotSet"
    var messageText: String = "Set a messageTitle and messageText for this notification to get rid of this default-text"
    var channelId: String? = null
    var actions: ArrayList<Action> = arrayListOf()
    var contentIntent: PendingIntent? = null


    constructor(context: Context, smallIcon: Int, messageTitle: String, messageText: String): this(context, smallIcon) {
        this.messageTitle = messageTitle
        this.messageText =  messageText
    }

    fun toNotificationCompat(notificationId: Int) : Notification {
        var builder = NotificationCompat.Builder(context).apply {
            setSmallIcon(smallIcon)
            setContentTitle(messageTitle)
            setContentText(messageText)
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




    @Suppress("UNCHECKED_CAST")
    class Action {

        var broadcastReceiver: Class<BroadcastReceiver>? = null
        var activity: Class<Activity>? = null
        var actionId: String
        var actionIcon: Int
        var actionText: String
        var context: Context
        var extras: HashMap<String, String> = hashMapOf()


        constructor(actionClass: Class<Any>, actionId: String, actionIcon: Int, actionText: String, context: Context) {
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

        constructor(actionClass: Class<Any>, actionId: String, actionIcon: Int, actionText: String, context: Context, extras: HashMap<String, String>) : this(actionClass, actionId, actionIcon, actionText, context) {
            this.extras = extras
        }

        fun getIntent(): PendingIntent? {
            if(activity != null) {
                val intent = Intent(context, activity).apply {
                    action = actionId
                }
                for(extraKey in extras.keys) {
                    intent.putExtra(extraKey, extras[extraKey])
                }
                return PendingIntent.getActivity(context, 0, intent, 0)
            }else if(broadcastReceiver != null) {
                val intent = Intent(context, broadcastReceiver).apply {
                    action = actionId
                }
                for(extraKey in extras.keys) {
                    intent.putExtra(extraKey, extras[extraKey])
                }
                return PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            return null
        }

    }

}