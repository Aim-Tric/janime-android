package cn.codebro.j_anime_android.presenter

import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.OpusView
import cn.codebro.j_anime_android.net.ApiCallback
import cn.codebro.j_anime_android.net.OpusService
import cn.codebro.j_anime_android.net.toOpusMediaUrl
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.OpusMediaVO
import cn.codebro.j_anime_android.pojo.OpusUpdateProgressDTO

class OpusPlayPresenter(private val view: OpusView) {
    private val opusService: OpusService = JAnimeApplication.apiManager!!.opusService()
    fun loadOpusVideo(opusId: String) {
        opusService.getOpusMedia(opusId).enqueue(GetOpusMediaCallback(view))
    }

    fun switchOpusEpisode(
        opusId: String,
        userOpusId: String,
        episode: String,
        mediaTitle: String,
        isFollow: Int = 0,
        mediaType: String = "mp4"
    ) {
        val mediaUrl = toOpusMediaUrl(opusId, episode, mediaType)
        view.loadMediaResource(
            userOpusId,
            mediaUrl,
            episode,
            mediaTitle,
            isFollow
        )
    }

    fun updateProgress(userOpusId: String, readingNum: Int, readingTime: Int) {
        opusService.updateProgress(OpusUpdateProgressDTO(userOpusId, readingNum, readingTime))
            .enqueue(UpdateProgressCallback(view))
    }

    inner class GetOpusMediaCallback(override val view: OpusView) :
        ApiCallback<OpusMediaVO?>(view) {
        override fun onResponseSuccess(response: ApiResponse<OpusMediaVO?>) {
            response.data?.let {
                view.setOpusMedia(it)
                var mediaIndex = 0
                if (it.readingNum > 0) {
                    it.mediaList.forEachIndexed { i, type ->
                        if (type.episodes.toInt() == it.readingNum) {
                            mediaIndex = i
                            return@forEachIndexed
                        }
                    }
                }
                val media = it.mediaList[mediaIndex]
                val mediaUrl = toOpusMediaUrl(it.id, media.episodes, media.mediaType)
                println(it.isFollow)
                view.loadMediaResource(
                    it.userOpusId,
                    mediaUrl,
                    media.episodes,
                    "${it.nameCn} ${media.episodes}",
                    it.isFollow,
                    it.readingTime.toInt()
                )
            }
        }
    }

    inner class UpdateProgressCallback(override val view: OpusView) : ApiCallback<Boolean?>(view) {
        override fun onResponseSuccess(response: ApiResponse<Boolean?>) {
            if (response.data == false) {
                view.showToast("网络好像开小差了~无法更新观看记录!")
            }
        }

    }
}