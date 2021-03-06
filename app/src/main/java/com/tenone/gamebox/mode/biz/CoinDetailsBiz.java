package com.tenone.gamebox.mode.biz;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.tenone.gamebox.mode.able.MyMessageAble;
import com.tenone.gamebox.view.fragment.GoldDetailsFragment;
import com.tenone.gamebox.view.fragment.PlatformFragment;

import java.util.ArrayList;
import java.util.List;

public class CoinDetailsBiz implements MyMessageAble {

	@Override
	public List<Fragment> getFragments() {
		List<Fragment> fragments = new ArrayList<Fragment>();
		GoldDetailsFragment fragment = new GoldDetailsFragment();
		PlatformFragment fragment1 = new PlatformFragment();
		fragments.add(fragment);
		fragments.add(fragment1);
		return fragments;
	}

	@Override
	public List<String> getTitles(Context cxt, int rId) {
		List<String> titles = new ArrayList<String>();
		String[] array = cxt.getResources().getStringArray(rId);
		if (array != null) {
			for (String string : array) {
				titles.add(string);
			}
		}
		array = null;
		return titles;
	}
}
