package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 3/28/16.
 */
public class IMEElementsGridFragment extends NextGenGridViewFragment implements NextGenPlaybackStatusListener {

    List<MovieMetaData.IMEElementsGroup> imeGroups;
    final List<NextGenIMEEngine> imeEngines = new ArrayList<NextGenIMEEngine>();
    long currentTimeCode = 0L;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imeGroups = NextGenApplication.getMovieMetaData().getImeElementGroups();
        for (MovieMetaData.IMEElementsGroup group : imeGroups){
            imeEngines.add(new NextGenIMEEngine(group.getIMEElementesList()));
        }
        super.onViewCreated(view, savedInstanceState);
    }

    protected void onListItmeClick(View v, int position, long id){

    }

    protected int getNumberOfColumns(){
        return 2;
    }

    protected int getListItemCount(){
        return imeGroups.size();
    }

    protected Object getListItemAtPosition(int i){
        return imeGroups.get(i);
    }

    protected int getListItemViewId(){
        return R.layout.ime_grid_item_view;
    }

    protected void fillListRowWithObjectInfo(int position, View rowView, Object item, boolean isSelected){
        TextView titleText= (TextView)rowView.findViewById(R.id.ime_title);
        TextView subText1= (TextView)rowView.findViewById(R.id.ime_desc_text1);
        TextView subText2= (TextView)rowView.findViewById(R.id.ime_desc_text2);
        ImageView poster = (ImageView)rowView.findViewById(R.id.ime_image_poster);

        MovieMetaData.IMEElementsGroup group = (MovieMetaData.IMEElementsGroup)item;

        if (titleText != null && group.linkedExperience != null){
            titleText.setText(group.linkedExperience.title);
        }

        NextGenIMEEngine engine = imeEngines.get(position);
        boolean hasChanged = engine.computeCurrentIMEElement(currentTimeCode);
        Object imeObj = engine.getCurrentIMEElement();
        if (hasChanged && imeObj != null ){
            if (imeObj instanceof MovieMetaData.PresentationDataItem) {
                if (poster != null) {
                    PicassoTrustAll.loadImageIntoView(getActivity(), ((MovieMetaData.PresentationDataItem) imeObj).getPosterImgUrl(), poster);
                }

                if (subText1 != null) {
                    subText1.invalidate();
                    subText1.setText(((MovieMetaData.PresentationDataItem) imeObj).title);
                }
            }
        }


    }

    public void playbackStatusUpdate(final NextGenPlaybackStatus playbackStatus, final long timecode){
        currentTimeCode = timecode;
        listAdaptor.notifyDataSetChanged();
    }

    protected String getHeaderText(){
        return "";
    }

    protected int getHeaderChildenCount(int header){
        if (header == 0)
            return imeGroups.size();
        else
            return 0;
    }

    protected int getHeaderCount(){
        return 0;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }
}
