package cn.codebro.j_anime_android.presenter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.net.OpusService
import cn.codebro.j_anime_android.pojo.OpusHomeDTO
import cn.codebro.j_anime_android.pojo.OpusHomeVO
import cn.codebro.j_anime_android.ui.home.IOpusHomeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpusHomePresenter(
    private val view: IOpusHomeView
) : PagingSource<Int, OpusHomeVO>() {
    private val opusService: OpusService = JAnimeApplication.apiManager!!.opusService()

    override fun getRefreshKey(state: PagingState<Int, OpusHomeVO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpusHomeVO> {
        return withContext(Dispatchers.IO) {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            try {
                val response = opusService.listByPage(OpusHomeDTO(page, pageSize)).execute()
                response.body()?.let {
                    if (it.code == 4001) {
                        view.notLogin()
                        return@withContext LoadResult.Error(IllegalStateException("登录状态已失效"))
                    } else if (it.code == 200 && it.data != null) {
                        val prevKey = if (page == 1) null else page - 1
                        val hasMore = page * pageSize >= it.data!!.total
                        val nextKey = if (hasMore) page + 1 else null
                        return@withContext LoadResult.Page(
                            it.data!!.records,
                            prevKey,
                            nextKey
                        )
                    }
                }
                return@withContext LoadResult.Invalid()
            } catch (e: Exception) {
                return@withContext LoadResult.Error(e)
            }
        }
    }

}