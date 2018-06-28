/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.example.muzafarimran.lastingsales.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.muzafarimran.lastingsales.chatheadbubble.BubbleBaseLayout;
import com.example.muzafarimran.lastingsales.chatheadbubble.BubbleLayout;
import com.example.muzafarimran.lastingsales.chatheadbubble.BubblesLayoutCoordinator;

public class BubblesService extends Service {
    private BubblesServiceBinder binder = new BubblesServiceBinder();
    private BubbleLayout mbbl;
    //private List<BubbleLayout> bubbles = new ArrayList<>();
//    private BubbleTrashLayout bubblesTrash;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mbbl != null)
            recycleBubble(mbbl);
        return super.onUnbind(intent);
    }

    private void recycleBubble(final BubbleLayout bubble) {
   /*     new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (bubble != null) {
                    try {
                        getWindowManager().removeView(bubble);
                        bubble.notifyBubbleRemoved();
                        mbbl = null;
                    } catch (Exception e) {
                        e.printStackTrace(); // no static method crash google device
                    }
                }
            }
        });*/
    }

    private WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }

    public void addBubble(BubbleLayout bubble, int x, int y) {
        WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(x, y);
        bubble.setWindowManager(getWindowManager());
        bubble.setViewParams(layoutParams);
        bubble.setLayoutCoordinator(layoutCoordinator);
        mbbl = bubble;
        addViewToWindow(bubble);
    }

//    void addTrash(int trashLayoutResourceId) {
//        if (trashLayoutResourceId != 0) {
//            bubblesTrash = new BubbleTrashLayout(this);
//            bubblesTrash.setWindowManager(windowManager);
//            bubblesTrash.setViewParams(buildLayoutParamsForTrash());
//            bubblesTrash.setVisibility(View.GONE);
//            LayoutInflater.from(this).inflate(trashLayoutResourceId, bubblesTrash, true);
//            addViewToWindow(bubblesTrash);
//            initializeLayoutCoordinator();
//        }
//    }

//    private void initializeLayoutCoordinator() {
//        layoutCoordinator = new BubblesLayoutCoordinator.Builder(this)
//                .setWindowManager(getWindowManager())
//                .setTrashView(bubblesTrash)
//                .build();
//    }

    private void addViewToWindow(final BubbleBaseLayout view) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindowManager().addView(view, view.getViewParams());
            }
        });
    }

    private WindowManager.LayoutParams buildLayoutParamsForBubble(int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = x;
        params.y = y;
        return params;
    }

//    private WindowManager.LayoutParams buildLayoutParamsForTrash() {
//        int x = 0;
//        int y = 0;
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSPARENT);
//        params.x = x;
//        params.y = y;
//        return params;
//    }

    public void removeBubble(BubbleLayout bubbl) {
        if (bubbl != null) {
            recycleBubble(bubbl);
        }
    }

    public class BubblesServiceBinder extends Binder {
        public BubblesService getService() {
            return BubblesService.this;
        }
    }
}