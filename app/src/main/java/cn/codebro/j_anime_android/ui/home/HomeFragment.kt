package cn.codebro.j_anime_android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.codebro.j_anime_android.MainActivity
import cn.codebro.j_anime_android.SearchActivity
import cn.codebro.j_anime_android.databinding.FragmentHomeBinding
import cn.codebro.j_anime_android.core.BaseFragment
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.presenter.OpusHomePresenter
import cn.codebro.j_anime_android.ui.adapter.OpusHomePagingDataAdapter
import cn.codebro.j_anime_android.ui.adapter.RecyclerViewMarginDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface IOpusHomeView : IView {
}

class HomeFragment : BaseFragment(), IOpusHomeView {
    private val spanCount = 2
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var homeViewModel: HomeViewModel? = null
    private var adapter: OpusHomePagingDataAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel!!.opusRecyclerViewFlow = Pager(
            PagingConfig(
                pageSize = 40
            )
        ) {
            OpusHomePresenter(this@HomeFragment)!!
        }.flow.cachedIn(homeViewModel!!.viewModelScope)

        adapter = OpusHomePagingDataAdapter()
        binding.opusHomeRecyclerView.adapter = adapter
        binding.opusHomeRecyclerView.layoutManager =
            StaggeredGridLayoutManager(spanCount, LinearLayout.VERTICAL)
        binding.opusHomeRecyclerView.addItemDecoration(
            RecyclerViewMarginDecoration(spanCount, 2)
        )
        binding.opusHomeRecyclerView.itemAnimator = DefaultItemAnimator()
        loadHomeOpus()

        binding.searchKeyWordPlainText.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    private fun loadHomeOpus() {
        lifecycleScope.launch {
            homeViewModel?.opusRecyclerViewFlow?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadHomeOpus()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadHomeOpus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}