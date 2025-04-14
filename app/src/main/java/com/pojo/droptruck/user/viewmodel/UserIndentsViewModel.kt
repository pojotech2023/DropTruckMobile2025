package com.pojo.droptruck.user.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pojo.droptruck.activity.indents.CreIndentRes
import com.pojo.droptruck.activity.indents.IndentActivity
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.repository.CustomerApiRepository
import com.pojo.droptruck.user.act.CustomerMainActivity
import com.pojo.droptruck.user.frag.mapindent.MapCreateIndentFragment
import com.pojo.droptruck.user.model.UserCreateIndent
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserIndentsViewModel @Inject constructor(var repo: CustomerApiRepository): ViewModel() {

    var cIndent = UserCreateIndent()
    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val cIResultLiveData = MutableLiveData<Resource<CreIndentRes>>()

    var bodyTypesLiveData: TypesPojo? = TypesPojo()
    var materialTypesLiveData : TypesPojo? = TypesPojo()
    var truckTypesLiveData : TypesPojo? = TypesPojo()

    var bodyTypeData = MutableLiveData<TypesPojo>()
    var materialTypeData = MutableLiveData<TypesPojo>()
    var truckTypeData = MutableLiveData<TypesPojo>()

    fun callCreateIntent(cInt: UserCreateIndent) {
        Log.d("Intent:::",Gson().toJson(cInt))
        repo.createIntent(cIResultLiveData,cInt)

    }

    fun getSpinnerData() {
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {

                val bodyType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.BODY_TYPES) }
                val truckType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.TRUCK_TYPES) }
                val matType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.MATERIAL_TYPES) }
                try {
                    bodyTypesLiveData = bodyType.await()
                    Log.d("BodyType:::",Gson().toJson(bodyTypesLiveData))
                    truckTypesLiveData = truckType.await()
                    materialTypesLiveData = matType.await()


                    bodyTypeData.postValue(bodyTypesLiveData)
                    truckTypeData.postValue(truckTypesLiveData)
                    materialTypeData.postValue(materialTypesLiveData)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

    }

}