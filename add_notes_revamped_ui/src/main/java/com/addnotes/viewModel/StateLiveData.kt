package com.addnotes.viewModel

import androidx.lifecycle.MutableLiveData

class StateLiveData<T> : MutableLiveData<StateData<T>>() {

    fun postLoading() {
        postValue(StateData<T>().loading())
    }

    fun postError(throwable: Throwable) {
        postValue(StateData<T>().error(throwable))
    }

    fun postSuccess(data: T) {
        postValue(StateData<T>().success(data))
    }
}