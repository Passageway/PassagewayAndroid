package com.passageway;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

/**
 * Recycler View Adapter to display all of the wallpaper categories in a list of cards
 */
public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    // TODO: Change these to default to what they had previously selected
    private int selectedPos = 0;
    private int lastSelectedPos = 0;

    private MainActivity mActivity;
    private ArrayList<FieldUnit> mUnits;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        public CardView container;


        public ViewHolder(Context c, View v) {
            super(v);
            container = (CardView) v.findViewById(R.id.card_view);
        }

        @Override
        public void onClick(View v) {

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecAdapter(ArrayList<FieldUnit> units, MainActivity activity) {
        mUnits = units;
        mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fieldunit_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(parent.getContext(), v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setAnimation(holder.container, position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
