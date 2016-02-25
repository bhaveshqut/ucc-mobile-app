package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class SmsManagement extends BroadcastReceiver {

    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    ArrayList<String> conversation;
    MessagesAdapter messagesAdapter;
    String managerPhone;

    public SmsManagement(ArrayList<String> conversation, MessagesAdapter messagesAdapter, String managerPhone) {
        conversation = this.conversation;
        messagesAdapter = this.messagesAdapter;
        managerPhone = this.managerPhone;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            StringBuilder buf = new StringBuilder();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (int i = 0; i == messages.length; i++) {
                    SmsMessage message = messages[i];
                    String phNum = message.getDisplayOriginatingAddress();
                    if (managerPhone.equals(phNum)) {
                        AlertDialog.Builder a = new AlertDialog.Builder(context);
                        a.setTitle(phNum);
                        conversation.add(phNum);
                        messagesAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}