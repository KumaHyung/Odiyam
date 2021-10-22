package com.soapclient.place.view.location
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.soapclient.place.databinding.FragmentSearchBinding
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.ext.popBackStack
import com.soapclient.place.view.adapter.PlacePagingAdapter
import com.soapclient.place.view.adapter.PlaceHistoryAdapter
import com.soapclient.place.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var searchJob: Job? = null
    private val viewModel: LocationViewModel by viewModels({requireActivity()})
    val placePageAdapter: PlacePagingAdapter by lazy {
        PlacePagingAdapter {
            lifecycleScope.launch {
                saveLocationHistory(it)
                setSelectedPlace(it)
                hideSoftKeyboard(binding.search)
                popBackStack()
            }
        }
    }

    val placeHistoryAdapter: PlaceHistoryAdapter by lazy {
        PlaceHistoryAdapter { location, delete ->
            historyItemClicked(location, delete)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root

        binding.search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(seq: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(seq: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(arg: Editable?) {
                arg?.let {
                    if (it.isEmpty()) {
                        binding.searchList.adapter = placeHistoryAdapter
                    } else {
                        binding.searchList.adapter = placePageAdapter
                    }
                } ?: run {
                    binding.searchList.adapter = placeHistoryAdapter
                }
            }
        })
        setListAdapter()
        observeSearchHistoryList()

        val longitude = arguments?.getString("longitude") ?: "0.0"
        val latitude = arguments?.getString("latitude") ?: "0.0"
        initSearchButton(longitude, latitude)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocusToSearchButton()
        showSoftKeyboard(binding.search)
    }

    private fun initSearchButton(longitude: String, latitude: String) {
        binding.search.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchLocation(view.text.toString(), longitude, latitude)
            }
            true
        }
    }

    private suspend fun setSelectedPlace(place: Place) = viewModel.setSelectedPlace(place)

    private suspend fun saveLocationHistory(place: Place) = viewModel.savePlaceHistory(place)

    private fun requestFocusToSearchButton() = binding.search.requestFocus()

    private fun showSoftKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun searchLocation(keyword: String, longitude: String, latitude: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchPlaceByKeyword(keyword, longitude, latitude).collectLatest {
                placePageAdapter.submitData(it)
            }
        }
    }

    private fun setListAdapter() {
        binding.searchList.adapter = placeHistoryAdapter
    }

    private fun observeSearchHistoryList() {
        viewModel.placeHistoryList.asLiveData().observe(viewLifecycleOwner) { list ->
            placeHistoryAdapter.submitList(list)
        }
    }

    private fun historyItemClicked(place: Place, delete: Boolean) {
        lifecycleScope.launch {
            if (delete) {
                viewModel.deletePlaceHistory(place)
            } else {
                setSelectedPlace(place)
                hideSoftKeyboard(binding.search)
                popBackStack()
            }
        }
    }
}