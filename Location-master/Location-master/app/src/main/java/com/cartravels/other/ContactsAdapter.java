package com.cartravels.other;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cartravels.loca.R;

/**
 * Created by shell on 11/11/16.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private SortedList<Contact> mContacts;

    public ContactsAdapter(Context context, LayoutInflater layoutInflater, Contact... items) {

        mLayoutInflater = layoutInflater;
        mContacts = new SortedList<Contact>(Contact.class, new SortedListAdapterCallback<Contact>(this) {
            @Override
            public int compare(Contact t0, Contact t1) {
                return t0.getName().compareTo(t1.getName());
            }

            @Override
            public boolean areContentsTheSame(Contact oldItem,
                                              Contact newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areItemsTheSame(Contact item1, Contact item2) {
                return item1.getMobileNo().equals(item2.getMobileNo());
            }
        });

        if (items != null) {
            for (Contact item : items) {
                mContacts.add(item);
            }
        }
    }

    public void addItem(Contact item) {
        mContacts.add(item);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        return new ContactViewHolder(
                mLayoutInflater.inflate(R.layout.contact_list_item, parent, false)) {
            @Override
            void onDoneChanged(boolean isDone) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                mContacts.recalculatePositionOfItemAt(adapterPosition);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        ((ContactViewHolder) holder).mTextViewName.setText(contact.getName());
        ((ContactViewHolder) holder).mTextViewMobileNo.setText(contact.getMobileNo());
    }

    @Override
    public int getItemCount() {
        if (mContacts != null) {
            return mContacts.size();
        } else {
            return 0;
        }
    }
}

