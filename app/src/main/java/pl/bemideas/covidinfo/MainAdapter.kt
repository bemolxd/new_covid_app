package pl.bemideas.covidinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_card.view.*

class MainAdapter(val homeFeed: Data): RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.item_article_card, parent, false)

        return CustomViewHolder(cellForRow)
    }

    override fun getItemCount(): Int = homeFeed.data.collection.count()

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.view.item_date.text = homeFeed.data.collection[position].articleDate
        holder.view.item_header.text = homeFeed.data.collection[position].articleName

        Glide.with(holder.view).load("https://gov.pl"+homeFeed.data.collection[position].thumbnail).into(holder.view.item_image)
    }


}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}
