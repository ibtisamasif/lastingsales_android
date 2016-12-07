package com.example.muzafarimran.lastingsales.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.receivers.CallsStatesReceiver;


public class PopupUIService extends Service {

    public static final Integer TIMES_RETRY_ALL_FILE_DOWNLOADS = 1;
    public static Intent thisIntent;
    WindowManager wm;
    LinearLayout popupMainLayout;
    LayoutInflater inflater;
    Context serviceContext;
    TextView tvNoteText;
    ImageView banner;
    ImageView logoSmall;
    ImageButton closeButton;
    ImageButton closeButtonShrunkView;
    Boolean isCarouselHidden = false;
    String selectedNumber;
    Long noteIdLong = null;
    private View largeInflatedView;
    private LinearLayout sliderParentLayout;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams shrunkParams;

    public static String getLocalFilePathFromFileName(String fileName, Context _context) {
        String path = _context.getFilesDir().getAbsolutePath() + "/" + fileName;
        return path;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onCreate();
        serviceContext = this;
        Bundle bundle = intent.getExtras();
        String noteIdString;
        if (bundle != null) {
            noteIdString = bundle.getString(CallsStatesReceiver.OUTGOINGCALL_CONTACT_NOTE_ID);
            if (noteIdString != null && !noteIdString.equals("")) {
                noteIdLong = Long.parseLong(noteIdString);
            }
        }
        executeServiceWork();
        return START_STICKY;
    }

    private void executeServiceWork() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        largeInflatedView = inflater.inflate(R.layout.uiocall_popup_large_layout, null);
        initializeAllViewsFromThisParentView(largeInflatedView);
        ImageButton closeButton = (ImageButton) largeInflatedView.findViewById(R.id.closeImageButton);
        popupMainLayout = (LinearLayout) largeInflatedView.findViewById(R.id.popupMainLayout);
        closeButton.setOnClickListener(getHideButtonClickListener());
        initializeAllViewsFromThisParentView(largeInflatedView);
        banner.setOnClickListener(getLargeLogoClickListener());
        banner.setScaleType(ImageView.ScaleType.FIT_XY);
        wm.addView(largeInflatedView, params);
    }

    private void initializeAllViewsFromThisParentView(View largeInflatedView) {
        tvNoteText = (TextView) largeInflatedView.findViewById(R.id.tvNoteTextUIOCallPopup);
        LSNote tempNote = null;
        if (noteIdLong != null) {
            tempNote = LSNote.findById(LSNote.class, noteIdLong);
            tvNoteText.setText(tempNote.getNoteText());
        }
        banner = (ImageView) largeInflatedView.findViewById(R.id.imBanner);
    }

    public View.OnTouchListener getOnTouchListenerDragDrop(final View shrunkView) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_MOVE | event.getAction() == MotionEvent.ACTION_DOWN )
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
               /* You can play around with the offset to set where you want the users finger to be on the view. Currently it should be centered.*/
                    int xOffset = v.getWidth() / 2;
                    int yOffset = v.getHeight() / 2;
                    int x = (int) event.getRawX() - xOffset;
                    int y = (int) event.getRawY() - yOffset;
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            x, y,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity.LEFT;
                    wm.updateViewLayout(largeInflatedView, params);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private View.OnClickListener getLargeLogoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }

    private View.OnClickListener getHideButtonClickListener() {
        return
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout largeLayout = (LinearLayout) largeInflatedView;
                        largeLayout.removeAllViews();
                        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View shrunkView = inflater.inflate(R.layout.uiocall_popup_shrunk_layout, null);
                        shrunkParams = new WindowManager.LayoutParams();
                        shrunkParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                        shrunkParams.horizontalMargin = 0;
                        shrunkParams.verticalMargin = 0;
                        shrunkView.setLayoutParams(shrunkParams);
                        shrunkView.setOnClickListener(getShrunkViewOnClickListener());
                        logoSmall = (ImageView) shrunkView.findViewById(R.id.imLogoSmall);
                        logoSmall.setScaleType(ImageView.ScaleType.FIT_XY);
                        closeButtonShrunkView = (ImageButton) shrunkView.findViewById(R.id.closeImageButtonShrunkView);
                        closeButtonShrunkView.setOnClickListener(getCloseButtonListener());
                        ((LinearLayout) largeLayout).addView(shrunkView);
                    }
                };
    }

    private View.OnLongClickListener getShrunkViewLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(getOnTouchListenerDragDrop(v));
                return true;
            }
        };
    }

    private View.OnClickListener getShrunkViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View inflatedView;
                final LinearLayout mainView = (LinearLayout) largeInflatedView;
                mainView.removeAllViews();
                inflatedView = inflater.inflate(R.layout.uiocall_popup_large_layout, null);
                closeButton = (ImageButton) inflatedView.findViewById(R.id.closeImageButton);
                closeButton.setOnClickListener(getHideButtonClickListener());
                initializeAllViewsFromThisParentView(inflatedView);
                banner.setScaleType(ImageView.ScaleType.FIT_XY);
                banner.setOnClickListener(getLargeLogoClickListener());
                ((LinearLayout) largeInflatedView).addView(inflatedView);
            }
        };
    }

    public void onDestroy() {
        wm.removeView(largeInflatedView);
        super.onDestroy();
//        stopThisService();
//        System.exit(0);
    }

    public void stopThisService() {
        ((Service) serviceContext).stopSelf();
    }

    public View.OnClickListener getCloseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onDestroy();
                Toast.makeText(serviceContext, "Clicked Close Button", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void showFeedbackPopup() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View feedbackView = inflater.inflate(R.layout.uiocall_popup_feedback_layout, null);
        RatingBar feedbackRatingBar = (RatingBar) feedbackView.findViewById(R.id.feedbackStarsBar);
        LayerDrawable stars = (LayerDrawable) feedbackRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        feedbackRatingBar.setStepSize(1);
        feedbackRatingBar.setNumStars(5);
        Toast.makeText(getApplicationContext(), "Rating: " + feedbackRatingBar.getRating(), Toast.LENGTH_SHORT).show();
        feedbackRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getApplicationContext(), "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        wm.addView(feedbackView, params);
    }
}