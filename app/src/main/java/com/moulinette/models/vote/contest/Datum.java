
package com.moulinette.models.vote.contest;

import android.net.Uri;

import androidx.databinding.BindingAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.moulinette.utilities.Config;
import com.moulinette.utilities.WrapContentDraweeView;

import static com.moulinette.utilities.Config.Image_URL;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("question_id")
    @Expose
    private String questionId;
    @SerializedName("question_name")
    @Expose
    private Object questionName;
    @SerializedName("option_1")
    @Expose
    private String option1;
    @SerializedName("option_1a")
    @Expose
    private String option1a;
    @SerializedName("option_1b")
    @Expose
    private String option1b;
    @SerializedName("option_1b_name")
    @Expose
    private String option1bName;
    @SerializedName("status")
    @Expose
    private String status;

    boolean selected = false;


    @BindingAdapter({"image"})
    public static void loadImage(SimpleDraweeView view, String imageUrl) {

        imageUrl = Image_URL +imageUrl;
        System.out.println("Image Url:   "+imageUrl);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl.replace(" ", "%20")))
                .setResizeOptions(new ResizeOptions(120, 120))
                .build();
        view.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(view.getController())
                        .setImageRequest(request)
                        .build());
    }



    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Object getQuestionName() {
        return questionName;
    }

    public void setQuestionName(Object questionName) {
        this.questionName = questionName;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption1a() {
        return option1a;
    }

    public void setOption1a(String option1a) {
        this.option1a = option1a;
    }

    public String getOption1b() {
        return option1b;
    }

    public void setOption1b(String option1b) {
        this.option1b = option1b;
    }

    public String getOption1bName() {
        return option1bName;
    }

    public void setOption1bName(String option1bName) {
        this.option1bName = option1bName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
