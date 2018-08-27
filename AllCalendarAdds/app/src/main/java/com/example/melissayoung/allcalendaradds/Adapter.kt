package com.example.melissayoung.allcalendaradds

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_account_info.view.*

class Adapter(private val accounts: ArrayList<CalendarAccount>,
              private val context: Context,
              private val clickListener: (CalendarAccount) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_account_info, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder?.accountName?.text = String.format(
                context.resources.getString(R.string.select_account_template),
                accounts[position].accountName
        )

        holder?.ownerName?.text = String.format(
                context.resources.getString(R.string.account_owner_template),
                accounts[position].ownerName
        )

        holder?.accountType?.text = String.format(
                context.resources.getString(R.string.account_type_template),
                accounts[position].accountType
        )

        holder?.bind(accounts[position], clickListener)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val accountName: TextView = view.account_name
    val ownerName: TextView  = view.owner_name
    val accountType: TextView = view.account_type

    fun bind(item: CalendarAccount, clickListener: (CalendarAccount) -> Unit) {

        itemView.setOnClickListener {
            clickListener(item)

            item.isSelected = !item.isSelected

            when (item.isSelected) {
                true -> itemView.setBackgroundColor(itemView.resources.getColor(R.color.pumiceGray))
                false -> itemView.setBackgroundColor(itemView.resources.getColor(R.color.background_material_light))
            }
        }
    }
}