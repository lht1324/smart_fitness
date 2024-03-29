package com.overeasy.smartfitness.model

sealed class RecordingState {
    data object Idle : RecordingState()
    data object OnRecord : RecordingState()
    data object Paused : RecordingState()
}