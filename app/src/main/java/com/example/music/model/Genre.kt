package com.example.music.model

class Genre() {

    private var name: String? = null
    private var color: String? = null

    fun setName(name:String?)
    {
        this.name = name
    }

    fun getName() : String?{
        return name
    }

    fun setColor(colorName:String?)
    {
        this.color = colorName
    }

    fun getColor() : String?{
        return color
    }

    override fun toString(): String {
        return name?:""
    }

}