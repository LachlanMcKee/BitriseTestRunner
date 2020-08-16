package net.lachlanmckee.bitrise.domain.mapper

import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.local.TestConfigDataSource
import net.lachlanmckee.bitrise.data.entity.Config
import net.lachlanmckee.bitrise.data.entity.ConfigModel
import net.lachlanmckee.bitrise.domain.entity.PathWithAnnotationGroups
import net.lachlanmckee.bitrise.domain.entity.TestApkMetadata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TestApkMetadataMapperTest {
    private val emptyFlankConfig = ConfigModel.FlankConfig(
        commonYamlFiles = emptyList(),
        annotationBasedYaml = ConfigModel.FlankConfig.AnnotationBasedYaml(
            options = emptyList(),
            fallbackYamlFiles = emptyList()
        )
    )

    @Test
    fun givenNoTestMethods_whenMap_thenReturnEmptyMetadata() = runBlocking {
        val testData = ConfigModel.TestData(
            hiddenAnnotations = emptyList(),
            ignoreTestsWithAnnotations = emptyList(),
            allowTestingWithoutFilters = true,
            flankConfig = emptyFlankConfig,
            options = emptyList()
        )
        val metadata = testMapper(testData, emptyList())
        assertEquals(
            TestApkMetadata(
                rootPackage = "",
                annotations = emptyList(),
                packages = emptyList(),
                classes = emptyList()
            ),
            metadata
        )
    }

    @Test
    fun givenSingleTestMethod_whenMap_thenReturnMetadata() = runBlocking {
        val testData = ConfigModel.TestData(
            hiddenAnnotations = emptyList(),
            ignoreTestsWithAnnotations = emptyList(),
            allowTestingWithoutFilters = true,
            flankConfig = emptyFlankConfig,
            options = emptyList()
        )
        val metadata = testMapper(
            testData, listOf(
                TestMethod(
                    testName = "com.example.integration.TestClass1#test1",
                    annotations = emptyList()
                )
            )
        )
        assertEquals(
            TestApkMetadata(
                rootPackage = "com.example.integration",
                annotations = emptyList(),
                packages = listOf(
                    PathWithAnnotationGroups(
                        path = "",
                        annotationGroups = null
                    )
                ),
                classes = listOf(
                    PathWithAnnotationGroups(
                        path = "TestClass1",
                        annotationGroups = null
                    ),
                    PathWithAnnotationGroups(
                        path = "TestClass1#test1",
                        annotationGroups = null
                    )
                )
            ),
            metadata
        )
    }

    @Test
    fun givenMultipleTestMethods_whenMap_thenReturnMetadata() = runBlocking {
        val testData = ConfigModel.TestData(
            hiddenAnnotations = emptyList(),
            ignoreTestsWithAnnotations = emptyList(),
            allowTestingWithoutFilters = true,
            flankConfig = emptyFlankConfig,
            options = emptyList()
        )
        val metadata = testMapper(
            testData, listOf(
                TestMethod(
                    testName = "com.example.integration.path1.TestClass1#test1",
                    annotations = listOf(TestAnnotation(
                        name = "IntegrationAnnotation",
                        values = emptyMap(),
                        inherited = false
                    ))
                ),
                TestMethod(
                    testName = "com.example.e2e.path2.TestClass2#test2",
                    annotations = listOf(TestAnnotation(
                        name = "E2eAnnotation",
                        values = emptyMap(),
                        inherited = false
                    ))
                )
            )
        )
        assertEquals(
            TestApkMetadata(
                rootPackage = "com.example",
                annotations = listOf("E2eAnnotation", "IntegrationAnnotation"),
                packages = listOf(
                    PathWithAnnotationGroups(
                        path = "",
                        annotationGroups = listOf(
                            listOf("E2eAnnotation"),
                            listOf("IntegrationAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "e2e",
                        annotationGroups = listOf(
                            listOf("E2eAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "e2e.path2",
                        annotationGroups = listOf(
                            listOf("E2eAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "integration",
                        annotationGroups = listOf(
                            listOf("IntegrationAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "integration.path1",
                        annotationGroups = listOf(
                            listOf("IntegrationAnnotation")
                        )
                    )
                ),
                classes = listOf(
                    PathWithAnnotationGroups(
                        path = "e2e.path2.TestClass2",
                        annotationGroups = listOf(
                            listOf("E2eAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "e2e.path2.TestClass2#test2",
                        annotationGroups = listOf(
                            listOf("E2eAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "integration.path1.TestClass1",
                        annotationGroups = listOf(
                            listOf("IntegrationAnnotation")
                        )
                    ),
                    PathWithAnnotationGroups(
                        path = "integration.path1.TestClass1#test1",
                        annotationGroups = listOf(
                            listOf("IntegrationAnnotation")
                        )
                    )
                )
            ),
            metadata
        )
    }

    @Test
    fun givenSingleTestMethodWithIgnoredAnnotation_whenMap_thenReturnEmptyMetadata() = runBlocking {
        val testData = ConfigModel.TestData(
            hiddenAnnotations = emptyList(),
            ignoreTestsWithAnnotations = listOf("Ignore"),
            allowTestingWithoutFilters = true,
            flankConfig = emptyFlankConfig,
            options = emptyList()
        )
        val metadata = testMapper(
            testData, listOf(
                TestMethod(
                    testName = "com.example.integration.TestClass1#test1",
                    annotations = listOf(TestAnnotation(
                        name = "Ignore",
                        values = emptyMap(),
                        inherited = false
                    ))
                )
            )
        )
        assertEquals(
            TestApkMetadata(
                rootPackage = "",
                annotations = emptyList(),
                packages = emptyList(),
                classes = emptyList()
            ),
            metadata
        )
    }

    @Test
    fun givenSingleTestMethodWithHiddenAnnotation_whenMap_thenReturnEmptyMetadata() = runBlocking {
        val testData = ConfigModel.TestData(
            hiddenAnnotations = listOf("HiddenAnnotation"),
            ignoreTestsWithAnnotations = emptyList(),
            allowTestingWithoutFilters = true,
            flankConfig = emptyFlankConfig,
            options = emptyList()
        )
        val metadata = testMapper(
            testData, listOf(
                TestMethod(
                    testName = "com.example.integration.TestClass1#test1",
                    annotations = listOf(TestAnnotation(
                        name = "HiddenAnnotation",
                        values = emptyMap(),
                        inherited = false
                    ))
                )
            )
        )
        assertEquals(
            TestApkMetadata(
                rootPackage = "com.example.integration",
                annotations = emptyList(),
                packages = listOf(
                    PathWithAnnotationGroups(
                        path = "",
                        annotationGroups = null
                    )
                ),
                classes = listOf(
                    PathWithAnnotationGroups(
                        path = "TestClass1",
                        annotationGroups = null
                    ),
                    PathWithAnnotationGroups(
                        path = "TestClass1#test1",
                        annotationGroups = null
                    )
                )
            ),
            metadata
        )
    }

    private suspend fun testMapper(
        testData: ConfigModel.TestData,
        originalTestMethods: List<TestMethod>
    ): TestApkMetadata {
        return TestApkMetadataMapper(TestConfigDataSource(createConfig(testData)))
            .mapTestApkMetadata(originalTestMethods)
    }

    private fun createConfig(testData: ConfigModel.TestData): Config {
        return Config(
            configModel = ConfigModel(
                bitrise = ConfigModel.Bitrise(
                    appId = "",
                    testApkSourceWorkflow = "",
                    testTriggerWorkflow = ""
                ),
                testData = testData
            ),
            secretProperties = Properties().apply { put("bitriseToken", "unused") }
        )
    }

}
