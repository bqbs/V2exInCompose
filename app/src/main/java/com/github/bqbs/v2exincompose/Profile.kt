package com.github.bqbs.v2exincompose

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.bqbs.v2exincompose.model.Member
import com.github.bqbs.v2exincompose.repository.V2exRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

/**
 * 资料页
 *
 */

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProfilePage(
    actions: MainActions? = null,
    id: Long? = -1,
    userName: String? = null,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var member: MutableState<Member?> = mutableStateOf<Member?>(null)
    Scaffold {
        Column {
            Row {
                Image(
                    painter = rememberImagePainter(data = member.value?.avatar_normal,
                        onExecute = ImagePainter.ExecuteCallback { _, _ -> true },
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_background)
                            error(R.drawable.ic_launcher_background)
                            transformations(CircleCropTransformation())
                        }), contentDescription = "", modifier = Modifier.size(200.dp)
                )
            }
            Row {
                Text("$userName", Modifier.clickable {
                    viewModel.member.observeForever {
                        member.value = it
                    }
                    viewModel.profileUserName.value = userName
                    viewModel.showProfile()
                })
            }
        }
    }
}


class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy {
        V2exRepository()
    }
    val member: MutableLiveData<Member> = MutableLiveData<Member>()
    var profileId: MutableState<Long?> = mutableStateOf(null)
    var profileUserName: MutableState<String?> = mutableStateOf(null)
    fun showProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val m: Member? = when {
                profileId.value != null -> {
                    repository.getProfile(profileId.value!!)
                }
                profileUserName.value != null -> {
                    repository.getProfile(profileUserName.value!!)
                }
                else -> {
                    null
                }
            }
            if (m != null) {
                member.postValue(m)
            }
        }
    }
}

