package com.example.investmenttracker.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.formatPrice
import com.example.investmenttracker.data.util.formatTokenHeldAmount
import com.example.investmenttracker.data.util.formatTokenTotalValue
import com.example.investmenttracker.databinding.WalletTokenSingleItemBinding

class MainActivityAdapter(
    private val context: Context,
    ): RecyclerView.Adapter<MainActivityAdapter.WalletViewHolder>() {

    private var onClickListener: OnClickListener? = null

    //This class finds the difference between two lists and provides the updated list as an output.
    // This class is used to notify updates to a RecyclerView Adapter.
    private val callback = object : DiffUtil.ItemCallback<CoinModel>(){
        override fun areItemsTheSame(oldItem: CoinModel, newItem: CoinModel): Boolean {
            return oldItem.cmcId == newItem.cmcId
        }

        override fun areContentsTheSame(oldItem: CoinModel, newItem: CoinModel): Boolean {
            return oldItem == newItem
        }
    }

    // AsyncListDiffer is a helper for computing the difference between two lists via DiffUtil on a background thread.
    // will signal the adapter of changes between submitted lists
    val differ = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        return WalletViewHolder(WalletTokenSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.bind(model)

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class WalletViewHolder(private val binding: WalletTokenSingleItemBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(coinModel: CoinModel) {
            val coinIcon =
                "https://s2.coinmarketcap.com/static/img/coins/64x64/" + "${differ.currentList[position].cmcId}" + ".png"

            binding.tvCoinName.text = coinModel.name
            binding.tvCoinPrice.text = "$${formatPrice(coinModel.price)}"
            binding.tvTokenHeldAmount.text = formatTokenHeldAmount(coinModel.totalTokenHeldAmount)
            binding.tvTokenTotalValue.text = "$${formatTokenTotalValue(coinModel.price, coinModel.totalInvestmentAmount)}"

            if (coinModel.percentChange24h.toString().contains("-")){
                binding.tvCoinPriceChangeDaily.setTextColor(context.getColor(R.color.redColorPercentage))
            }else {
                binding.tvCoinPriceChangeDaily.setTextColor(context.getColor(R.color.greenColorPercentage))
            }
            binding.tvCoinPriceChangeDaily.text = String.format("%.2f", coinModel.percentChange24h)+"%"

            Glide
                .with(context)
                .load(coinIcon)
                .thumbnail(Glide.with(context).load(R.drawable.spinner))
                .centerCrop()
                .placeholder(R.drawable.coin_place_holder)
                .into(binding.ivSearchedCoinResultImage)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, coinModel: CoinModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


}