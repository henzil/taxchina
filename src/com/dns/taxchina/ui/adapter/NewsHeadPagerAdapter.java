package com.dns.taxchina.ui.adapter;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import android.annotation.SuppressLint;import android.content.Context;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentManager;import android.support.v4.app.FragmentPagerAdapter;import android.view.ViewGroup;import com.dns.taxchina.service.model.NewsItemHeadModel;import com.dns.taxchina.ui.fragment.NewsItemHeadFragment;/** * @author fubiao * @version create time:2014-3-10_下午1:54:30 * @Description 新闻 head 图片 adapter */public class NewsHeadPagerAdapter extends FragmentPagerAdapter {	private String titleStr;	private List<NewsItemHeadModel> list = new ArrayList<NewsItemHeadModel>();	public NewsHeadPagerAdapter(Context context, FragmentManager fm, List<NewsItemHeadModel> l, String titleStr) {		super(fm);		if (l != null && !l.isEmpty()) {			this.list = l;		}		this.titleStr = titleStr;	}	@SuppressLint("UseSparseArrays")	private HashMap<Integer, Object> cachDataMap = new HashMap<Integer, Object>();	@Override	public Fragment getItem(int arg0) {		NewsItemHeadFragment adFragment = NewsItemHeadFragment.newInstance(list.get(arg0), titleStr);		return adFragment;	}	@Override	public int getCount() {		return list.size();	}	@Override	public void destroyItem(ViewGroup container, int position, Object object) {		super.destroyItem(container, position, object);	}}