package com.app.weatherforeastapp

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherforeastapp.databinding.FavouritRowBinding
import com.app.weatherforeastapp.model.WeatherResponse
import com.app.weatherforeastapp.ui.favourit.FavouritViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class FavouritAdapter(var favouriteList:ArrayList<WeatherResponse>, favouritesViewModel: FavouritViewModel, context: Context):RecyclerView.Adapter<FavouritAdapter.FavouritViewHolder>() {

    class FavouritViewHolder(var view:FavouritRowBinding):RecyclerView.ViewHolder(view.root)
        lateinit var context: Context
        private var favouriteViewModel=FavouritViewModel(context.applicationContext as Application)
        private var removedPosition=0
       lateinit var removedObject:WeatherResponse


        init {
            this.context =context
            this.favouriteViewModel=favouriteViewModel
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritViewHolder {

        val binding=FavouritRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FavouritViewHolder(binding)

    }



    override fun onBindViewHolder(holder: FavouritViewHolder, position: Int) {

        val item=favouriteList[position]
        holder.view.txtCity.text=item.timezone
        holder.view.txtDesc.text=item.current.weather[0].description
        holder.view.txtTemp.text=item.current.temp.toInt().toString()
        holder.view.favItem.setOnClickListener {
            if (item != null) {
                onItemClickListener?.onClick(position,item)
            }

        }
    }
    //if i need to add an  new favourit event
fun onUpdateData(newlist:List<WeatherResponse>){
    favouriteList.clear()
    favouriteList.addAll(newlist)
    //the response saved as live data so any change in this data must be notified
    notifyDataSetChanged()
}


    fun removeFavouritFromAdapter(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedObject = favouriteList[viewHolder.adapterPosition]
        favouriteList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(
            viewHolder.itemView,
            "${removedObject.timezone} removed",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Undo") {
                favouriteList.add(removedPosition, removedObject)
                notifyItemInserted(removedPosition)
            }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>(){
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event==Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                        favouriteViewModel.deleteWeatherObjectByTimeZone(removedObject.timezone)
                    }
                }
            })
        }.show()


    }


    var onItemClickListener:OnItemClickListener?=null
    interface OnItemClickListener{
        fun onClick(pos:Int,weatherResponse: WeatherResponse)
    }



    override fun getItemCount(): Int =favouriteList.size
 /*   override fun getItemCount(): Int {
      return favouriteList.size

    }*/


}


