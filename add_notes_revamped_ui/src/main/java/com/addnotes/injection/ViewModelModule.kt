package com.addnotes.injection

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.addnotes.viewModel.CreateNotesViewModel
import com.addnotes.viewModel.HomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@InstallIn(SingletonComponent::class)
@Module
object ViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun homeViewModel(sharedPreferences: SharedPreferences): ViewModel {
        return HomeViewModel(sharedPreferences)
    }

    @Provides
    @IntoMap
    @ViewModelKey(CreateNotesViewModel::class)
    fun createNotesViewModel(sharedPreferences: SharedPreferences): ViewModel {
        return CreateNotesViewModel(sharedPreferences)
    }
}