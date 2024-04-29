package cn.codebro.j_anime_android.ui.adapter

import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cn.codebro.j_anime_android.OpusActivity
import cn.codebro.j_anime_android.R
import cn.codebro.j_anime_android.databinding.LayoutOpusHomeItemBinding
import cn.codebro.j_anime_android.net.OPUS_COVER_URL
import cn.codebro.j_anime_android.pojo.OpusHomeVO
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * 番剧元素ViewHolder
 */
class OpusHomeViewHolder(private val binding: LayoutOpusHomeItemBinding) :
    ViewHolder(binding.root) {
    private var opusHomeVO: OpusHomeVO? = null
    fun bind(item: OpusHomeVO?) {
        item?.let {
            opusHomeVO = item
            val url = "${OPUS_COVER_URL}${item.coverUrl}"
            item.coverUrl?.let {
                Glide.with(itemView)
                    .asDrawable()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_launcher_background)
                    .load(url)
                    .centerCrop()
                    .into(binding.opusCoverImageView)
            }
            binding.opusTitleTextView.text = item.nameCn
            binding.opusLaunchStartTextView.text = item.launchStart ?: "暂未确定"
            binding.opusLaunchStartTextView.setTextColor(item.hasResource)
            binding.episodesTextView.text =
                itemView.context.getString(R.string.opus_home_item_episodes_text_tpl)
                    .format(item.episodes ?: "?")
        }
    }

}

/**
 * 番剧首页Paging数据适配器
 * 用于绑定元素及创建元素ViewHolder
 */
class OpusHomePagingDataAdapter :
    PagingDataAdapter<OpusHomeVO, OpusHomeViewHolder>(OpusHomeDiffer) {
    override fun onBindViewHolder(holder: OpusHomeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            if (item?.hasResource == 0) {
                Toast.makeText(it.context, "该番剧暂无片源，敬请期待！", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(it.context, OpusActivity::class.java)
            intent.putExtra("opusItemId", item?.id)
            it.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpusHomeViewHolder {
        val opusHomeItemBinding = LayoutOpusHomeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OpusHomeViewHolder(opusHomeItemBinding)
    }
}

/**
 * 番剧数据比较器
 * 用于比较两个数据是否为同一个数据
 */
object OpusHomeDiffer : DiffUtil.ItemCallback<OpusHomeVO>() {
    override fun areItemsTheSame(oldItem: OpusHomeVO, newItem: OpusHomeVO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OpusHomeVO, newItem: OpusHomeVO): Boolean {
        return oldItem == newItem
    }
}

/**
 * 番剧瀑布流元素外边距包装
 */
class RecyclerViewMarginDecoration(private var spanCount: Int, margin: Int) :
    ItemDecoration() {
    private val margin: Int
    private var orderPosition = 0
    private var isPullUp = false
    private var topPosition = -1
    private var childLayoutOuPosition = 0
    private var childLayoutJiPosition = 0

    init {
        this.margin = margin
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager
        if (manager is GridLayoutManager) {
            val spanSizeLookup = manager.spanSizeLookup
            val spanSize = spanSizeLookup.getSpanSize(parent.getChildAdapterPosition(view))
            if (spanSize == 1) {
                //找到第一个开始位置  判断第一个item是基数还是偶数
                if (parent.getChildLayoutPosition(view) % spanCount == 0 && topPosition == -1) {
                    topPosition = parent.getChildLayoutPosition(view)
                } else if (parent.getChildLayoutPosition(view) % spanCount != 0 && topPosition == -1) {
                    topPosition = parent.getChildLayoutPosition(view)
                }

                //此处parent.getChildLayoutPosition(view))会有一个正序和反序，对应上划和下划，需要区分，因为我们下边会根据当前item的postion做条件设置合适的magin
                isPullUp = parent.getChildLayoutPosition(view) > orderPosition
                orderPosition = parent.getChildLayoutPosition(view)

                //根据奇偶数来改变内部判断条件 区分是单纯的一个一行n个item，还是多个一行n个item
                if (topPosition % spanCount == 0) {
                    if (parent.getChildLayoutPosition(view) % spanCount == 0) {
                        outRect[margin, 0, margin / 2] = 0
                        childLayoutOuPosition = parent.getChildLayoutPosition(view)
                    } else {
                        //根据正反序找到这一行的最后一个item位置  加载示例：上拉 0 1  2 3  4 5  下拉： 3 2 1 0
                        val lastItem =
                            if (isPullUp) childLayoutOuPosition + spanCount - 1 else parent.getChildLayoutPosition(
                                view
                            )
                        if (parent.getChildLayoutPosition(view) == lastItem) {
                            outRect[margin / 2, 0, margin] = 0
                        } else {
                            outRect[margin / 2, 0, margin / 2] = 0
                        }
                    }
                } else {
                    if (parent.getChildLayoutPosition(view) % spanCount != 0) {
                        outRect[margin, 0, margin / 2] = 0
                        childLayoutJiPosition = parent.getChildLayoutPosition(view)
                    } else {
                        val lastItem =
                            if (isPullUp) childLayoutJiPosition + spanCount - 1 else parent.getChildLayoutPosition(
                                view
                            )
                        if (parent.getChildLayoutPosition(view) == lastItem) {
                            outRect[margin / 2, 0, margin] = 0
                        } else {
                            outRect[margin / 2, 0, margin / 2] = 0
                        }
                    }
                }
            }
        }
    }
}