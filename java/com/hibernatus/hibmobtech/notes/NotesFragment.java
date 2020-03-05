package com.hibernatus.hibmobtech.notes;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.site.SiteUtils;

import java.util.List;

public class NotesFragment extends Fragment {
    public static final String TAG = NotesFragment.class.getSimpleName();
    protected List<String> fullContactList;
    protected Long siteId;
    protected Site site;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        site = SiteCurrent.getInstance().getSiteCurrent();
        if(site != null) siteId = site.getId();
        String fragmentId = getArguments().getString("fragmentType");
        Log.i(TAG, "onCreateView: fragmentType=" + fragmentId + " comments =" + site.getComments());

        fullContactList = SiteUtils.formatContactList(site);

        View v = inflater.inflate(R.layout.site_note, container, false);
        TextView commentTextView = (TextView)v.findViewById(R.id.siteNoteCommentTextView);
        commentTextView.setText(site.getComments());
        return v;

    }
}