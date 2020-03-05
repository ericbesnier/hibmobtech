package com.hibernatus.hibmobtech.contact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.model.contact.Contact;
import com.hibernatus.hibmobtech.model.contact.ContactRoleSite;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.view.TextDrawable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// c l a s s   C o n t a c t F r a g m e n t
// -----------------------------------------
public class ContactFragment extends Fragment {
    public static final String TAG = ContactFragment.class.getSimpleName();;
    protected String fragmentId;
    public List<Contact> contactList;
    protected Site currentSite;
    protected Long currentSiteId;
    protected View view;
    protected RecyclerView contactRecyclerView;
    protected TextView contactRecyclerViewCategoryTextView;
    protected TextView contactRecyclerViewAdress1TextView;
    protected TextView contactRecyclerViewAdress2TextView;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getString("fragmentType");
        Log.i(TAG, "onCreateView: fragmentType=" + fragmentId);

        if(SiteCurrent.getInstance().isCurrentSite()){
            currentSite = SiteCurrent.getInstance().getSiteCurrent();
            currentSiteId = currentSite.getId();
            Log.i(TAG, "onCreateView: currentSite=" + currentSite + " currentSiteId=" + currentSiteId);
            view = inflater.inflate(R.layout.contact_recycler_view, container, false);
            setContactList();
            setViews();
        }
        return view;
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    protected void setContactList() {
        final Set<ContactRoleSite> contactRoleSites = currentSite.getContactRoleSites();
        Log.i(TAG, "onCreateView: contactRoleSites=" + contactRoleSites);
        Iterator<ContactRoleSite> roleSiteIterator = contactRoleSites.iterator();
        contactList = new ArrayList<>();
        while (roleSiteIterator.hasNext()) {
            ContactRoleSite contactRoleSite = roleSiteIterator.next();
            Contact contact = contactRoleSite.getContact();
            Log.i(TAG, "onCreateView: contact=" + contact);
            contact.setRole(contactRoleSite.getRole());
            contactList.add(contact);
        }
        Log.i(TAG, "onCreateView: contactList=" + contactList);
    }

    protected void setViews() {
        if(currentSite == null){
            Log.e(TAG, "setViews: currentSite == null");
            return;
        }
        if(currentSite.getCategory() != null) {
            contactRecyclerViewCategoryTextView = (TextView) view.findViewById(R.id.contactRecyclerViewCategoryTextView);
            contactRecyclerViewCategoryTextView.setText(currentSite.getCategory().getName());
        }

        if(currentSite.getAddress() != null) {
            contactRecyclerViewAdress1TextView = (TextView) view.findViewById(R.id.contactRecyclerViewAdress1TextView);
            contactRecyclerViewAdress1TextView.setText(currentSite.getAddress().getAdressPart1());
            contactRecyclerViewAdress2TextView = (TextView) view.findViewById(R.id.contactRecyclerViewAdress2TextView);
            contactRecyclerViewAdress2TextView.setText(currentSite.getAddress().getAdressPart2());
        }

        contactRecyclerView = (RecyclerView) view.findViewById(R.id.contactRecyclerView);
        setupRecyclerView(contactRecyclerView, currentSite, contactList, fragmentId);
    }

    protected void setupRecyclerView(RecyclerView recyclerView, Site site, List<Contact> contactList, String fragmentId) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter(
                getActivity(),
                site,
                contactList,
                fragmentId));
    }

    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        Site site;
        String fragmentId;
        List<Contact> contactList;

        public RecyclerViewAdapter(
                Context context,
                Site site,
                List<Contact> contactList,
                String fragmentId) {
            this.site = site;
            this.contactList = contactList;
            this.fragmentId = fragmentId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_list_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            viewHolder.contact = contactList.get(position);
            Contact contact = viewHolder.contact;
            Log.i(TAG, "onBindViewHolder: viewHolder.contact=" + viewHolder.contact);

            // avatar
            String firstLetter = String.valueOf(contactList.get(position).getName().charAt(0));
            Log.i(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(contactList.get(position).getId());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px
            viewHolder.contactListRowAvatarImageView.setImageDrawable(drawable);

            // title
            if(contact.getTitle() == null) {
                viewHolder.contactListRowTitleTextView.setVisibility(View.GONE);
            }
            else {
                viewHolder.contactListRowTitleTextView.setText(contact.getTitle());
            }

            // fullname
            viewHolder.contactListRowNameTextView.setText(contact.getName());

            // function
            Log.i(TAG, "onBindViewHolder: contact.getRole()=" + contact.getRole());
            if(contact.getRole() == null) {
                viewHolder.contactListRowRoleTextView.setVisibility(View.GONE);
            }
            else {
                viewHolder.contactListRowRoleTextView.setText(contact.getRole());
            }

            viewHolder.view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    ContactCurrent.getInstance().setContactCurrent(contactList.get(position));

                    Log.i(TAG, "onClick: fragmentType=" + fragmentId + " comments =" + site.getComments());
                    Bundle bundleForDetailContact = new Bundle();
                    bundleForDetailContact.putLong("currentSiteId", site.getId());
                    bundleForDetailContact.putInt("positionContact", position);
                    bundleForDetailContact.putString("fragmentType", fragmentId);
                    bundleForDetailContact.putString("Comments", site.getComments());
                    bundleForDetailContact.putString("Address", site.getAddress().getAdress());

                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.putExtras(bundleForDetailContact);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public Contact contact;
            public final ImageView contactListRowAvatarImageView;
            public final TextView contactListRowTitleTextView;
            public final TextView contactListRowNameTextView;
            public final TextView contactListRowRoleTextView;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                contactListRowAvatarImageView = (ImageView) view.findViewById(R.id.contactListRowAvatarImageView);
                contactListRowTitleTextView = (TextView) view.findViewById(R.id.contactListRowTitleTextView);
                contactListRowNameTextView = (TextView) view.findViewById(R.id.contactListRowNameTextView);
                contactListRowRoleTextView = (TextView) view.findViewById(R.id.contactListRowRoleTextView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + contactListRowNameTextView.getText();
            }
        }
    }
}