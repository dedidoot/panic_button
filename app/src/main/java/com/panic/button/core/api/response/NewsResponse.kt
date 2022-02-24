package com.panic.button.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class NewsResponse(@SerializedName("data") val newsModel: NewsModel? = null) : BaseResponse()

@Parcelize
data class NewsModel(@SerializedName("items") val items: ArrayList<NewsItemModel>? = null) : Parcelable


@Parcelize
data class NewsItemModel(@SerializedName("id") val id: Int? = null,
                         @SerializedName("post_title") val post_title: String? = null,
                         @SerializedName("post_slug") val post_slug: String? = null,
                         @SerializedName("post_content") val post_content: String? = null,
                         @SerializedName("cover") val cover: CoverModel? = null,
                         @SerializedName("category") val category: CategoryModel? = null,
                         var isShowImageView: Boolean = true) :  Parcelable

@Parcelize
data class CoverModel(@SerializedName("id") val id: String? = null,
                      @SerializedName("url") val url: String? = null) : Parcelable
@Parcelize
data class CategoryModel(@SerializedName("category_name") val category_name: String? = null,
                         @SerializedName("category_slug") val category_slug: String? = null) : Parcelable