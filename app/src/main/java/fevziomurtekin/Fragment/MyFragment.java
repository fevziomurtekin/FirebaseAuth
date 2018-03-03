package fevziomurtekin.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuarkdijital.fixturemakersocial.R;

import fevziomurtekin.MyFragmentActivity;
import fevziomurtekin.Typefaces;

/**
 * Created by omurt on 19.02.2018.
 */

public class MyFragment extends Fragment {
    public static final String KEY_ID = "res_id";
    private View mRoot;
    private int mRootResId;
    private LayoutInflater mInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mRootResId = getArguments().getInt(KEY_ID);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater=inflater;
        mRoot=inflater.inflate(mRootResId,container,false);
        createItems();
        return mRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    protected void getData() {

    }

    protected void createItems() {

    }

    public static MyFragment newInstance() {

        Bundle args = new Bundle();
        args.putInt("RES_ID", R.layout.activity_main);
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View getView() {return super.getView();}

    public int getmRootResId() {return mRootResId;}

    public View getChildItems(int id) {
        return mRoot.findViewById(id);
    }

    public Typefaces getTypeFaces() {
        return ((MyFragmentActivity) getActivity()).getTypeFaces();
    }

    public MyFragmentActivity getAct () {return (MyFragmentActivity) getActivity();}

    public LayoutInflater getmInflater() {
        return mInflater;
    }

    public View getmRoot() {
        return mRoot;
    }

    public float getHeight() {
        return ((MyFragmentActivity) getActivity()).getHeight();
    }

    public float getWidth() {
        return ((MyFragmentActivity) getActivity()).getWidth();
    }

    public float getScale() {
        return ((MyFragmentActivity) getActivity()).getScale();
    }

    protected boolean isEmpty(String text){
        if(text.length()==0) return true;
        else return false;

    }

}
