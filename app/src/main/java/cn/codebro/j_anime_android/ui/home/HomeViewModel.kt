package cn.codebro.j_anime_android.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import cn.codebro.j_anime_android.pojo.OpusHomeVO
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {

    var opusRecyclerViewFlow: Flow<PagingData<OpusHomeVO>>? = null
}