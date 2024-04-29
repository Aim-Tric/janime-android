package cn.codebro.j_anime_android

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.databinding.ActivityOpusBinding
import cn.codebro.j_anime_android.pojo.MediaListType
import cn.codebro.j_anime_android.pojo.OpusMediaVO
import cn.codebro.j_anime_android.presenter.OpusPlayPresenter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


interface OpusView : IView {
    fun setOpusMedia(opusMedia: OpusMediaVO)

    fun loadMediaResource(
        userOpusId: String,
        url: String,
        episode: String,
        mediaTitle: String,
        isFollow: Int = 0,
        seekTo: Int = 0
    )

    fun getActiveEpisode(): String
}

class OpusActivity : BaseView(), OpusView {

    private lateinit var binding: ActivityOpusBinding
    private lateinit var opusPlayPresenter: OpusPlayPresenter

    private var videoPlayer: StandardGSYVideoPlayer? = null
    private var fullScreenPlayer: GSYBaseVideoPlayer? = null
    private var orientationUtils: OrientationUtils? = null
    private var adapter: EpisodeGridViewAdapter? = null
    private var activeEpisode: String? = null
    private var isPlay = false
    var isFollow: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMediaPlayer()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(0) {
                release()
            }
        }

        opusPlayPresenter = OpusPlayPresenter(this)
        val opusItemId = intent.getStringExtra("opusItemId")
        opusItemId?.let { opusPlayPresenter.loadOpusVideo(it) }
    }

    private fun initMediaPlayer() {
        videoPlayer = binding.opusPlayerView

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        videoPlayer!!.thumbImageView = imageView
        videoPlayer!!.titleTextView.visibility = View.VISIBLE
        videoPlayer!!.backButton.visibility = View.VISIBLE
        orientationUtils = OrientationUtils(this, videoPlayer)
        videoPlayer!!.setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                isPlay = false
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                if (orientationUtils != null) {
                    orientationUtils!!.backToProtVideo()
                }
                videoPlayer!!.playPosition = fullScreenPlayer!!.playPosition
                videoPlayer!!.titleTextView.text = fullScreenPlayer!!.titleTextView.text
                fullScreenPlayer!!.release()
                fullScreenPlayer = null
            }
        })
        videoPlayer!!.fullscreenButton.setOnClickListener {
            orientationUtils!!.resolveByClick()
            fullScreenPlayer = videoPlayer!!.startWindowFullscreen(this, true, true)
            fullScreenPlayer!!.playPosition = videoPlayer!!.playPosition
            fullScreenPlayer!!.titleTextView.text = videoPlayer!!.titleTextView.text
        }
        videoPlayer!!.setIsTouchWiget(true)
        videoPlayer!!.backButton.setOnClickListener { onBackPressed() }
        videoPlayer!!.isNeedOrientationUtils = true
    }


    override fun onPause() {
        videoPlayer!!.currentPlayer.onVideoPause();
        super.onPause()
    }

    override fun onResume() {
        videoPlayer!!.currentPlayer.onVideoResume(false);
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            videoPlayer!!.currentPlayer.release()
        }
        if (orientationUtils != null) orientationUtils!!.releaseListener()
    }

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        release()
        super.onBackPressed()
    }

    private fun release() {
        if (orientationUtils!!.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer!!.fullscreenButton.performClick();
            return;
        }
        videoPlayer!!.setVideoAllCallBack(null)
    }

    override fun setOpusMedia(opusMedia: OpusMediaVO) {
        binding.opusPlayerSummaryTextView.text = opusMedia.aniSummary
        binding.opusPlayerTitleTextView.text = opusMedia.nameCn
        binding.opusPlayingEpisodesTextView.text =
            this.getString(R.string.opus_player_playing_episodes_text_tpl)
                .format(opusMedia.readingNum.toString())
        binding.isFollowOpusTextView.text =
            this.getString(R.string.opus_player_is_follow_status_text_tpl)
                .format(if (opusMedia.isFollow == 1) "已追番" else "未追番")
        binding.readStatusTextView.text =
            this.getString(R.string.opus_player_is_follow_status_text_tpl)
                .format(
                    when (opusMedia.readStatus) {
                        0 -> "未看"
                        1 -> "在看"
                        else -> "已看"
                    }
                )
        isFollow = opusMedia.isFollow
        adapter = EpisodeGridViewAdapter(this, opusMedia.mediaList)
        binding.opusEpisodesChooserGridView.adapter = adapter
        binding.opusEpisodesChooserGridView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val episode = opusMedia.mediaList[position].episodes
                binding.opusPlayingEpisodesTextView.text =
                    this.getString(R.string.opus_player_playing_episodes_text_tpl)
                        .format(episode)
                opusPlayPresenter.switchOpusEpisode(
                    opusMedia.id,
                    opusMedia.userOpusId,
                    episode,
                    "${opusMedia.nameCn} $episode",
                    isFollow
                )
            }
    }

    override fun loadMediaResource(
        userOpusId: String,
        url: String,
        episode: String,
        mediaTitle: String,
        isFollow: Int,
        seekTo: Int
    ) {
        activeEpisode = episode
        adapter?.notifyDataSetChanged()
        val cookieMap: MutableMap<String, String> = mutableMapOf()
        url.toHttpUrlOrNull()?.let {
            JAnimeApplication.apiManager?.persistentCookieStore?.get(it)?.let { cookies ->
                val cookieString: StringBuilder = StringBuilder()
                cookies.forEach { cookie ->
                    cookieString.append(cookie.name).append("=").append(cookie.value).append(";")
                }
                cookieMap.put(
                    "Cookie",
                    cookieString.deleteCharAt(cookieString.length - 1).toString()
                )
            }
        }
        // 记录观看时间
        videoPlayer!!.setGSYVideoProgressListener { _, _, currentPosition, _ ->
            // 没有追番不执行
            if (isFollow == 0) return@setGSYVideoProgressListener
            // 每隔5s左右发送一次请求
            if (currentPosition / 1000 % 5 == 0L) {
                opusPlayPresenter.updateProgress(
                    userOpusId,
                    episode.toInt(),
                    (currentPosition / 1000).toInt()
                )
            }
        }
        videoPlayer!!.setUp(url, true, null, cookieMap, mediaTitle)
        videoPlayer!!.isStartAfterPrepared = false
        videoPlayer!!.seekOnStart = seekTo.toLong() * 1000
        videoPlayer!!.startPlayLogic()
    }

    override fun getActiveEpisode(): String = activeEpisode!!
}

class EpisodeGridViewAdapter(
    private val opusView: OpusView,
    private val episodes: List<MediaListType>
) :
    BaseAdapter() {
    override fun getCount(): Int = episodes.size

    override fun getItem(position: Int): Any = episodes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: EpisodeGridViewHolder? = null
        var view = convertView
        if (convertView == null) {
            val inflater: LayoutInflater = LayoutInflater.from(opusView.getContext())
            view = inflater.inflate(R.layout.layout_episodes_chooser_grid_item, parent, false)
            viewHolder = EpisodeGridViewHolder()

            viewHolder.episodeTextView = view.findViewById(R.id.episodeChooserTextView)

            view.tag = viewHolder
        } else {
            viewHolder = view!!.tag as EpisodeGridViewHolder
        }

        viewHolder.episodeTextView!!.text = episodes[position].episodes
        if (episodes[position].episodes == opusView.getActiveEpisode()) {
            viewHolder.episodeTextView!!.setTextColor(Color.parseColor("#FF47A3"))
        } else {
            viewHolder.episodeTextView!!.setTextColor(Color.parseColor("#000000"))
        }

        return view!!
    }

}

class EpisodeGridViewHolder {
    var episodeTextView: TextView? = null
}