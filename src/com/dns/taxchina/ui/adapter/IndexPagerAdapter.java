package com.dns.taxchina.ui.adapter;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import android.annotation.SuppressLint;import android.content.Context;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentManager;import android.support.v4.app.FragmentPagerAdapter;import android.view.ViewGroup;import com.dns.taxchina.service.model.IndexHeadModel;import com.dns.taxchina.ui.fragment.AdFragment;/** * @author fubiao * @version create time:2014-3-10_下午1:54:30 * @Description 首页 广告 图片 adapter */public class IndexPagerAdapter extends FragmentPagerAdapter {	private List<IndexHeadModel> list = new ArrayList<IndexHeadModel>();	public IndexPagerAdapter(Context context, FragmentManager fm, List<IndexHeadModel> l) {		super(fm);		if (l != null && !l.isEmpty()) {			this.list = l;		}	}	@SuppressLint("UseSparseArrays")	private HashMap<Integer, Object> cachDataMap = new HashMap<Integer, Object>();	@Override	public Fragment getItem(int arg0) {		AdFragment adFragment = AdFragment.newInstance(list.get(arg0), arg0);		return adFragment;	}	@Override	public int getCount() {		return list.size();	}	@Override	public void destroyItem(ViewGroup container, int position, Object object) {		super.destroyItem(container, position, object);	}}