package com.example.reddittest.make_post_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittest.R
import com.example.reddittest.data_model.Rule
import com.example.reddittest.databinding.RuleLayoutItemBinding


class RuleListAdapter(private val rules: MutableList<Rule>, context: Context)
    : RecyclerView.Adapter<RuleViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val itemView = inflater.inflate(R.layout.rule_layout_item, parent, false)
        return RuleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val rule = rules[position]
        holder.bind(rule)
    }

    override fun getItemCount(): Int {
        return rules.size
    }
}

class RuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var binding = RuleLayoutItemBinding.bind(itemView)

    fun bind(rule: Rule) {
        binding.ruleText.text = rule.shortName
        binding.descriptionText.text = rule.description
        binding.descriptionText.visibility = View.GONE

        binding.ivShowdescription.setOnClickListener {
            if (binding.descriptionText.visibility == View.GONE) {
                val fadeIn = AnimationUtils.loadAnimation(itemView.context, R.anim.fade_in)
                binding.descriptionText.startAnimation(fadeIn)
                binding.descriptionText.visibility = View.VISIBLE
            } else {
                binding.descriptionText.visibility = View.GONE
            }
        }
    }
}
