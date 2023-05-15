package com.example.music.model

class Feedback() {

    private var email: String? = null
    private var comment: String? = null

    fun setFeedback(email: String?,comment: String?){
        this.email = email
        this.comment = comment
    }


    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getComment(): String? {
        return comment
    }

    fun setComment(comment: String?) {
        this.comment = comment
    }
}