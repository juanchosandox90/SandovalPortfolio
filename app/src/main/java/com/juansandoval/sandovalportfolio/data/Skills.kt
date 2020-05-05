package com.juansandoval.sandovalportfolio.data

class Skills() {
    var skill_title: String? = null
    var skill_percentage: String? = null
    var subskill_title: String? = null
    var subskill_title2: String? = null

    constructor(
        skill_title: String,
        skill_percentage: String,
        subskill_title: String,
        subskill_title2: String
    ) : this() {
        this.skill_title = skill_title
        this.skill_percentage = skill_percentage
        this.subskill_title = subskill_title
        this.subskill_title2 = subskill_title2
    }
}