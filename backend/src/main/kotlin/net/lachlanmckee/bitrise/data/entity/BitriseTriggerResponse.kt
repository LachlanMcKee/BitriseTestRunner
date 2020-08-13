package net.lachlanmckee.bitrise.data.entity

import com.google.gson.FieldNamingPolicy
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseTriggerResponse(
    val status: String,
    val buildUrl: String
)
