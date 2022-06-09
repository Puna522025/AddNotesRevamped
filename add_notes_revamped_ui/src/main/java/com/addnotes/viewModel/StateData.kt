package com.addnotes.viewModel

import androidx.annotation.NonNull
import androidx.annotation.Nullable

class StateData<T>() {

    private lateinit var mDataStatus: DataStatus

    @Nullable
    private var mData: T?

    @Nullable
    private var mThrowable: Throwable?

    init {
        mDataStatus = DataStatus.CREATED
        mData = null
        mThrowable = null
    }

    fun loading(): StateData<T> {
        mDataStatus = DataStatus.LOADING
        mData = null
        mThrowable = null
        return this
    }

    fun success(data: T): StateData<T> {
        mDataStatus = DataStatus.SUCCESS
        mData = data
        mThrowable = null
        return this
    }

    fun error(error: Throwable): StateData<T> {
        mDataStatus = DataStatus.ERROR
        mData = null
        mThrowable = error
        return this
    }

    @NonNull
    fun getStatus(): DataStatus? {
        return mDataStatus
    }

    @Nullable
    fun getData(): T? {
        return mData
    }

    @Nullable
    fun getError(): Throwable? {
        return mThrowable
    }

    enum class DataStatus {
        CREATED, SUCCESS, ERROR, LOADING, VALIDATE, COMPLETE
    }
}