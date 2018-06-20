package et.tsingtaopad;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseFragmentSupport extends Fragment {
	

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = getActivity().getCurrentFocus();
			if (currentFocus != null) {
				IBinder windowToken = currentFocus.getWindowToken();
				imm.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	public boolean allowBackPressed() {
		FragmentManager fm = getChildFragmentManager();
		//从fragment管理器中得到Fragment当前已加入Fragment回退栈中的fragment的数量。
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
			return true;
		}
		return false;
	}
}
