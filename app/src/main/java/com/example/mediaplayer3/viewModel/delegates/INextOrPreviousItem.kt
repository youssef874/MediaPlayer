package com.example.mediaplayer3.viewModel.delegates

interface INextOrPreviousItem<T> {

    fun nextItem(
        list: List<T>,
        currentItem: T,
        onNext: (next: T)->Unit
    )

    fun previousItem(
        list: List<T>,
        currentItem: T,
        onPrevious: (previous: T)->Unit
    )
}