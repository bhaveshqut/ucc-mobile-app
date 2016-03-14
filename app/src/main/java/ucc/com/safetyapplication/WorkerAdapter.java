package ucc.com.safetyapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Damian on 9/03/2016.
 */
public class WorkerAdapter extends BaseAdapter {
    List<Worker> workers;
    Context context;
    List<String> chosenWorkers;

    public WorkerAdapter(Context context, List<Worker> workers, boolean positionsNeeded) {
        this.workers = workers;
        this.context = context;

        if (positionsNeeded) {
            chosenWorkers = new ArrayList<>();
        }
    }

    public List<String> getChosenPositions() {
        return chosenWorkers;
    }

    @Override
    public int getCount() {
        return workers.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int listPosition = position;
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.worker_item, parent, false);
        }

        final TextView employeeId = (TextView)view.findViewById(R.id.workerId);
        employeeId.setText(workers.get(position).getEmployeeId());

        TextView name = (TextView)view.findViewById(R.id.workerName);
        name.setText(workers.get(position).getName());

        TextView phNumber = (TextView)view.findViewById(R.id.workerPhoneNumber);
        phNumber.setText(workers.get(position).getPhoneNumber());

        return view;
    }

}