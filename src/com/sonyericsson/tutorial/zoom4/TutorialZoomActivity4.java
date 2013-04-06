/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sonyericsson.tutorial.zoom4;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ViewAnimator;

import com.sonyericsson.zoom.DynamicZoomControl;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.LongPressZoomListener;

/**
 * Activity for zoom tutorial 4
 */
public class TutorialZoomActivity4 extends Activity 
//implements ViewFactory 
{

    /** Constant used as menu item id for resetting zoom state */
    private static final int MENU_ID_RESET = 0;

    /** Image zoom view */
    private ImageZoomView mZoomView;

    /** Zoom control */
    private DynamicZoomControl mZoomControl;

    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** On touch listener for zoom view */
    private LongPressZoomListener mZoomListener;
    
    private String src = "http://www.mangahere.com/manga/king_of_hell/v31/c235/";

    private String sec = "http://z.mhcdn.net/store/manga/958/31-238.0/compressed/g001.jpg?v=11364611627";
    private boolean retrieving = false;
    
    private ViewAnimator mViewAnimator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mZoomControl = new DynamicZoomControl();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image800x600);
        
        mZoomListener = new LongPressZoomListener(getApplicationContext());
        mZoomListener.setZoomControl(mZoomControl);
        
//        mImageSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher);
//        mImageSwitcher.setFactory(this);
//        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
//        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        
//        mViewAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
        

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable()
//        {
//            public void run()
//            {
//                mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
//                mZoomView.setImage(mBitmap);
//                mZoomView.setZoomState(mZoomControl.getZoomState());
//                mZoomView.setOnTouchListener(mZoomListener);
//                mZoomView.setAspectQuotient(480, 689);
////                
//                mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
//                resetZoomState();
//            }
//        }, 50);
//        mZoomView.setImage(mBitmap);
//        resetZoomState();
//        mImageSwitcher.setOnTouchListener(new OnTouchListener()
//        {
//            
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                Log.d("switcher", "here");
//                final int action = event.getAction();
//            
//                switch (action) 
//                {
//                case MotionEvent.ACTION_UP:
//                    mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.image800x600)));
//                    break;
//                }
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });
        
        mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
//        mZoomView.setImage(mBitmap);
        new GetHtml().execute(src);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mZoomView.dispose();
//        mZoomView.setOnTouchListener(null);
//        mZoomControl.getZoomState().deleteObservers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_RESET, 2, R.string.menu_reset);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_RESET:
                resetZoomState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reset zoom state and notify observers
     */
    private void resetZoomState() {
        mZoomControl.getZoomState().setPanX(0.5f);
        mZoomControl.getZoomState().setPanY(0.5f);
        mZoomControl.getZoomState().setZoom(1f);
        mZoomControl.getZoomState().notifyObservers();
    }
    
    private class GetHtml extends AsyncTask<String, Void, Bitmap>
    {

        protected Bitmap doInBackground(String... urls)
        {
            try
            {
                String html = "";
                Document doc = Jsoup.connect(urls[0]).get();
                Element image = doc.select("img").first();
                html = image.absUrl("src");
                if (!html.equals(""))
                {
//                  URL url = new URL(src);
//                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                  connection.setDoInput(true);
//                  connection.connect();
//                  InputStream input = connection.getInputStream();
                  mBitmap = BitmapFactory.decodeStream((InputStream)new URL(html).getContent());
//                    Drawable drawableImage = Drawable.createFromStream((InputStream)new URL(html).getContent(), "src");
              
                    return mBitmap;
                }
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return null;
        }
        
        protected void onPostExecute(Bitmap result)
        {
            if (result != null)
            {
                mZoomView.setImage(result);
                
//                mZoomView.setZoomState(mZoomControl.getZoomState());
//                mZoomView.setOnTouchListener(mZoomListener);
       
//                mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
//                resetZoomState();
            }
            
            retrieving = false;
        }
    }

//    @Override
//    public View makeView()
//    {
//        mZoomView = new ImageZoomView(this, null);
//
//        Log.d("why", "twice");
//        mZoomView.setScaleType(ImageZoomView.ScaleType.FIT_CENTER);
//        mZoomView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
////        mZoomView.setImage(mBitmap);
////        mZoomView.setZoomState(mZoomControl.getZoomState());
////        mZoomView.setOnTouchListener(mZoomListener);
////////        Log.d("quote", mZoomView.getAspectQuotient() + "");
////        mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
//////////        mZoomView.setBackgroundColor(0xFF000000);
////        resetZoomState();
////        new GetHtml().execute(src);
//        return mZoomView;
//    }

}
