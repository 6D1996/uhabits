/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.notifications;


import android.app.*;
import android.graphics.*;
import android.os.*;

import androidx.annotation.Nullable;

import android.text.format.*;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.FragmentActivity;

import com.android.datetimepicker.time.TimePickerDialog;

import org.isoron.uhabits.*;
import org.isoron.uhabits.core.models.*;
import org.isoron.uhabits.inject.*;
import org.isoron.uhabits.receivers.*;
import org.isoron.uhabits.utils.*;

import java.util.*;

import static android.content.ContentUris.parseId;

public class SnoozeDelayPickerActivity extends FragmentActivity
    implements AdapterView.OnItemClickListener
{
    private Habit habit;

    private ReminderController reminderController;

    @Nullable
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle bundle)
    {
        super.onCreate(bundle);
        if (getIntent() == null) finish();
        if (getIntent().getData() == null) finish();

        HabitsApplication app = (HabitsApplication) getApplicationContext();
        HabitsApplicationComponent appComponent = app.getComponent();
        reminderController = appComponent.getReminderController();
        habit = appComponent.getHabitList().getById(parseId(getIntent().getData()));
        if (habit == null) finish();

        int theme = R.style.Theme_AppCompat_Light_Dialog_Alert;
        dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, theme))
            .setTitle(R.string.select_snooze_delay)
            .setItems(R.array.snooze_picker_names, null)
            .create();

        dialog.getListView().setOnItemClickListener(this);
        dialog.setOnDismissListener(d -> finish());
        dialog.show();

        SystemUtils.unlockScreen(this);
    }

    private void showTimePicker()
    {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = TimePickerDialog.newInstance(
            (view, hour, minute) -> {
                reminderController.onSnoozeTimePicked(habit, hour, minute);
                finish();
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(this),
            Color.BLUE);
        dialog.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        int[] snoozeValues = getResources().getIntArray(R.array.snooze_picker_values);
        if (snoozeValues[position] >= 0)
        {
            reminderController.onSnoozeDelayPicked(habit, snoozeValues[position]);
            finish();
        }
        else showTimePicker();
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause()
    {
        if (dialog != null) dialog.dismiss();
        super.onPause();
    }
}
