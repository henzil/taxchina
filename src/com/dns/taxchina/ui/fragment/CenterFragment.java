package com.dns.taxchina.ui.fragment;import android.os.Bundle;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.TextView;import com.dns.taxchina.R;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 学员中心 fragment */public class CenterFragment extends BaseFragment {	private TextView title, back;	@Override	protected void initData() {	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.center_fragment, container, false);		title = (TextView) view.findViewById(R.id.title_text);		title.setText("学员中心");		back = (TextView) view.findViewById(R.id.back_text);		back.setVisibility(View.GONE);		return view;	}	@Override	protected void initWidgetActions() {	}}