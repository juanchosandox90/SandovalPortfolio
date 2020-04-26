package com.juansandoval.sandovalportfolio.data

class Work() {
    var company_title: String? = null
    var end_date: String? = null
    var start_date: String? = null
    var work_description: String? = null
    var work_image: String? = null
    var work_role: String? = null

    constructor(
        company_title: String,
        end_date: String,
        start_date: String,
        work_description: String,
        work_image: String,
        work_role: String
    ) : this() {
        this.company_title = company_title
        this.end_date = end_date
        this.start_date = start_date
        this.work_description = work_description
        this.work_image = work_image
        this.work_role = work_role
    }
}