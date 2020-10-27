package ke.co.mobiticket.mobiticket.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.CustomToast;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class BaseActivity extends AppCompatActivity {

    /*variable declaration*/
    public CustomToast mToast;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_toolbar);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    /*show custom toast*/
    public void showToast(String aContent) {
        mToast.setCustomView(aContent);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = new CustomToast(this);
        setStatusBarGradiant(this);
    }

    /*set type face*/
    public void setTypeFace(EditText tv) {
        Typeface face = Typeface.createFromAsset(getAssets(), "font/googlesansregular.ttf");
        tv.setTypeface(face);
    }

    /*load fragment*/
    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            fadeOutIn(findViewById(R.id.frame_container));
        }
    }

    /*Animation*/
    public void RunLayoutAnimation(RecyclerView recyclerView) {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_up_to_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /*Fade out in*/
    public void fadeOutIn(View view) {
        view.setAlpha(0);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.5f, 1f);
        ObjectAnimator.ofFloat(view, "alpha", 0f).start();
        animatorAlpha.setDuration(300);
        animatorSet.play(animatorAlpha);
        animatorSet.start();
    }

    /*intent*/
    public void startActivity(Class aClass) {
        startActivity(new Intent(this, aClass));
    }

    /*show view*/
    public void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /*hide view*/
    public void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    /*invisible view*/
    public void invisibleView(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    /* set notification */
    public void SetNotificationImage(ImageView view) {
        Glide.with(this).load(R.raw.gif_bell).into(view);
    }

    /* load image */
    public void setImage(int i,ImageView imageView) {
        Glide.with(this).load(i).into(imageView);
    }

    public void showCustomDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(BaseActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BaseActivity.this, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }
}
