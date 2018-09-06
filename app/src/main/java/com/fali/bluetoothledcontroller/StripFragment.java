package com.fali.bluetoothledcontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fali.bluetoothledcontroller.SerialMapping.SerialKeywords;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorSelectedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class StripFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_STRIP_NUMBER = "strip_number";
	MainActivity mActivity;
	
	private int previousColor = 0;
	private int mSpinnerCount=0;
	private int mSpinnerInitializedCount=0;
	
	public StripFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_dummy,
				container, false);
		//TextView dummyTextView = (TextView) rootView
		//		.findViewById(R.id.section_label);
		//dummyTextView.setText(Integer.toString(getArguments().getInt(
		//		ARG_SECTION_NUMBER)));
		
		mActivity = ((MainActivity)getActivity());

		SeekBar speedBar = (SeekBar)rootView.findViewById(R.id.seekBarSpeed);
		speedBar.setOnSeekBarChangeListener(seekBarSpeedChanged);
		
		ColorPicker picker = (ColorPicker)rootView.findViewById(R.id.colorPicker);
		
		picker.setOnColorSelectedListener(colorSelected);
		
		SVBar svBar = (SVBar)rootView.findViewById(R.id.sVBar1);
		OpacityBar opacityBar = (OpacityBar)rootView.findViewById(R.id.opacityBar1);
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
						
		ToggleButton reverseBtn = (ToggleButton)rootView.findViewById(R.id.toggleReverse);
		reverseBtn.setOnCheckedChangeListener(toggleReverseChanged);
		
		ToggleButton randomColorsBtn = (ToggleButton)rootView.findViewById(R.id.toggleRandomColors);
		randomColorsBtn.setOnCheckedChangeListener(toggleRandomColorsChanged);
		
		mSpinnerCount = 1;
		Spinner modeSelector = (Spinner)rootView.findViewById(R.id.modeSelector);
		fillModeList(modeSelector);
		modeSelector.setOnItemSelectedListener(modeSelectedItemChanged);
			
		return rootView;
	}
	
	private void fillModeList(Spinner spinner)
	{
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity,
		        R.array.modes_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}
	
	private SeekBar.OnSeekBarChangeListener seekBarSpeedChanged = new OnSeekBarChangeListener()
	{
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
					int value = seekBar.getProgress();
					mActivity.updateValue(getArguments().getInt(ARG_STRIP_NUMBER), SerialKeywords.WaitTime, value);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
					
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			TextView lblSpeedValue = (TextView)getView().findViewById(R.id.lblSpeedValue);
			lblSpeedValue.setText(Integer.toString(seekBar.getProgress()));	
		}
	};
	
	private ColorPicker.OnColorSelectedListener colorSelected = new OnColorSelectedListener() {
		
		@Override
		public void onColorSelected(int arduinocolor, int color) {
			if(previousColor != 0)
				((ColorPicker)getView().findViewById(R.id.colorPicker)).setOldCenterColor(previousColor);
			previousColor = color;
			
			mActivity.storeUpdateValue(SerialKeywords.Brightness, Color.alpha(color));
			mActivity.updateValue(getArguments().getInt(ARG_STRIP_NUMBER), SerialKeywords.Color, arduinocolor);
		}
	};
	
	private CompoundButton.OnCheckedChangeListener toggleReverseChanged = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActivity.updateValue(getArguments().getInt(ARG_STRIP_NUMBER), SerialKeywords.Reverse, isChecked);
			}
	};
	
	private CompoundButton.OnCheckedChangeListener toggleRandomColorsChanged = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActivity.updateValue(getArguments().getInt(ARG_STRIP_NUMBER), SerialKeywords.RandomColor, isChecked);
			}
	};
	
	private AdapterView.OnItemSelectedListener modeSelectedItemChanged = new OnItemSelectedListener() {
		@Override
	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			if(mSpinnerInitializedCount < mSpinnerCount)
			{
				mSpinnerInitializedCount++;
			}
			else
			{
				mActivity.updateValue(getArguments().getInt(ARG_STRIP_NUMBER), SerialKeywords.Mode, (int)id);
			}
	    }
	
	    @Override
	    public void onNothingSelected(AdapterView<?> parentView) {
	        // your code here
	    }
	};
}