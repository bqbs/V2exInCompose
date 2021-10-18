package com.github.bqbs.v2exincompose

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.bqbs.v2exincompose.model.Member
import com.github.bqbs.v2exincompose.repository.V2exRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 资料页
 *
 */

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProfilePage(
    actions: MainActions? = null,
    id: Long? = null,
    userName: String? = null,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val member by viewModel.member.collectAsState(null)

    Scaffold {
        Column {
            Row {
                Image(
                    painter = rememberImagePainter(data = member?.avatar_large,
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
                Text(member?.username ?: "Welcome", Modifier.clickable {
                    viewModel.profileUserName.value = userName
                    viewModel.profileId.value = id
                    viewModel.showProfile()
                })
            }
        }
    }

    if (id != null || userName != null) {
        viewModel.profileUserName.value = userName
        viewModel.profileId.value = id
        viewModel.showProfile()
    }
}


class ProfileViewModel() : ViewModel() {
    private val repository by lazy {
        V2exRepository()
    }
    private var _member = MutableStateFlow<Member?>(null)
    val member: MutableStateFlow<Member?>
        get() = _member
    var profileId: MutableStateFlow<Long?> = MutableStateFlow(null)
    var profileUserName: MutableStateFlow<String?> = MutableStateFlow(null)
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
            withContext(Dispatchers.Main) {
                _member.emit(m)
            }
        }
    }
}

