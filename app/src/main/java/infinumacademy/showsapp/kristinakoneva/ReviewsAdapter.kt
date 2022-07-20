package infinumacademy.showsapp.kristinakoneva

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinumacademy.showsapp.kristinakoneva.databinding.ViewReviewItemBinding
import model.Review

class ReviewsAdapter (

    private var items: List<Review>

): RecyclerView.Adapter<ReviewsAdapter.ShowDetailsViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowDetailsViewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context))
        return ShowDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowDetailsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun addItem(review: Review) {
        items = items + review
        notifyItemInserted(items.size-1)
    }

    inner class ShowDetailsViewHolder(private val binding: ViewReviewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Review){
            binding.reviewRating.text = item.rating.toString()
            if(item.reviewer==null){
                binding.reviewUsername.text = "username"
            }
            else{
                binding.reviewUsername.text = item.reviewer
            }
            binding.reviewComment.text = item.comment
        }
    }
}