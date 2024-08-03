package cn.codebro.j_anime_android.state

object ApplicationState {
    var server: String? = null
    var username: String? = null
    var token: String? = null

    fun refresh(appState: ApplicationState) {
        server = appState.server
        username = appState.username
        token = appState.token
    }

    fun clear() {
        server = null
        username = null
        token = null
    }



}