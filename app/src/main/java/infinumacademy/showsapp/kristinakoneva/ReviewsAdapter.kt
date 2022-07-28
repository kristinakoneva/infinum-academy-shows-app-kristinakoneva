package infinumacademy.showsapp.kristinakoneva

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinumacademy.showsapp.kristinakoneva.databinding.ViewReviewItemBinding
import model.Review

class ReviewsAdapter(

    private var items: List<Review>

) : RecyclerView.Adapter<ReviewsAdapter.ShowDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowDetailsViewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowDetailsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun addAllItems(reviews: List<Review>) {
        items = reviews
        notifyDataSetChanged()
    }

    private fun extractUsername(email: String): String {
        val parts = email.split("@")
        val username = parts[0]
        return username
    }

    inner class ShowDetailsViewHolder(private val binding: ViewReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Review) {
            with(binding) {
                reviewRating.text = item.rating.toString()
                reviewUsername.text = extractUsername(item.user.email)
                reviewComment.text = item.comment
            }

        }
    }
}