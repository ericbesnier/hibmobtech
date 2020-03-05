package com.hibernatus.hibmobtech.invoice;

import android.app.Activity;
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

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Invoice;
import com.hibernatus.hibmobtech.site.SiteApiService;
import com.hibernatus.hibmobtech.site.SiteCurrent;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;

// c l a s s   I n v o i c e F r a g m e n t
// -----------------------------------------
public class InvoiceFragment extends Fragment {
    public static final String TAG = InvoiceFragment.class.getSimpleName();
    protected String fragmentId;
    protected String parentClass;
    protected Activity activity;

    protected List<Invoice> invoiceList = Collections.emptyList();

    protected View view;
    protected RecyclerView invoiceRecyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getString("fragmentType");
        parentClass = getArguments().getString("parentClass");
        activity = getActivity();
        Log.d(TAG, "onCreateView: fragmentType=" + fragmentId);

        if(SiteCurrent.getInstance().isCurrentSite()){
            view = inflater.inflate(R.layout.invoice_recycler_view, container, false);
            findViews();
            setViews();
            recyclerViewAdapter = new RecyclerViewAdapter(
                    activity,
                    SiteCurrent.getInstance().getSiteCurrent().getId(),
                    invoiceList,
                    fragmentId,
                    parentClass);
            Log.d(TAG, "onCreateView: recyclerViewAdapter=" + recyclerViewAdapter);
            invoiceRecyclerView.setAdapter(recyclerViewAdapter);
            loadInvoice(SiteCurrent.getInstance().getSiteCurrent().getId());
        }
        return view;
    }

    @Override
    public void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);

        if (requestCode == HibmobtechApplication.MACHINE_CREATE_REQUEST) {
            loadInvoice(SiteCurrent.getInstance().getSiteCurrent().getId());
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    protected void findViews() {
        invoiceRecyclerView = (RecyclerView)view.findViewById(R.id.invoiceRecyclerView);
    }

    protected void setViews() {
        invoiceRecyclerView.setHasFixedSize(true);
    }

    public void loadInvoice(final long siteId){
        Log.d(TAG, "loadInvoice");
        invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(invoiceRecyclerView.getContext()));
        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();

        service.getSiteInvoices(siteId, new Callback<List<Invoice>>() {
            @Override
            public void success(List<Invoice> invoices, retrofit.client.Response response) {
                if(invoices == null) return;

                Log.d(TAG, "loadInvoice: success: invoices=" + invoices);
                invoiceList = invoices;

                recyclerViewAdapter = new RecyclerViewAdapter(
                        activity,
                        siteId,
                        invoiceList,
                        fragmentId,
                        parentClass);
                invoiceRecyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
            }
        });
    }

    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        String parentClass;
        String fragmentId;
        long siteId;
        private List<Invoice> invoiceList;
        Activity activity;

        public RecyclerViewAdapter(
                Activity activity,
                long siteId,
                List<Invoice> invoiceList,
                String fragmentId,
                String parentClass) {
            this.siteId = siteId;
            this.invoiceList = invoiceList;
            this.fragmentId = fragmentId;
            this.parentClass = parentClass;
            this.activity = activity;
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            private List<Invoice> invoiceList;
            Activity activity;

            public TextView invoiceListRowIdTextView;
            public TextView invoiceListRowNumberTextView;
            public TextView invoiceListRowIssuedDateTextView;
            public ImageView invoiceListRowStateImageViewIcon;
            public TextView invoiceListRowAmountTextView;
            public TextView invoiceListRowPaidDateTextView;
            public TextView invoiceListRowTitlePaidDateTextView;

            public ViewHolder(View view, List<Invoice> invoiceList, Activity activity) {
                super(view);
                this.view = view;
                this.invoiceList = invoiceList;
                this.activity = activity;

                invoiceListRowIdTextView = (TextView) view.findViewById(R.id.invoiceListRowIdTextView);
                invoiceListRowNumberTextView = (TextView) view.findViewById(R.id.invoiceListRowNumberTextView);
                invoiceListRowStateImageViewIcon = (ImageView) view.findViewById(R.id.invoiceListRowStateImageViewIcon);
                invoiceListRowIssuedDateTextView = (TextView) view.findViewById(R.id.invoiceListRowIssuedDateTextView);
                invoiceListRowAmountTextView = (TextView) view.findViewById(R.id.invoiceListRowAmountTextView);
                invoiceListRowPaidDateTextView = (TextView) view.findViewById(R.id.invoiceListRowPaidDateTextView);
                invoiceListRowTitlePaidDateTextView = (TextView) view.findViewById(R.id.invoiceListRowTitlePaidDateTextView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.invoice_list_row, parent, false);
            return new ViewHolder(view, invoiceList, activity);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            final Invoice invoice = invoiceList.get(position);
            Log.d(TAG, "onBindViewHolder: invoice=" + invoice);

            // id
            if(invoice.getId() != null) {
                viewHolder.invoiceListRowIdTextView.setText(invoice.getId().toString());
            }
            else{
                viewHolder.invoiceListRowIdTextView.setText("");
            }

            // number
            if(invoice.getNumber() != null) {
                viewHolder.invoiceListRowNumberTextView.setText(invoice.getNumber());
            }
            else{
                viewHolder.invoiceListRowNumberTextView.setText("");
            }

            // state
            if(invoice.getPaidDate() != null) {
                invoice.setState(true); // état : payée > true
            }
            else {
                invoice.setState(false); // état : impayée > false
            }
            if(invoice.isState() == true){
                viewHolder.invoiceListRowStateImageViewIcon.setImageResource(R.mipmap.ic_complete);
            }
            else{
                viewHolder.invoiceListRowStateImageViewIcon.setImageResource(R.mipmap.ic_error);

            }


            // issuedDate
            if(invoice.getIssuedDate() != null) {
                SimpleDateFormat issuedDateSdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);
                String issuedDateString;
                issuedDateString = issuedDateSdf.format(invoice.getIssuedDate());
                viewHolder.invoiceListRowIssuedDateTextView.setText(issuedDateString);
            }
            else{
                viewHolder.invoiceListRowIssuedDateTextView.setText("");
            }

            // amount
            if(invoice.getAmount() != null) {
                viewHolder.invoiceListRowAmountTextView.setText(invoice.getAmount().toString() + "€");
            }
            else{
                viewHolder.invoiceListRowAmountTextView.setText("");
            }

            // paidDate
            if(invoice.getPaidDate() != null) {
                SimpleDateFormat paidDateSdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);
                String paidDateString;
                paidDateString = paidDateSdf.format(invoice.getPaidDate());
                viewHolder.invoiceListRowPaidDateTextView.setText(paidDateString);
            }
            else{
                viewHolder.invoiceListRowTitlePaidDateTextView.setVisibility(View.INVISIBLE);
                viewHolder.invoiceListRowPaidDateTextView.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return invoiceList.size();
        }
    }
}