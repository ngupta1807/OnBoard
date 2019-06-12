package com.bookmyride.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.bookmyride.R;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment
{
	private static final int MAX_YEAR = 2099;
	private DatePickerDialog.OnDateSetListener listener;

	public void setListener(DatePickerDialog.OnDateSetListener listener)
	{
		this.listener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		Calendar cal = Calendar.getInstance();
		View dialog = inflater.inflate(R.layout.date_picker_dialog, null);
		final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
		final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
		monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		monthPicker.setMinValue(1);
		monthPicker.setMaxValue(12);
		monthPicker.setValue(cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);
		yearPicker.setMinValue(year);
		yearPicker.setMaxValue(MAX_YEAR);
		yearPicker.setValue(year);

		builder.setView(dialog)
		.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				MonthYearPickerDialog.this.getDialog().cancel();
			}
		});
		return builder.create();
	}
}