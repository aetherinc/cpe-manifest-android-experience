package com.wb.nextgen.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by gzcheng on 2/24/16.
 */
public interface NextGenFragmentTransactionInterface {
    void transitRightFragment(Fragment nextFragment);

    void transitLeftFragment(Fragment nextFragment);

    void transitMainFragment(Fragment nextFragment);
}
