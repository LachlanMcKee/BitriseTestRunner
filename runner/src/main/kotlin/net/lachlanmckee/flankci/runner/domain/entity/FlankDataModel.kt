package net.lachlanmckee.flankci.runner.domain.entity

internal data class FlankDataModel(
  val isRerun: Boolean,
  val branch: String,
  val buildSlug: String,
  val commitHash: String,
  val rootPackage: String,
  val annotations: List<String>,
  val packages: List<String>,
  val classes: List<String>,
  val fullClasses: List<String>,
  val checkboxOptions: Map<Int, Boolean>,
  val dropDownOptions: Map<Int, Int>
)
