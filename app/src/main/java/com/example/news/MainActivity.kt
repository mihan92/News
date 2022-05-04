package com.example.news

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var requestClose: Disposable

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btSearch.setOnClickListener {

            val obs = createRequest(urlIndex)
                .map { Gson().fromJson(it, Feed::class.java) }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            requestClose = obs.subscribe({
                showRecView(it.items)
            }, {
                Log.e("tag1", "", it)
            })
        }

    }

    private fun showRecView(feedList: ArrayList<FeedItem>) {
        binding.recView.adapter = RecAdapter(feedList)
        binding.recView.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestClose.dispose()
    }
}

class Feed(
    val items: ArrayList<FeedItem>
)

class FeedItem(
    val title: String,
    val link: String,
    val description: String,
    val enclosure: Enclosure
)

data class Enclosure(
    val link: String,
    val type: String,
    val length: Int
)

class RecAdapter(private val items: ArrayList<FeedItem>): RecyclerView.Adapter<RecHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)

        return RecHolder(view)
    }

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}


class RecHolder(view: View): RecyclerView.ViewHolder(view) {

    fun bind(item: FeedItem) {
        val vTitle = itemView.findViewById<TextView>(R.id.tv_title)
        val vDesc = itemView.findViewById<TextView>(R.id.tv_desc)
        val vThumb = itemView.findViewById<ImageView>(R.id.item_thumb)
        vTitle.text = item.title
        vDesc.text = item.description

        val thumbURI: String = item.enclosure.link
        try {
            Picasso.with(vThumb.context).load(thumbURI).into(vThumb)
        } catch (e: Exception) {
            Log.e("ImgLoadingError", e.message ?: "null")

            Picasso.with(vThumb.context).load(R.drawable.no_image).into(vThumb)
        }

        itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.link)
            vThumb.context.startActivity(intent)
        }
    }

}

