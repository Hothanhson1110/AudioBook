package com.example.app.domain.model

enum class VoiceGender {
    MALE, FEMALE;

    val displayName: String
        get() = when (this) {
            MALE -> "Nam"
            FEMALE -> "Nu"
        }
}

enum class VoiceAge {
    CHILD, TEEN, YOUNG, ADULT, MIDDLE, ELDERLY;

    val displayName: String
        get() = when (this) {
            CHILD -> "Tre em"
            TEEN -> "Thieu nien"
            YOUNG -> "Thanh nien"
            ADULT -> "Truong thanh"
            MIDDLE -> "Trung nien"
            ELDERLY -> "Nguoi gia"
        }
}

enum class VoiceStyle {
    NEUTRAL, SCIENCE, NOVEL, NEWS, COMEDY, ROMANCE,
    KNOWLEDGE, POETRY, DOCUMENTARY, STORY;

    val displayName: String
        get() = when (this) {
            NEUTRAL -> "Trung tinh"
            SCIENCE -> "Khoa hoc"
            NOVEL -> "Tieu thuyet"
            NEWS -> "Tin tuc"
            COMEDY -> "Hai kich"
            ROMANCE -> "Lang man"
            KNOWLEDGE -> "Kien thuc"
            POETRY -> "Tho ca"
            DOCUMENTARY -> "Tai lieu"
            STORY -> "Truyen"
        }

    val description: String
        get() = when (this) {
            NEUTRAL -> "Doc deu, khong nhan nha"
            SCIENCE -> "Cham rai, chinh xac, phu hop sach ky thuat"
            NOVEL -> "Bieu cam nhe, giu nhip doc muot"
            NEWS -> "Nhanh, ro rang, phu hop tin tuc"
            COMEDY -> "Nhan nha, vui tuoi"
            ROMANCE -> "Nhe nhang, tinh cam"
            KNOWLEDGE -> "Ro rang, de hieu"
            POETRY -> "Cham, ngu dieu, nhan manh cam xuc"
            DOCUMENTARY -> "Trang trong, phu hop tai lieu"
            STORY -> "Ke chuyen tu nhien"
        }
}

data class VoiceSettings(
    val gender: VoiceGender = VoiceGender.FEMALE,
    val age: VoiceAge = VoiceAge.YOUNG,
    val style: VoiceStyle = VoiceStyle.NEUTRAL,
    val localeTag: String = "vi-VN",
) {
    val summary: String
        get() = "${gender.displayName} - ${age.displayName} - ${style.displayName}"
}
