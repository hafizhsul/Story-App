package com.example.storyapp

import com.example.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                photoUrl = "photoUrl + $i",
                createdAt = "createAt = $i",
                name = "name + $i",
                description = "description + $i",
                lon = i.toDouble(),
                lat = i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}