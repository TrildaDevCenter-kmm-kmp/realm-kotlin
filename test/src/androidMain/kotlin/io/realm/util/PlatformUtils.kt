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

package io.realm.util

import android.annotation.SuppressLint
import java.io.File
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual object PlatformUtils {
    @SuppressLint("NewApi")
    @ExperimentalPathApi
    actual fun createTempDir(): String {
        return Files.createTempDirectory("android_tests").absolutePathString()
    }

    actual fun deleteTempDir(path: String) {
        File(path).deleteRecursively()
    }

    @OptIn(ExperimentalTime::class)
    actual fun sleep(duration: Duration) {
        Thread.sleep(duration.toLongMilliseconds())
    }

    actual fun threadId(): ULong = Thread.currentThread().id.toULong()
}
