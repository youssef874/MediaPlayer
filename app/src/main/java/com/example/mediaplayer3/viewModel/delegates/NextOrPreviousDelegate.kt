package com.example.mediaplayer3.viewModel.delegates

import androidx.lifecycle.ViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NextOrPreviousDelegate <T>: ReadWriteProperty<ViewModel,INextOrPreviousItem<T>>, INextOrPreviousItem<T> {
    override fun getValue(thisRef: ViewModel, property: KProperty<*>): INextOrPreviousItem<T> {
        return this
    }

    override fun setValue(
        thisRef: ViewModel,
        property: KProperty<*>,
        value: INextOrPreviousItem<T>
    ) {
        //Do nothing
    }

    override fun nextItem(list: List<T>, currentItem: T, onNext: (next: T) -> Unit) {
        val currentIndex = list.indexOf(currentItem)
        val nextIndex = if (currentIndex + 1 <= list.size-1) currentIndex+1 else 0
        val nextItem = list[nextIndex]
        onNext(nextItem)
    }

    override fun previousItem(list: List<T>, currentItem: T, onPrevious: (previous: T) -> Unit) {
        val currentIndex = list.indexOf(currentItem)
        val previousIndex = if (currentIndex - 1 >= 0) currentIndex-1 else list.size-1
        val previousItem = list[previousIndex]
        onPrevious(previousItem)
    }
}