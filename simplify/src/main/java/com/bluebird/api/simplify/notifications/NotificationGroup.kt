package com.bluebird.api.simplify.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

@Suppress("unused")
class NotificationGroup(var groupId: Int, var groupTitle: String, var manager: NotificationManager) {

    var contentIntent: PendingIntent? = null
    var notifications: ArrayList<Notification> = arrayListOf()

    fun addNotificationToGroup(notification: Notification) {
        notifications.add(notification)
        showGroupSummary(manager)
    }

    fun removeNotificationFromGroup(notificationId: Int) {
        notifications.forEach { notification -> if(notification.notificationId == notificationId) notifications.remove(notification) }
        if(notifications.size <= 0) {
            manager.cancel(groupId)
        }
    }

    private fun showGroupSummary(manager: NotificationManager) {

        val groupSummary = NotificationCompat.Builder(notifications[0].context, notifications[0].channelId)
                .setGroup(groupId.toString())
                .setGroupSummary(true)
                .setSmallIcon(notifications[0].smallIcon)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)

        val style = NotificationCompat.InboxStyle()
        style.setBigContentTitle(groupTitle)
        notifications.forEach { notification -> style.addLine(notification.messageText) }
        groupSummary.setStyle(style)

        if(notifications[0].iconTint != null) {
            groupSummary.color = notifications[0].iconTint!!
        }
        if(contentIntent != null) {
            groupSummary.setContentIntent(contentIntent)
        }
        manager.notify(groupId, groupSummary.build())
    }

}