package org.techtown.kormate.domain.model

data class UserIntel(
    private val nation: String = "",
    private val major: String = "",
    private var selfIntro: String = "",
    private var gender: String = ""
)