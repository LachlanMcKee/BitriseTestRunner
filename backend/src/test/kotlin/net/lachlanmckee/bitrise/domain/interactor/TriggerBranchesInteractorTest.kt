package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.*
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.local.TestConfigDataSource
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.data.entity.Config
import net.lachlanmckee.bitrise.data.entity.ConfigModel
import net.lachlanmckee.bitrise.domain.mapper.BuildsMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TriggerBranchesInteractorTest {
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val buildsMapper: BuildsMapper = mockk()
    private val interactor = TriggerBranchesInteractor(
        bitriseDataSource,
        TestConfigDataSource(Config(
            configModel = ConfigModel(
                bitrise = ConfigModel.Bitrise(
                    appId = "",
                    testApkSourceWorkflow = "source-workflow",
                    testTriggerWorkflow = ""
                ),
                testData = mockk()
            ),
            secretProperties = mockk()
        )),
        buildsMapper
    )

    private val applicationCall: ApplicationCall = mockk()

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(bitriseDataSource, buildsMapper, applicationCall)
    }

    @Test
    fun givenGetBuildsSuccessAndMappingSuccess_whenExecute_thenRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds("source-workflow") } returns Result.success(emptyList())

        val buildsData = BuildsData(emptyList(), emptyMap())
        every { buildsMapper.mapBuilds(emptyList()) } returns buildsData

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds("source-workflow")
            buildsMapper.mapBuilds(emptyList())
            applicationCall.respond(buildsData)
        }
    }

    @Test
    fun givenGetBuildsSuccessAndMappingFailure_whenExecute_thenDoNotRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds("source-workflow") } returns Result.success(emptyList())
        every { buildsMapper.mapBuilds(emptyList()) } throws RuntimeException()

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds("source-workflow")
            buildsMapper.mapBuilds(emptyList())
        }
    }

    @Test
    fun givenGetBuildsFailure_whenExecute_thenDoNotRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds("source-workflow") } returns Result.failure(RuntimeException())

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds("source-workflow")
        }
    }
}