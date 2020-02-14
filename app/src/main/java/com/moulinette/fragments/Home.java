package com.moulinette.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.moulinette.R;
import com.moulinette.activities.QuizSection;
import com.moulinette.activities.RaffleDrawSection;
import com.moulinette.activities.VoteSection;
import com.moulinette.databinding.FragmentHomeBinding;
import com.moulinette.utilities.TinyDB;

import render.animations.Attention;
import render.animations.Bounce;
import render.animations.Render;
import render.animations.Slide;
import render.animations.Zoom;


public class Home extends Fragment {

    FragmentHomeBinding binding;
    MyClickHandler handlers;
    Render render;

    Activity ac;
    TinyDB tinyDB;

    @SuppressLint ( "WrongConstant" )
    @Override
    public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {

        binding = DataBindingUtil.inflate ( inflater , R.layout.fragment_home , container , false );

        ac = getActivity ( );
        tinyDB = new TinyDB ( ac );

        // Create Render Class
        render = new Render ( ac );

        Handler handler = new Handler ( );
        handler.postDelayed ( new Runnable ( ) {
            @Override
            public void run ( ) {


                // Set Animation
                render.setAnimation ( Zoom.In ( binding.quiz ) );
                render.start ( );

                render.setAnimation ( Zoom.InDown ( binding.raffleDraw ) );
                render.start ( );

                render.setAnimation ( Zoom.InUp ( binding.raffleDraw ) );
                render.start ( );

            }
        } , 100 );

        handler = new Handler ( );
        handler.postDelayed ( new Runnable ( ) {
            @Override
            public void run ( ) {
                binding.raffleDrawImg.setVisibility(View.VISIBLE);
                binding.raffleArow.setVisibility(View.VISIBLE);
                binding.raffleTxt.setVisibility(View.VISIBLE);


                binding.quizImg.setVisibility(View.VISIBLE);
                binding.quizArow.setVisibility(View.VISIBLE);
                binding.quizLay.setVisibility(View.VISIBLE);

                binding.voteImg.setVisibility(View.VISIBLE);
                binding.voteArow.setVisibility(View.VISIBLE);
                binding.voteText.setVisibility(View.VISIBLE);


                // Set Animation
                render.setAnimation ( Slide.InLeft ( binding.raffleDrawImg ) );
                render.start ( );

                render.setAnimation ( Slide.InLeft ( binding.quizImg ) );
                render.start ( );

                render.setAnimation ( Slide.InLeft ( binding.voteImg ) );
                render.start ( );


                render.setAnimation (Slide.InRight(binding.raffleArow) );
                render.start ( );


                render.setAnimation (Slide.InRight(binding.quizArow) );
                render.start ( );

                render.setAnimation (Slide.InRight(binding.voteArow) );
                render.start ( );


                render.setAnimation (Zoom.In(binding.raffleTxt) );
                render.start ( );

                render.setAnimation (Zoom.In(binding.quizLay) );
                render.start ( );

                render.setAnimation (Zoom.In(binding.voteText) );
                render.start ( );




            }
        } , 1000 );


        handlers = new MyClickHandler ( ac );
        binding.setClick ( handlers );
        return binding.getRoot ( );
    }

    public class MyClickHandler {


        Activity context;

        public MyClickHandler ( Activity ac ) {
            this.context = ac;
        }

        public void raffleDrawClick ( View view ) {
            Intent intent = new Intent ( ac , RaffleDrawSection.class );
            ac.startActivity ( intent );
            ac.overridePendingTransition ( R.anim.enter_from_right , R.anim.exit_to_left );
        }


        public void QuizSectionClick ( View view ) {
            Intent intent = new Intent ( ac , QuizSection.class );
            ac.startActivity ( intent );
            ac.overridePendingTransition ( R.anim.enter_from_right , R.anim.exit_to_left );
        }

        public void VoteSectionClick ( View view ) {
            Intent intent = new Intent ( ac , VoteSection.class );
            ac.startActivity ( intent );
            ac.overridePendingTransition ( R.anim.enter_from_right , R.anim.exit_to_left );
        }

    }

}
