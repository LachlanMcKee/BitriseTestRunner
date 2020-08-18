package net.lachlanmckee.bitrise.data.datasource.remote

import com.google.gson.JsonElement
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.lachlanmckee.bitrise.data.api.withTempFile
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.entity.BitriseTriggerRequest
import net.lachlanmckee.bitrise.data.entity.BitriseTriggerResponse
import net.lachlanmckee.bitrise.data.entity.BuildsResponse

interface BitriseService {
    suspend fun getBuilds(): Result<List<BuildsResponse.BuildData>>

    suspend fun getArtifactDetails(buildSlug: String): Result<JsonElement>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<JsonElement>

    suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>>

    suspend fun triggerWorkflow(
        branch: String,
        jobName: String,
        workflowId: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse>
}

class BitriseServiceImpl(
    private val client: HttpClient,
    private val configDataSource: ConfigDataSource
) : BitriseService {

    private suspend fun createAppUrl(): String {
        return "https://api.bitrise.io/v0.1/apps/${configDataSource.getConfig().bitrise.appId}"
    }

    override suspend fun getBuilds(): Result<List<BuildsResponse.BuildData>> = kotlin.runCatching {
        client
            .get<BuildsResponse>("${createAppUrl()}/builds?workflow=${configDataSource.getConfig().bitrise.testApkSourceWorkflow}&sort_by=created_at") {
                auth()
            }
            .data
    }

    override suspend fun getArtifactDetails(buildSlug: String): Result<JsonElement> = kotlin.runCatching {
        client
            .get<JsonElement>("${createAppUrl()}/builds/$buildSlug/artifacts") {
                auth()
            }
    }

    override suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<JsonElement> =
        kotlin.runCatching {
            client
                .get<JsonElement>("${createAppUrl()}/builds/$buildSlug/artifacts/$artifactSlug") {
                    auth()
                }
        }

    override suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>> = kotlin.runCatching {
        var startTime = System.currentTimeMillis()
        println("Download started")
        client.withTempFile(testApkUrl) {
            println("Download finished. Time: ${System.currentTimeMillis() - startTime}")

            startTime = System.currentTimeMillis()
            DexParser.findTestMethods(it.absolutePath).also {
                println("Parsing finished. Time: ${System.currentTimeMillis() - startTime}")
            }
        }
    }

    override suspend fun triggerWorkflow(
        branch: String,
        jobName: String,
        workflowId: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse> = kotlin.runCatching {
        client.post<BitriseTriggerResponse> {
            url("${createAppUrl()}/builds")
            contentType(ContentType.Application.Json)
            auth()
            body = BitriseTriggerRequest(
                buildParams = BitriseTriggerRequest.BuildParams(
                    environments = listOf(
                        BitriseTriggerRequest.BuildParams.EnvironmentValue(
                            mappedTo = "FLANK_CONFIG",
                            value = flankConfigBase64
                        ),
                        BitriseTriggerRequest.BuildParams.EnvironmentValue(
                            mappedTo = "JOB_NAME",
                            value = jobName
                        )
                    ),
                    branch = branch,
                    workflowId = workflowId
                )
            )
        }
    }

    private suspend fun HttpRequestBuilder.auth() {
        header("Authorization", configDataSource.getConfig().bitriseToken)
    }
}
