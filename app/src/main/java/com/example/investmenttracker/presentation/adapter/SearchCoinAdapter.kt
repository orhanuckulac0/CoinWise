package com.example.investmenttracker.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.formatPrice
import com.example.investmenttracker.databinding.SearchCoinSingleItemBinding

class SearchCoinAdapter(
    private val context: Context,
    private val coin: CoinModel
): RecyclerView.Adapter<SearchCoinAdapter.CoinsViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: SearchCoinAdapter.CoinsViewHolder, position: Int) {
        val model = coin
        val coinIcon = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+"${coin.cmcId}"+".png"

        Glide
            .with(context)
            .load(coinIcon)
            .thumbnail(Glide.with(context).load(R.drawable.spinner))
            .centerCrop()
            .placeholder(R.drawable.coin_place_holder)
            .into(holder.ivCoinSearchResultImage)


        holder.tvCoinName.text = model.name
        holder.tvCoinPrice.text = formatPrice(model.price)

        // set onclick listener to add coin icon only
        holder.ivAddCoin.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        return CoinsViewHolder(SearchCoinSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 1
    }

    inner class CoinsViewHolder(binding: SearchCoinSingleItemBinding) : RecyclerView.ViewHolder(binding.root){
        val tvCoinName = binding.tvCoinName
        val tvCoinPrice = binding.tvCoinPrice
        val ivCoinSearchResultImage = binding.ivSearchedCoinResultImage
        val ivAddCoin = binding.ivAddCoin
    }

    interface OnClickListener{
        fun onClick(position: Int, coinModel: CoinModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

}