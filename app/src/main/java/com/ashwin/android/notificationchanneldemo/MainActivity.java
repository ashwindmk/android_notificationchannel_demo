package com.ashwin.android.notificationchanneldemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText groupIdEditText;
    private EditText channelIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupIdEditText = (EditText) findViewById(R.id.groupid_edittext);
        channelIdEditText = (EditText) findViewById(R.id.channelid_edittext);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationGroup(NotificationManager notificationManager, String groupId) {
        NotificationChannelGroup group = new NotificationChannelGroup(groupId, groupId);
        notificationManager.createNotificationChannelGroup(group);
    }

    public void check(View view) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // All notifications are disabled check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.areNotificationsEnabled()) {
                Toast.makeText(getBaseContext(), "Notifications are disabled!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Group is disabled check
            String groupId = groupIdEditText.getText().toString();
            if (!groupId.isEmpty()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    NotificationChannelGroup group = notificationManager.getNotificationChannelGroup(groupId);
                    if (group != null) {
                        if (group.isBlocked()) {
                            Toast.makeText(getBaseContext(), "Notifications group is disabled!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Group '" + groupId + "' does not exist", Toast.LENGTH_LONG).show();
                    }
                }
            }

            // Channel is disabled check
            String channelId = channelIdEditText.getText().toString();
            if (!channelId.isEmpty()) {
                NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
                if (channel != null) {
                    Log.d("channel-test", "Channel " + channelId + " importance: " + channel.getImportance());
                    if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                        Toast.makeText(getBaseContext(), "Notification channel '" + channelId + "' is disabled!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Channel group is disabled check
                    String channelGroup = channel.getGroup();
                    if (channelGroup != null && !channelGroup.isEmpty()) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            NotificationChannelGroup group = notificationManager.getNotificationChannelGroup(channelGroup);
                            if (group.isBlocked()) {
                                Toast.makeText(getBaseContext(), "Notification group for channel '" + channelId + "' is disabled!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Channel '" + channelId + "' does not exist", Toast.LENGTH_LONG).show();
                }
            }
        }

        // Notifications are enabled
        Toast.makeText(getBaseContext(), "Notifications are enabled!", Toast.LENGTH_LONG).show();
    }

    public void notify(View view) {
        String channelId = channelIdEditText.getText().toString();
        if (channelId.isEmpty()) {
            Toast.makeText(getBaseContext(), "Channel ID cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Test Notification")
                .setContentText(channelId);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Testing channel: " + channelId);

                // Set group
                String groupId = groupIdEditText.getText().toString();
                if (!groupId.isEmpty()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        // Create group if does not exist
                        NotificationChannelGroup group = notificationManager.getNotificationChannelGroup(groupId);
                        if (group == null) {
                            createNotificationGroup(notificationManager, groupId);
                        }
                    } else {
                        createNotificationGroup(notificationManager, groupId);
                    }

                    channel.setGroup(groupId);
                }

                // Set light color
                channel.enableLights(true);
                channel.setLightColor(Color.GREEN);

                channel.setShowBadge(true);

                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            } else {
                Toast.makeText(getBaseContext(), "Channel " + channelId + " already exists", Toast.LENGTH_LONG).show();
            }

            builder.setChannelId(channelId);
        }

        notificationManager.notify(channelId.hashCode(), builder.build());
    }

    public void change(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = channelIdEditText.getText().toString();
            if (channelId.isEmpty()) {
                Toast.makeText(getBaseContext(), "Channel ID cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }

            String groupId = groupIdEditText.getText().toString();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Testing channel: " + channelId);

            // Set group for channel
            // Group of a channel can be changed
            if (!groupId.isEmpty()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    // Create group if does not exist
                    NotificationChannelGroup group = notificationManager.getNotificationChannelGroup(groupId);
                    if (group == null) {
                        Toast.makeText(getBaseContext(), "Group does not exist, creating new group...", Toast.LENGTH_LONG).show();
                        createNotificationGroup(notificationManager, groupId);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Creating group...", Toast.LENGTH_LONG).show();
                    createNotificationGroup(notificationManager, groupId);
                }

                channel.setGroup(groupId);
            }

            // The following properties will not change!
            // Only users can change it through settings
            if (channel.getLightColor() == Color.GREEN) {
                channel.setLightColor(Color.RED);
            } else {
                channel.setLightColor(Color.GREEN);
            }
            channel.setShowBadge(!channel.canShowBadge());
            channel.enableVibration(!channel.shouldVibrate());

            notificationManager.createNotificationChannel(channel);
        }
    }

    public void delete(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = channelIdEditText.getText().toString();
            if (!channelId.isEmpty()) {
                notificationManager.deleteNotificationChannel(channelId);
                Toast.makeText(getBaseContext(), "Channel '" + channelId + "' deleted!", Toast.LENGTH_LONG).show();
            }

            String groupId = groupIdEditText.getText().toString();
            if (!groupId.isEmpty()) {
                notificationManager.deleteNotificationChannelGroup(groupId);
                Toast.makeText(getBaseContext(), "Group '" + groupId + "' deleted!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
