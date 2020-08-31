package net.lachlanmckee.bitrise.core.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse
import javax.inject.Inject

internal class BuildsMapperImpl @Inject constructor() : BuildsMapper {
    override fun mapBuilds(data: List<BuildsResponse.BuildData>): BuildsData {
        return BuildsData(
            branches = data
                .map { it.branch }
                .distinct(),

            branchBuilds = data
                .sortedByDescending { it.buildNumber }
                .groupBy(
                    keySelector = { it.branch },
                    valueTransform = {
                        BuildsData.Build(
                            status = it.statusText,
                            commitHash = it.commitHash,
                            commitMessage = it.commitMessage
                                ?.split("\n")
                                ?.firstOrNull()
                                ?.let { message ->
                                    if (message.length > 50) {
                                        message.take(50) + "..."
                                    } else {
                                        message
                                    }
                                },
                            buildNumber = it.buildNumber,
                            buildSlug = it.slug
                        )
                    }
                )
        )
    }
}
