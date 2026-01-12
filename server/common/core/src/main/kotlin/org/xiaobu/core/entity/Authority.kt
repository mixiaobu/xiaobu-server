package org.xiaobu.core.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Authority @JsonCreator constructor(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("description") var description: String,
    @JsonProperty("type") var type: String,
    @JsonProperty("sort") var sort: Int
)
