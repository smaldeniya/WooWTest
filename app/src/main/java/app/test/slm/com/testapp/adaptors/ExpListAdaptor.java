package app.test.slm.com.testapp.adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.icu.text.MessageFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.test.slm.com.testapp.R;
import app.test.slm.com.testapp.utility.Constants;

/**
 * Created by sahanm on 8/2/16.
 */
public class ExpListAdaptor extends BaseExpandableListAdapter implements Filterable{

    private Context context;
    JSONObject data;
    JSONArray groups;

    public ExpListAdaptor(Context context, JSONObject data) throws JSONException {
        this.context = context;
        this.data = data;
        this.groups = data.getJSONArray(Constants.JSON_GROUPS);
    }

    @Override
    public int getGroupCount() {
        return groups.length();
    }

    @Override
    public int getChildrenCount(int i) {
        int childrenCount = 0;
        try {
            JSONObject group = this.groups.getJSONObject(i);
            childrenCount = group.getJSONArray(Constants.JSON_PEOPLE).length();
        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }
        return childrenCount;
    }

    @Override
    public Object getGroup(int i) {
        JSONObject group = null;
        try {
            group = this.groups.getJSONObject(i);
        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }
        return group;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        JSONObject child = null;
        try {
            JSONObject group = this.groups.getJSONObject(groupPosition);
            JSONArray people = group.getJSONArray(Constants.JSON_PEOPLE);
            child = people.getJSONObject(childPosititon);
        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }
        return child;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int groupPosition, int childPosititon) {
        return childPosititon;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            JSONObject group = this.groups.getJSONObject(groupPosition);
            String groupHeader = group.getString(Constants.JSON_GROUP_NAME);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_item_header, null);
            }

            TextView headerLbl = (TextView) convertView.findViewById(R.id.lblListHeader);
            headerLbl.setTypeface(null, Typeface.BOLD);
            headerLbl.setText(groupHeader);

        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Resources res = this.context.getResources();

        try {
            JSONObject group = this.groups.getJSONObject(groupPosition);
            JSONArray people = group.getJSONArray(Constants.JSON_PEOPLE);
            JSONObject child = people.getJSONObject(childPosition);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_item, null);
            }

            ImageView statusIcon = (ImageView) convertView.findViewById(R.id.statusIcon);
            int statusIconId = resolveStatusIcons(child.getString(Constants.JSON_STATUS_ICON));
            statusIcon.setImageDrawable(res.getDrawable(statusIconId));

            TextView name = (TextView) convertView.findViewById(R.id.contactName);
            name.setText(child.getString(Constants.JSON_FIRST_NAME) + " " + child.getString(Constants.JSON_LAST_NAME));

            TextView statusMsg = (TextView) convertView.findViewById(R.id.contactStatusDesc);
            statusMsg.setText(resolveStatusMessage(child.getString(Constants.JSON_STATUS_ICON), child.getString(Constants.JSON_STATUS_MSG)));

            ImageView icon = (ImageView) convertView.findViewById(R.id.profilePicture);
            icon.setImageDrawable(res.getDrawable(R.drawable.contacts_list_avatar_male));

        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private int resolveStatusIcons(String status) {
        int v = 0;
        if(status.compareTo(Constants.JSON_STATUS_ONLINE) == 0) {
            v = R.drawable.contacts_list_status_online;
        } else if(status.compareTo(Constants.JSON_STATUS_OFFLINE) == 0) {
            v = R.drawable.contacts_list_status_offline;
        } else if(status.compareTo(Constants.JSON_STATUS_BUSY) == 0) {
            v = R.drawable.contacts_list_status_busy;
        } else if(status.compareTo(Constants.JSON_STATUS_AWAY) == 0) {
            v = R.drawable.contacts_list_status_away;
        } else {
            v = R.drawable.contacts_list_status_pending;
        }

        return v;
    }

    private String resolveStatusMessage(String status, String defaultMsg) {
        if(defaultMsg.compareTo("") == 0) {
            String msg = "";

            switch (status) {
                case Constants.JSON_STATUS_ONLINE:
                    msg = Constants.JSON_STATUS_MSG_ONLINE;
                    break;

                case Constants.JSON_STATUS_OFFLINE:
                    msg = Constants.JSON_STATUS_MSG_OFFLINE;;
                    break;

                case Constants.JSON_STATUS_BUSY:
                    msg = Constants.JSON_STATUS_MSG_BUSY;
                    break;

                case Constants.JSON_STATUS_AWAY:
                    msg = Constants.JSON_STATUS_MSG_AWAY;
                    break;

                default:
                    msg = Constants.JSON_STATUS_MSG_PENDING;;
                    break;
            }

            return msg;
        } else {
            return defaultMsg;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                JSONArray resultGroups = new JSONArray();

                try {
                    JSONArray groups = data.getJSONArray(Constants.JSON_GROUPS);

                    for (int i = 0; i < groups.length() ; i++) {
                        JSONObject iThGroup = groups.getJSONObject(i);
                        JSONArray iThPeople = iThGroup.getJSONArray(Constants.JSON_PEOPLE);

                        JSONObject iThGroupResult = new JSONObject();
                        JSONArray iThPeopleResult = new JSONArray();
                        iThGroupResult.put(Constants.JSON_GROUP_NAME, iThGroup.getString(Constants.JSON_GROUP_NAME));

                        for (int j = 0; j < iThPeople.length(); j++) {
                            JSONObject person = iThPeople.getJSONObject(j);

                            if(person.getString(Constants.JSON_FIRST_NAME).toLowerCase().contains(charSequence.toString().toLowerCase())
                                    || person.getString(Constants.JSON_LAST_NAME).toLowerCase().contains(charSequence.toString().toLowerCase())) {

                                iThPeopleResult.put(person);
                            }
                        }

                        iThGroupResult.put(Constants.JSON_PEOPLE, iThPeopleResult);
                        resultGroups.put(iThGroupResult);
                    }

                } catch (JSONException e) {
                    Log.e(ExpListAdaptor.class.getName(), e.getMessage());
                }

                filterResults.values = resultGroups;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ExpListAdaptor.this.groups = (JSONArray) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void resetTextSearch() {
        try {
            ExpListAdaptor.this.groups = data.getJSONArray(Constants.JSON_GROUPS);
            notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(ExpListAdaptor.class.getName(), e.getMessage());
        }
    }

}
