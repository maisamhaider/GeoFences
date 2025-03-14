package profile.manager.location.based.auto.profile.changer.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import profile.manager.location.based.auto.profile.changer.R;
import profile.manager.location.based.auto.profile.changer.fragments.DoneFragment;
import profile.manager.location.based.auto.profile.changer.fragments.UndoneFragment;

public class HistoryFragmentsAdapter extends FragmentPagerAdapter {
    int[] titles = new int[]{R.string.un_done,R.string.done};
    Context context;

    public HistoryFragmentsAdapter(Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return UndoneFragment.newInstance();
            case 1:
                return DoneFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(titles[position]);
    }
}
