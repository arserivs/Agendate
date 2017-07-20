package com.arsoft.agendate;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.views.CustomScrollView;

import java.util.Calendar;

/**
 * Created by larcho on 8/2/16.
 */
public class LoggedinBaseFragment extends Fragment implements ViewTreeObserver.OnScrollChangedListener {

    private TextView titleTW;
    private TextView subtitleTW;
    private CustomScrollView returnView;
    private boolean hidingActionBar = true;

    //public static Fragment getFragment(final Class<? extends Fragment> fragment, final UserInfo userInfo, Bundle bundle2) {
    public static Fragment getFragment(final Class<? extends Fragment> fragment, Bundle bundle2) {
        Bundle bundle = new Bundle();
        bundle.putString("fragmentClass", fragment.getName());
        //bundle.putParcelable("userInfo", userInfo);
        if(bundle2 != null){
            bundle.putBundle("searchService",bundle2);
        }

        LoggedinBaseFragment returnFragment = new LoggedinBaseFragment();
        returnFragment.setArguments(bundle);

        return returnFragment;
    }

    public static void setTitle(final Activity activity, final String title, final String subtitle) {
        LoggedinBaseFragment fragment = (LoggedinBaseFragment) activity.getFragmentManager().findFragmentByTag("loggedInBase");
        if (fragment != null) {
            fragment.setFragmentTitle(title, subtitle);
        }
    }

    public void setFragmentTitle(final String title, final String subtitle) {

        final UserInfo userInfo = getArguments().getParcelable("userInfo");

        if (title == null && subtitle == null) {
            subtitleTW.setVisibility(View.GONE);
            String titleString = "";
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour >= 6 && hour < 12) {
                titleString = "Buenos días ";
            } else if (hour >= 12 && hour < 18) {
                titleString = "Buenas tardes ";
            } else {
                titleString = "Buenas noches ";
            }
            if (userInfo != null) {
                titleString += userInfo.nombre;
            }
            titleTW.setText(titleString);
        } else if (title != null && subtitle == null) {
            subtitleTW.setVisibility(View.GONE);
            titleTW.setText(title);
        } else {
            subtitleTW.setVisibility(View.VISIBLE);
            titleTW.setText(title);
            subtitleTW.setText(subtitle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        returnView = (CustomScrollView) inflater.inflate(R.layout.loggedin_base, container, false);

        returnView.getViewTreeObserver().addOnScrollChangedListener(this);


        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        final UserInfo userInfo = getArguments().getParcelable("userInfo");
        final ImageView imageView = (ImageView) returnView.findViewById(R.id.loggedinbase_imageView);
        if (userInfo != null) {
            if (userInfo.getFotoPortada() != null) {
                imageView.setImageBitmap(userInfo.getFotoPortada());
            }
        }

        titleTW = (TextView) returnView.findViewById(R.id.loggedinbase_textViewTitle);
        subtitleTW = (TextView) returnView.findViewById(R.id.loggedinbase_textViewSubtitle);

        final Typeface mbBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Bold.ttf");
        final Typeface mbLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Light.ttf");

        titleTW.setTypeface(mbBold);
        subtitleTW.setTypeface(mbLight);

        setFragmentTitle(null, null);

        final String className = getArguments().getString("fragmentClass");

        if(savedInstanceState == null) {
            Fragment fragment = null;

            try {
                fragment = (Fragment) Class.forName(className).newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (fragment != null) {
                fragment.setArguments(getArguments().getBundle("searchService"));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.loggedinbase_frameLayout, fragment);
                if(className!=null) {
                    if (!className.equals(DashboardFragment.class.getName())) {
                        ft.addToBackStack(null);
                    }
                }
                ft.commit();
            }
        }

        return returnView;
    }

    @Override
    public void onDestroyView() {
        returnView.getViewTreeObserver().removeOnScrollChangedListener(this);
        returnView = null;
        super.onDestroyView();
    }

    @Override
    public void onScrollChanged() {
        int scrollY = returnView.getScrollY();
        if(hidingActionBar && scrollY > 10) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            hidingActionBar = false;
        } else if(!hidingActionBar && scrollY <= 10) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(null);
            hidingActionBar = true;
        }
    }
}
