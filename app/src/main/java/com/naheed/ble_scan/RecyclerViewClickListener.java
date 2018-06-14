package com.naheed.ble_scan;

import android.view.View;

/**
 * Created by Naheed on 6/12/2018.
 */
public interface RecyclerViewClickListener {

    void onItemClick(View view, int position);
    void onButtonClick(int position, Object object, String keyIdentifier);

}
