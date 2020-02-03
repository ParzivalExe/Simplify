package com.bluebird.api.simplify.notifications

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlin.collections.ArrayList
import kotlin.math.acos

@Suppress("unused")
class Notification(var context: Context, var smallIcon: Int, var channelId: String) {

    var messageTitle: String = "ValuesNotSet"
    var messageText: String = "Set a messageTitle and messageText for this notification to get rid of this default-text"
    var actions: ArrayList<Action> = arrayListOf()
    var contentIntent: PendingIntent? = null
    var iconTint: Int? = null
    var group: NotificationGroup? = null

    constructor(context: Context, smallIcon: Int, messageTitle: String, messageText: String, channelId: String): this(context, smallIcon, channelId) {
        this.messageTitle = messageTitle
        this.messageText =  messageText
        this.channelId = channelId
    }

    private fun toNotificationCompat() : Notification {
        var builder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(smallIcon)
            setContentTitle(messageTitle)
            setContentText(messageText)
            if(iconTint != null) {
                color = iconTint!!
            }
        }
        if(group != null) {
            builder.setGroup(group!!.groupId.toString())
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
        if(group != null) {
            group!!.showGroupSummary(manager, this)
        }
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
        var intentExtras: Intent? = null


        constructor(actionClass: Class<Object>, actionId: String, actionIcon: Int, actionText: String, context: Context) {
            if(actionClass.superclass != null) {
                if (actionClass.superclass!!.name == NotificationActivity::class.java.name) {
                    this.activity = actionClass as Class<Activity>
                } else if (actionClass.superclass!!.name == NotificationBroadcastReceiver::class.java.name) {
                    this.broadcastReceiver = actionClass as Class<BroadcastReceiver>
                }
            }
            this.actionId = actionId
            this.actionIcon = actionIcon
            this.actionText = actionText
            this.context = context
        }

        constructor(actionClass: Class<Object>, actionId: String, actionIcon: Int, actionText: String, context: Context, intent: Intent) : this(actionClass, actionId, actionIcon, actionText, context) {
            this.intentExtras = intent
        }

        fun getIntent(): PendingIntent? {
            if(activity != null) {
                val intent = Intent(context, activity).apply {
                    action = actionId
                    intentExtras?.let { putExtras(it) }
                }
                return PendingIntent.getActivity(context, 0, intent, 0)
            }else if(broadcastReceiver != null) {
                val intent = Intent(context, broadcastReceiver).apply {
                    action = actionId
                    intentExtras?.let { putExtras(it) }
                }
                return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            return null
        }


        abstract class NotificationBroadcastReceiver(var action: String) : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(context != null && intent != null) {
                    if (intent.action == action) {
                        actionReceived(context, intent)
                    }
                }
            }
            abstract fun actionReceived(context: Context, intent: Intent)
        }

        abstract class NotificationActivity(var action: String) : AppCompatActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                if(intent != null && intent.action == action) {
                    actionCreate(savedInstanceState, intent)
                }
            }
            abstract fun actionCreate(savedInstanceState: Bundle?, intent: Intent)
        }

    }

}