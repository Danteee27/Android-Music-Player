package com.example.music.model

import java.io.Serializable

class Song : Serializable {

    private var id = 0
    private var title: String? = null
    private var image: String? = null
    private var url: String? = null
    private var artist: String? = null
    private var latest = false
    private var featured = false
    private var count = 0
    private var isPlaying = false
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
    }

    fun getArtist(): String? {
        return artist
    }

    fun setArtist(artist: String?) {
        this.artist = artist
    }

    fun isLatest(): Boolean {
        return latest
    }

    fun setLatest(latest: Boolean) {
        this.latest = latest
    }

    fun isFeatured(): Boolean {
        return featured
    }

    fun setFeatured(featured: Boolean) {
        this.featured = featured
    }

    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
    }
}