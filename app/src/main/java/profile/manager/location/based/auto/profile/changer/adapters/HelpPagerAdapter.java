package profile.manager.location.based.auto.profile.changer.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import profile.manager.location.based.auto.profile.changer.R;

public class HelpPagerAdapter extends PagerAdapter {

    ArrayList<Integer> layoutList;
    Context context;

    public HelpPagerAdapter(Context context) {
        this.context = context;

        layoutList = new ArrayList<>();
        layoutList.add(R.layout.help_one_layout);
        layoutList.add(R.layout.help_two_layout);
        layoutList.add(R.layout.help_three_layout);
    }

    @Override
    public int getCount() {
        return layoutList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
         LayoutInflater inflater = LayoutInflater.from(context);

        ViewGroup layout = (ViewGroup) inflater.inflate(layoutList.get(position),
                collection,
                false);
        collection.addView(layout);
        return layout;
    }



    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
