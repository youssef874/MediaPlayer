package com.example.mediaplayer3.viewModel.delegates

interface INextOrPreviousItem<T> {

    fun nextItem(
        list: List<T>,
        currentItem: T,
        isRandom: Boolean,
        onNext: (next: T)->Unit
    )

    fun previousItem(
        list: List<T>,
        currentItem: T,
        isRandom: Boolean,
        onPrevious: (previous: T)->Unit
    )
}