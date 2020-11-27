/*
 * Copyright (C) 2016-2020 Álinson Santos Xavier <isoron@gmail.com>
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

package org.isoron.uhabits.sync

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

data class RegisterReponse(val key: String)
data class GetDataVersionResponse(val version: Long)

class RemoteSyncServer(
        private val baseURL: String = "https://sync.loophabits.org",
        private val httpClient: HttpClient = HttpClient(Android) {
            install(JsonFeature)
        }
) : AbstractSyncServer {

    override suspend fun register(): String = Dispatchers.IO {
        try {
            val response: RegisterReponse = httpClient.post("$baseURL/register")
            return@IO response.key
        } catch(e: ServerResponseException) {
            throw ServiceUnavailable()
        }
    }

    override suspend fun put(key: String, newData: SyncData) = Dispatchers.IO {
        try {
            val response: String = httpClient.put("$baseURL/db/$key") {
                header("Content-Type", "application/json")
                body = newData
            }
        } catch (e: ServerResponseException) {
            throw ServiceUnavailable()
        } catch (e: ClientRequestException) {
            throw KeyNotFoundException()
        }
    }

    override suspend fun getData(key: String): SyncData = Dispatchers.IO {
        try {
            val data: SyncData = httpClient.get("$baseURL/db/$key")
            return@IO data
        } catch (e: ServerResponseException) {
            throw ServiceUnavailable()
        } catch (e: ClientRequestException) {
            throw KeyNotFoundException()
        }
    }

    override suspend fun getDataVersion(key: String): Long = Dispatchers.IO {
        try {
            val response: GetDataVersionResponse = httpClient.get("$baseURL/db/$key/version")
            return@IO response.version
        } catch(e: ServerResponseException) {
            throw ServiceUnavailable()
        } catch (e: ClientRequestException) {
            throw KeyNotFoundException()
        }
    }
}
