package net.lachlanmckee.flankci.results.domain.mapper

import gsonpath.GsonSafeList
import net.lachlanmckee.flankci.core.data.entity.generic.BuildDataResponse
import net.lachlanmckee.flankci.core.data.entity.generic.EnvironmentValueResponse
import net.lachlanmckee.flankci.results.domain.entity.TestResultModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TestResultsListMapperTest {
  @Test
  fun givenNoBuildData_whenMap_thenExpectEmptyListResultModels() {
    testMapBuilds(emptyList(), emptyList())
  }

  @Test
  fun givenBuildDataWithoutJobName_whenMap_thenExpectListResultModelsWithoutJobName() {
    testMapBuilds(
      listOf(
        BuildDataResponse(
          branch = "branch",
          statusText = "statusText",
          commitHash = "commitHash",
          commitMessage = "commitMessage",
          buildNumber = 1,
          slug = "slug",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>().apply {
            add(EnvironmentValueResponse("ENV1", "VALUE1"))
          }
        )
      ),
      listOf(
        TestResultModel(
          branch = "branch",
          status = "statusText",
          commitHash = "commitHash",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          buildSlug = "slug",
          jobName = null,
          ciUrl = "https://app.bitrise.io/build/slug"
        )
      )
    )
  }

  @Test
  fun givenBuildDataWithJobName_whenMap_thenExpectListResultModelsWithJobName() {
    testMapBuilds(
      listOf(
        BuildDataResponse(
          branch = "branch",
          statusText = "statusText",
          commitHash = "commitHash",
          commitMessage = "commitMessage",
          buildNumber = 1,
          slug = "slug",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>().apply {
            add(EnvironmentValueResponse("JOB_NAME", "JOB1"))
          }
        )
      ),
      listOf(
        TestResultModel(
          branch = "branch",
          status = "statusText",
          commitHash = "commitHash",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          buildSlug = "slug",
          jobName = "JOB1",
          ciUrl = "https://app.bitrise.io/build/slug"
        )
      )
    )
  }

  @Test
  fun givenBuildDataWithoutEnvironmentValues_whenMap_thenExpectListResultModelsWithoutJobName() {
    testMapBuilds(
      listOf(
        BuildDataResponse(
          branch = "branch",
          statusText = "statusText",
          commitHash = "commitHash",
          commitMessage = "commitMessage",
          buildNumber = 1,
          slug = "slug",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          originalEnvironmentValueList = null
        )
      ),
      listOf(
        TestResultModel(
          branch = "branch",
          status = "statusText",
          commitHash = "commitHash",
          triggeredAt = "triggeredAt",
          finishedAt = "finishedAt",
          buildSlug = "slug",
          jobName = null,
          ciUrl = "https://app.bitrise.io/build/slug"
        )
      )
    )
  }

  private fun testMapBuilds(data: List<BuildDataResponse>, expected: List<TestResultModel>) {
    assertEquals(expected, TestResultsListMapper().mapToTestResultsList(data))
  }
}
