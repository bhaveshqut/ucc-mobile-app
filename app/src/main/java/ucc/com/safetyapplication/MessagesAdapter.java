package ucc.com.safetyapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Damian on 22/02/2016.
 */
class MessagesAdapter extends BaseAdapter {
    List<String> messages;
    Context context;

    public MessagesAdapter(Context context, List<String> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int id) {
        return new Object();
    }

    @Override
    public long getItemId(int id) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.message_preview, parent);

        TextView message = (TextView)view.findViewById(R.id.lblMessage);
        message.setText(messages.get(position));

        return view;
    }

}