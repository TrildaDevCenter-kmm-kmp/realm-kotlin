/*
 * Copyright 2021 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.mongodb.internal

import io.ktor.client.features.logging.Logger
import io.realm.LogConfiguration
import io.realm.internal.RealmLog
import io.realm.internal.interop.NativePointer
import io.realm.internal.interop.RealmInterop
import io.realm.internal.interop.sync.MetadataMode
import io.realm.internal.interop.sync.NetworkTransport
import io.realm.internal.platform.freeze
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.AppConfiguration.Companion.DEFAULT_BASE_URL
import kotlinx.coroutines.CoroutineDispatcher

internal class AppConfigurationImpl(
    override val appId: String,
    override val baseUrl: String = DEFAULT_BASE_URL,
    override val networkTransportDispatcher: CoroutineDispatcher,
    override val metadataMode: MetadataMode = MetadataMode.RLM_SYNC_CLIENT_METADATA_MODE_PLAINTEXT,
    logConfig: LogConfiguration,
) : AppConfiguration {

    val log: RealmLog = RealmLog(configuration = logConfig)

    private val networkTransport: NetworkTransport = KtorNetworkTransport(
        // FIXME Add AppConfiguration.Builder option to set timeout as a Duration with default \
        //  constant in AppConfiguration.Companion
        //  https://github.com/realm/realm-kotlin/issues/408
        timeoutMs = 5000,
        dispatcher = networkTransportDispatcher,
        logger = object : Logger {
            override fun log(message: String) {
                this@AppConfigurationImpl.log.debug(message)
            }
        }
    )

    // Only freeze anything after all properties are setup as this triggers freezing the actual
    // AppConfigurationImpl instance itself
    val nativePointer: NativePointer = RealmInterop.realm_app_config_new(
        appId = appId,
        baseUrl = baseUrl,
        networkTransport = RealmInterop.realm_network_transport_new(networkTransport)
    ).freeze()
}
