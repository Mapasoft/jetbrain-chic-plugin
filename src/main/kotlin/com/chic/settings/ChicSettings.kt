package com.chic.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@State(
    name = "com.chic.settings.ChicSettings",
    storages = [Storage("chic.xml")]
)
@Service(Service.Level.PROJECT)
class ChicSettings : PersistentStateComponent<ChicSettings.State> {

    data class State(var compilerPath: String = "")

    private var state = State()

    override fun getState(): State = state

    override fun loadState(newState: State) {
        state = newState
    }

    var compilerPath: String
        get() = state.compilerPath
        set(value) { state.compilerPath = value }

    companion object {
        fun getInstance(project: Project): ChicSettings = project.service()
    }
}
