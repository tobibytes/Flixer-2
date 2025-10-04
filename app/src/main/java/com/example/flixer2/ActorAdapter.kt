package com.example.flixer2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ActorAdapter(private val context: Context, private val actors: List<Actor>) :
    RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_actor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actor = actors[position]
        holder.bind(actor)
    }

    override fun getItemCount() = actors.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val actorImageView = itemView.findViewById<ImageView>(R.id.actor_image)
        private val actorNameTextView = itemView.findViewById<TextView>(R.id.actor_name)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(actor: Actor) {
            actorNameTextView.text = actor.name
            actor.profileUrl?.let {
                Glide.with(context)
                    .load(it)
                    .transform(RoundedCorners(100)) // Circle
                    .into(actorImageView)
            } ?: actorImageView.setImageDrawable(null)
        }

        override fun onClick(v: View?) {
            val actor = actors[absoluteAdapterPosition]
            val intent = Intent(context, ActorDetailActivity::class.java)
            intent.putExtra("ACTOR_EXTRA", actor)
            context.startActivity(intent)
        }
    }
}
