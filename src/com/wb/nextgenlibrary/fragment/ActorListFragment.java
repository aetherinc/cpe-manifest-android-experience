package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;

import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.SensitiveFragmentInterface;
import com.wb.nextgenlibrary.network.BaselineApiDAO;

import com.wb.nextgenlibrary.data.MovieMetaData.CastData;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FontFitTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class ActorListFragment extends ExtraLeftListFragment<CastData> implements SensitiveFragmentInterface {


    public static enum TalentListMode{
        ACTOR_MODE, CHARACTER_MODE;
    }

    protected TalentListMode listMode = TalentListMode.ACTOR_MODE;
    private ToggleButton actorModeSwitch = null;
    private boolean bFixedMode = false;

    public void setForcedMode(TalentListMode mode){
        listMode = mode;
        bFixedMode = true;
        if (actorModeSwitch != null)
            actorModeSwitch.setVisibility(View.GONE);
    }


    protected int getLayoutId(){
        return R.layout.next_gen_actor_list_view;
    }

    protected void onModeChanged(){

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actorModeSwitch = (ToggleButton)view.findViewById(R.id.actor_mode_switch);
        if (actorModeSwitch != null) {
            if (NextGenExperience.getMovieMetaData().hasActorCharacterMode() && !bFixedMode) {
                actorModeSwitch.setVisibility(View.VISIBLE);
                actorModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            listMode = TalentListMode.CHARACTER_MODE;
                        } else {
                            listMode = TalentListMode.ACTOR_MODE;
                        }
                        onModeChanged();
                        listAdaptor.notifyDataSetChanged();
                        if (listAdaptor.selectedIndex >= 0){
                            onItemClick(null,listView, listAdaptor.selectedIndex, 0);
                        }
                    }
                });
            } else
                actorModeSwitch.setVisibility(View.GONE);
        }



        BaselineApiDAO.getCastActorsImages(NextGenExperience.getMovieMetaData().getActorsAndCharactersList(), new ResultListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listAdaptor != null)
                                listAdaptor.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public <E extends Exception> void onException(E e) {

            }
        });

    }

    @Override
    protected int getPadding(){
        float density = NextGenExperience.getScreenDensity(getActivity());
        int spacing = (int)(5 *density);
        return 0;
    }


    public List<CastData> getActorInfos(){
        if (listMode == TalentListMode.ACTOR_MODE) {
            if (NextGenExperience.getMovieMetaData().getActorsList() != null)
                return NextGenExperience.getMovieMetaData().getActorsList();
        } else {
            if (NextGenExperience.getMovieMetaData().getCharactersList() != null)
                return NextGenExperience.getMovieMetaData().getCharactersList();
        }
        return new ArrayList<CastData>();
    }


    @Override
    protected void onListItemClick(int index, CastData selectedObject){

        if (getActivity() instanceof NGEFragmentTransactionInterface){
            ActorDetailFragment target = new ActorDetailFragment();
            target.setDetailObject(selectedObject);
            ((NGEFragmentTransactionInterface)getActivity()).transitRightFragment(target);
            ((NGEFragmentTransactionInterface)getActivity()).resetUI(false);

        }
        listAdaptor.notifyDataSetChanged();
        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_TALENT, selectedObject.getId(), null);
    }

    protected int getListItemCount() {
        return getActorInfos().size();
    }

    protected CastData getListItemAtPosition(int i) {
        return getActorInfos().get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_actors_row;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }

    protected void fillListRowWithObjectInfo(View rowView, final CastData thisActor) {


        final ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);

        if(!thisActor.displayName.equals(realNameTxt.getText())) {
            realNameTxt.setText(thisActor.displayName.toUpperCase());
            characterNameTxt.setText(thisActor.charactorName);
        }

        FontFitTextView initialText = (FontFitTextView)rowView.findViewById(R.id.next_gen_actor_initial);
        initialText.setText(thisActor.displayName.substring(0,1).toUpperCase());


        if (thisActor.getBaselineCastData() != null && !StringHelper.isEmpty(thisActor.getBaselineCastData().getThumbnailImageUrl())){
            Picasso.with(getActivity()).load(thisActor.getBaselineCastData().getThumbnailImageUrl()).fit().centerCrop().into(avatarImg);
            initialText.setVisibility(View.GONE);
            // have to use picasso in this case because Glide won't do return any bitmap for centerCrop images.
        }else
            initialText.setVisibility(View.VISIBLE);


    }

    protected String getHeaderText(){
        if (listMode == TalentListMode.ACTOR_MODE)
            return NextGenExperience.getMovieMetaData().getActorGroupText();
        else
            return NextGenExperience.getMovieMetaData().getCharacterGroupText();
    }


    public void notifyCurrentSensitiveFragment(Fragment fragment){
        if (!(fragment instanceof ActorDetailFragment) ){
            resetSelectedItem();
            listAdaptor.notifyDataSetChanged();
            ((NGEFragmentTransactionInterface)getActivity()).resetUI(true);
        }
    }
}
