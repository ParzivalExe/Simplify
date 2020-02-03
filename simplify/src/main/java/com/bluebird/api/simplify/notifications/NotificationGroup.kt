package com.bluebird.api.simplify.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

class NotificationGroup(var groupId: Int, var groupTitle: String) {

    var contentIntent: PendingIntent? = null
    var notifications: ArrayList<Notification> = arrayListOf()


    fun showGroupSummary(manager: NotificationManager, lastNotification: Notification) {

        val groupSummary = NotificationCompat.Builder(lastNotification.context, lastNotification.channelId)
                .setGroup(groupId.toString())
                .setGroupSummary(true)
                .setSmallIcon(lastNotification.smallIcon)
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine(lastNotification.messageText)
                        .addLine("...")
                        .setBigContentTitle(groupTitle))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
        if(lastNotification.iconTint != null) {
            groupSummary.color = lastNotification.iconTint!!
        }
        if(contentIntent != null) {
            groupSummary.setContentIntent(contentIntent)
        }
        manager.notify(groupId, groupSummary.build())
    }

}