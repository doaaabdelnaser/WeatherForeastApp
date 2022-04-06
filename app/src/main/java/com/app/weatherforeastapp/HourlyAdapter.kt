package com.app.weatherforeastapp
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherforeastapp.databinding.RowofHourlyBinding
import com.app.weatherforeastapp.model.Hourly
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(var hourlyList: ArrayList<Hourly>) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>(){


    class HourlyViewHolder(var view: RowofHourlyBinding): RecyclerView.ViewHolder(view.root)
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item=hourlyList[position]
        holder.view.txtTemp.text=item.temp.toInt().toString()+"°"
        holder.view.txtDesc.text=item.weather[0].description
        holder.view.txtHour.text=timeFormat(item.dt.toInt())
        Glide.with(holder.view.imgIcon.context)
            .load(item.weather[0].icon?.let { getImage(it) })
            .placeholder(R.drawable.ic_cloudya)
            .into(holder.view.imgIcon)    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding= RowofHourlyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HourlyAdapter.HourlyViewHolder(binding)
    }

    private fun localizingDays(day:String,context: Context):String{
        return when (day.trim()) {
            "Saturday" ->context.getString(R.string.saturday)
            "Sunday" ->context.getString(R.string.sunday)
            "Monday" ->context.getString(R.string.monday)
            "Tuesday" ->context.getString(R.string.tuesday)
            "Wednesday" ->context.getString(R.string.wednesday)
            "Friday" ->context.getString(R.string.friday)
            "Thursday" ->context.getString(R.string.thursday)
            else ->day
        }
    }
    //get icon of weather from Api
    fun getImage(icon:String):String{

        return "http://openweathermap.org/img/w/${icon}.png"

    }
    //any udate or change in liveData
    fun updateData(newList: List<Hourly>){
        hourlyList.clear()
        hourlyList.addAll(newList)
        notifyDataSetChanged()
    }

    //single line function
    override fun getItemCount(): Int =hourlyList.size

}
//time formated to get time day hour.minut.second
fun timeFormat(millisSeconds:Int ): String {
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis((millisSeconds * 1000).toLong())
    val format = SimpleDateFormat("hh:00 aaa")
    return format.format(calendar.time)
}
