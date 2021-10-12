package com.masai.myjournalapp.adapter


interface OnTaskItemClicked {



    fun onEditClicked(task: String)

    fun onDeleteClicked(task: String)
}