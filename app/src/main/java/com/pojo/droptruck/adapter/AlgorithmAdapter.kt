package com.pojo.droptruck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import com.pojo.droptruck.R
import com.pojo.droptruck.pojo.IndentRate
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import javax.annotation.Nullable

class AlgorithmAdapter(
    context: Context,
    algorithmList: ArrayList<IndentRate>,
    val mActivity: String
) : ArrayAdapter<IndentRate>(context, 0, algorithmList) {

    @NonNull
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        // It is used to set our custom view.
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.drive_rate_spinner, parent, false)

        val textViewName = view.findViewById<TextView>(R.id.name_amt)
        val currentItem = getItem(position)

        // It is used the name to the TextView when the
        // current item is not null.
        currentItem?.let {

            var dateTime: String? = ""

            try{
                currentItem.createdAt?.let { dT->
                    dateTime = AppUtils.uTCToLocal(dT)//AppUtils.convertDate(dT)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

            try {
                if (mActivity.equals(AppConstant.ConfirmIndentActivity)) {
                    if (it.user!=null) {
                        if (position>0){
                            textViewName.text = it.rate + " - " + it.user?.name + " ( " + dateTime + " ) "
                        }else{
                            textViewName.text = it.rate + " " + it.user?.name
                        }
                    }else{
                        textViewName.text = it.rate + " ( " + dateTime + " ) "
                    }

                }else {
                    textViewName.text = it.rate + " ( " + dateTime + " ) "
                }

            }catch (e:Exception){
                textViewName.text = it.rate + " ( " + dateTime + " ) "
            }

            //textViewName.text = textViewName.text.toString() + " ( " + dateTime + " ) "

        }
        return view
    }
}

