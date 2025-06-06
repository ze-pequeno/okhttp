/*
 * Copyright (C) 2022 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.survey.ssllabs

import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.survey.types.Client
import okhttp3.survey.types.SuiteId
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SslLabsClient(
  callFactory: Call.Factory,
) {
  private val moshi = Moshi.Builder().build()

  private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

  private val retrofit =
    Retrofit
      .Builder()
      .baseUrl(SslLabsApi.BASE_URL)
      .addConverterFactory(moshiConverterFactory)
      .callFactory(callFactory)
      .build()

  private val sslLabsApi = retrofit.create(SslLabsApi::class.java)

  suspend fun clients(): List<Client> =
    sslLabsApi.clients().map { userAgent ->
      Client(
        userAgent = userAgent.name,
        version = userAgent.version,
        platform = userAgent.platform,
        enabled = userAgent.suiteNames.map { SuiteId(null, it) },
      )
    }
}

suspend fun main() {
  val sslLabsClient =
    SslLabsClient(
      callFactory = OkHttpClient(),
    )

  for (client in sslLabsClient.clients()) {
    println(client)
  }
}
