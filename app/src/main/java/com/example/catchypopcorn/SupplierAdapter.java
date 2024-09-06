package com.example.catchypopcorn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder> {

    private final List<Map<String, Object>> supplierList;

    public SupplierAdapter(List<Map<String, Object>> supplierList) {
        this.supplierList = supplierList;
    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_list_item, parent, false);
        return new SupplierViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SupplierViewHolder holder, int position) {
        Map<String, Object> supplier = supplierList.get(position);
        holder.supplierName.setText(supplier.get("name").toString());
        holder.supplierAddress.setText(supplier.get("address").toString());
        holder.supplierContact.setText(supplier.get("contact").toString());
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {

        public TextView supplierName;
        public TextView supplierAddress;
        public TextView supplierContact;

        public SupplierViewHolder(View itemView) {
            super(itemView);
            supplierName = itemView.findViewById(R.id.supplierName);
            supplierAddress = itemView.findViewById(R.id.supplierAddress);
            supplierContact = itemView.findViewById(R.id.supplierContact);
        }
    }
}
