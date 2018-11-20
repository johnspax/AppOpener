package opener.app.spaxsoftware.com.appopener.Adopter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import opener.app.spaxsoftware.com.appopener.Model.PackageNames;
import opener.app.spaxsoftware.com.appopener.R;

public class ListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<PackageNames> packageNamesList = null;
    private ArrayList<PackageNames> arraylist;

    public ListViewAdapter(Context context, List<PackageNames> packageNamesList) {
        mContext = context;
        this.packageNamesList = packageNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<PackageNames>();
        this.arraylist.addAll(packageNamesList);
    }

    public class ViewHolder {
        TextView packageLabel;
        TextView packageName;
    }

    @Override
    public int getCount() {
        return packageNamesList.size();
    }

    @Override
    public PackageNames getItem(int position) {
        return packageNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.packageName = (TextView) view.findViewById(R.id.packageName);
            holder.packageLabel = (TextView) view.findViewById(R.id.packageLabel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.packageName.setText(packageNamesList.get(position).getPackageName());
        holder.packageLabel.setText(packageNamesList.get(position).getApplicationName() + " : ");
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        packageNamesList.clear();
        if (charText.length() == 0) {
            packageNamesList.addAll(arraylist);
        } else {
            for (PackageNames pn : arraylist) {
                if (pn.getPackageName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    packageNamesList.add(pn);
                }
            }
        }
        notifyDataSetChanged();
    }
}
