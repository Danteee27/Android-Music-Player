package com.example.music.model

class Contact(private var id: Int, private var image: Int) {

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getImage(): Int {
        return image
    }

    fun setImage(image: Int) {
        this.image = image
    }

    companion object {
        const val FACEBOOK = 0
        const val HOTLINE = 1
        const val GMAIL = 2
        const val SKYPE = 3
        const val YOUTUBE = 4
        const val ZALO = 5
    }
}