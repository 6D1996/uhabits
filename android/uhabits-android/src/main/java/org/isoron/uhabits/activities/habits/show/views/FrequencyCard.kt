/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
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
package org.isoron.uhabits.activities.habits.show.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.databinding.ShowHabitFrequencyBinding
import org.isoron.uhabits.utils.toThemedAndroidColor
import java.util.HashMap

data class FrequencyCardViewModel(
    val frequency: HashMap<Timestamp, Array<Int>>,
    val firstWeekday: Int,
    val color: PaletteColor,
)

class FrequencyCard(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var binding = ShowHabitFrequencyBinding.inflate(LayoutInflater.from(context), this)

    fun update(data: FrequencyCardViewModel) {
        val androidColor = data.color.toThemedAndroidColor(context)
        binding.frequencyChart.setFrequency(data.frequency)
        binding.frequencyChart.setFirstWeekday(data.firstWeekday)
        binding.title.setTextColor(androidColor)
        binding.frequencyChart.setColor(androidColor)
    }
}

class FrequencyCardPresenter(
    val habit: Habit,
    val firstWeekday: Int,
) {
    fun present() = FrequencyCardViewModel(
        color = habit.color,
        frequency = habit.originalEntries.computeWeekdayFrequency(
            isNumerical = habit.isNumerical
        ),
        firstWeekday = firstWeekday,
    )
}
