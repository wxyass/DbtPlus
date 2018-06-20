package et.tsingtaopad.operation.working.fragment;


import et.tsingtaopad.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RemarkFragment extends Fragment {

	public static RemarkFragment getInstance() {
		RemarkFragment userFragment = new RemarkFragment();
		return userFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_remark, container, false);
	}

}
