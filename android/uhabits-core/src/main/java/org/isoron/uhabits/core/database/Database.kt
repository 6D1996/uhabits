/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
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
package org.isoron.uhabits.core.database

import java.io.*

interface Database {

    fun query(q: String, vararg params: String): Cursor

    fun query(q: String, callback: ProcessCallback) {
        query(q).use { c ->
            c.moveToNext()
            callback.process(c)
        }
    }

    fun update(
            tableName: String,
            values: Map<String, Any?>,
            where: String,
            vararg params: String,
    ): Int

    fun insert(tableName: String, values: Map<String, Any?>): Long?

    fun delete(tableName: String, where: String, vararg params: String)

    fun execute(query: String, vararg params: Any)

    fun beginTransaction()

    fun setTransactionSuccessful()

    fun endTransaction()

    fun close()

    val version: Int

    val file: File?

    interface ProcessCallback {
        fun process(cursor: Cursor)
    }
}