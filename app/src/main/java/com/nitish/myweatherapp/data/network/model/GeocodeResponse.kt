package com.nitish.myweatherapp.data.network.model

import com.google.gson.annotations.SerializedName

data class LocalNames(

	@field:SerializedName("mr")
	val mr: String? = null
)

data class GeocodeResponseItem(

	@field:SerializedName("local_names")
	val localNames: LocalNames? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
