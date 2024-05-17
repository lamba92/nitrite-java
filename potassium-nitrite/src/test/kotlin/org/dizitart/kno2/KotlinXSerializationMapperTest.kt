/*
 * Copyright (c) 2017-2020. Nitrite author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dizitart.kno2

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.serialization.DocumentFormat
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.kno2.serialization.decodeFromDocument
import org.dizitart.kno2.serialization.encodeToDocument
import org.dizitart.no2.exceptions.ValidationException
import org.dizitart.no2.mvstore.MVStoreModule
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 *
 * @author Joris Jensen
 */
class KotlinXSerializationMapperTest {
    private val log = LoggerFactory.getLogger(KotlinXSerializationMapperTest::class.java)
    private val dbPath = getRandomTempDbFile()

    @Serializable
    private data class TestData(
        val polymorphType: SomePolymorphType,
        val someMap: Map<String, String>,
        val someSerializableObjectMap: Map<InnerObject, SomePolymorphType>,
        val innerObject: InnerObject,
        @SerialName("_id") val id: String? = null,
        val valueClass: SomeValueClass,
        val someInt: Int,
        val someDouble: Double,
        val nullable: String?,
        val enum: SomeEnum,
        val someList: List<TestData>,
        val someArray: Array<String>,
    ) {

        @JvmInline
        @Serializable
        value class SomeValueClass(val s: String)

        @Serializable
        enum class SomeEnum {
            SomeValue,
        }

        @Serializable
        data class InnerObject(val someValue: String)

        @Serializable
        sealed class SomePolymorphType(val value: String) {
            @Serializable
            data object SomeTypeA : SomePolymorphType("Type A")

            @Serializable
            data class SomeTypeB(val someValue: String) : SomePolymorphType("Type B")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestData

            if (polymorphType != other.polymorphType) return false
            if (someMap != other.someMap) return false
            if (innerObject != other.innerObject) return false
            if (id != other.id) return false
            if (someInt != other.someInt) return false
            if (someDouble != other.someDouble) return false
            if (nullable != other.nullable) return false
            if (enum != other.enum) return false
            if (someList != other.someList) return false
            return someArray.contentEquals(other.someArray)
        }

        override fun hashCode(): Int {
            var result = polymorphType.hashCode()
            result = 31 * result + someMap.hashCode()
            result = 31 * result + innerObject.hashCode()
            result = 31 * result + id.hashCode()
            result = 31 * result + someInt
            result = 31 * result + someDouble.hashCode()
            result = 31 * result + (nullable?.hashCode() ?: 0)
            result = 31 * result + enum.hashCode()
            result = 31 * result + someList.hashCode()
            result = 31 * result + someArray.contentHashCode()
            return result
        }
    }

    private val testData = TestData(
        polymorphType = TestData.SomePolymorphType.SomeTypeA,
        someMap = mapOf("testkey" to "testvalue", "test1" to "test2"),
        someSerializableObjectMap = mapOf(TestData.InnerObject("test") to TestData.SomePolymorphType.SomeTypeA),
        innerObject = TestData.InnerObject("someValue"),
        id = Random.nextLong().toString(),
        valueClass = TestData.SomeValueClass("someString"),
        someInt = 1,
        someDouble = 1.0,
        nullable = null,
        enum = TestData.SomeEnum.SomeValue,
        someList = listOf(
            TestData(
                polymorphType = TestData.SomePolymorphType.SomeTypeB("someValue"),
                someMap = emptyMap(),
                someSerializableObjectMap = emptyMap(),
                innerObject = TestData.InnerObject(""),
                id = null,
                valueClass = TestData.SomeValueClass("someString"),
                someInt = 1,
                someDouble = 1.0,
                nullable = "test",
                enum = TestData.SomeEnum.SomeValue,
                someList = emptyList(),
                someArray = emptyArray(),
            ),
            TestData(
                polymorphType = TestData.SomePolymorphType.SomeTypeB("someValue"),
                someMap = emptyMap(),
                someSerializableObjectMap = emptyMap(),
                innerObject = TestData.InnerObject(""),
                id = null,
                valueClass = TestData.SomeValueClass("someString"),
                someInt = 1,
                someDouble = 1.0,
                nullable = "test",
                enum = TestData.SomeEnum.SomeValue,
                someList = emptyList(),
                someArray = emptyArray(),
            ),
        ),
        someArray = arrayOf("someArrayData")
    )

    @Test
    fun testModule() {
        val documentFormat = DocumentFormat { allowStructuredMapKeys = true }
        val db = nitrite {
            validateRepositories = false
            loadModule(MVStoreModule(dbPath))
            loadModule(KotlinXSerializationMapper(documentFormat))
        }

        val repo = db.getRepository<TestData>()
        repo.insert(testData)
        repo.find { a -> a.second.get("_id") == testData.id }
            .firstOrNull()
            .also { assertEquals(it, testData) }
        db.close()
        Path(dbPath).deleteIfExists()
    }

    @Test
    fun testMapping() {
        val documentFormat = DocumentFormat { allowStructuredMapKeys = true }
        val document = documentFormat.encodeToDocument(testData)
        val decodedObject = documentFormat.decodeFromDocument<TestData>(document)
        assertSame(testData.someArray.size, decodedObject.someArray.size)
        testData.someArray.forEachIndexed { index, s -> assertEquals(decodedObject.someArray[index], s) }
        assertEquals(testData, decodedObject.copy(someArray = testData.someArray))
    }

    @Test(expected = ValidationException::class)
    fun testRepositoryValidationFailsWithKotlinx() {
        val db = nitrite {
            loadModule(MVStoreModule(dbPath))
            loadModule(KotlinXSerializationMapper)
        }

        val repo = db.getRepository<CacheEntry>()
        repo.insert(CacheEntry("sha256"))
        repo.find(CacheEntry::sha256 eq "sha256")
            .firstOrNull()
            .also { assertEquals(it?.sha256, "sha256") }
        db.close()
        Path(dbPath).deleteIfExists()
    }

    @Test
    fun testRepositoryValidationDisabled() {
        val db = nitrite {
            validateRepositories = false
            loadModule(MVStoreModule(dbPath))
            loadModule(KotlinXSerializationMapper)
        }

        val repo = db.getRepository<CacheEntry>()
        repo.insert(CacheEntry("sha256", Clock.System.now()))
        repo.find(CacheEntry::sha256 eq "sha256")
            .firstOrNull()
            .also { assertEquals(it?.sha256, "sha256") }
        db.close()
        Path(dbPath).deleteIfExists()
    }
}

@Serializable
data class CacheEntry(
    val sha256: String,
    val lastUpdated: Instant = Clock.System.now(),
)