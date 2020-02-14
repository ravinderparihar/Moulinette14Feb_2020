package com.moulinette.utilities;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;


public class WrapContentDraweeView extends SimpleDraweeView {

  public Animatable mGif;
  private View mGifButton;
  public Boolean mRunning = false;
  public  static List<Animatable> mGifList = new ArrayList<>();


  public   boolean onClick(int pos){

    if(mRunning) {
      mGifList.get(pos).stop();
      mGif.stop();
      mRunning = false;
    }
    else {
      mGifList.get(pos).stop();
      mGif.start();
      mRunning = true;
    }
    return mRunning;
  }


  public   boolean onClick(){
    if(mRunning) {
      mGif.stop();
      mRunning = false;
    }
    else {
      mGif.start();
      mRunning = true;
    }
    return mRunning;
  }
  public ControllerListener mListener = new BaseControllerListener<ImageInfo>() {
    @Override
    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
      updateViewSize(imageInfo);
    }

    @Override
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
      updateViewSize(imageInfo);
      if(animatable != null) {
        mGif = animatable;
        mGifList.add(mGif);
        if(mGifButton != null) {
          mGifButton.setVisibility(VISIBLE);
          setClickable(true);
        }
      }
      else {
        mGif = null;
      }
    }
  };

  public WrapContentDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
  }

  public WrapContentDraweeView(Context context) {
    super(context);
  }

  public WrapContentDraweeView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public WrapContentDraweeView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public WrapContentDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }


  public void setImageURI(String uri, Object callerContext, View gifImg) {
    setImageURI(uri, callerContext);
    mGifButton = gifImg;
  }

  @Override
  public void setImageURI(Uri uri, Object callerContext) {
    mGifButton = null;
    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build();
    DraweeController controller = ((PipelineDraweeControllerBuilder)getControllerBuilder())
            .setControllerListener(mListener)
            .setCallerContext(callerContext)
//                .setAutoPlayAnimations(true)
            .setImageRequest(request)
            .setOldController(getController())
            .build();

    setController(controller);
  }

  public   void updateViewSize(ImageInfo imageInfo) {
    if (imageInfo != null) {
      setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
    }
  }
}
