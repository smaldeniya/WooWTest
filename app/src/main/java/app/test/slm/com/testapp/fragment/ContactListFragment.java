package app.test.slm.com.testapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import app.test.slm.com.testapp.R;
import app.test.slm.com.testapp.adaptors.ExpListAdaptor;
import app.test.slm.com.testapp.utility.Constants;

/**
 * Created by sahanm on 8/1/16.
 */
public class ContactListFragment extends Fragment {

    private Context context;
    ExpListAdaptor listAdapter;
    private EditText searchItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.contactLIstView);
        try {
            String contacts = loadJSONFromAsset(Constants.CONTACT_FILE);
            JSONObject obj = new JSONObject(contacts);
            listAdapter = new ExpListAdaptor(context, obj);
            listView.setAdapter(listAdapter);
        } catch (JSONException e) {
            Log.e(ContactListFragment.class.getName(), e.getMessage());
        }

        searchItem = (EditText) view.findViewById(R.id.inputSearch);
        searchItem.addTextChangedListener(textWatcher);

        return view;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length() > 0) {
                ContactListFragment.this.listAdapter.getFilter().filter(charSequence);
            } else {
                ContactListFragment.this.listAdapter.resetTextSearch();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
