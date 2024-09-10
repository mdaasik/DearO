package com.carworkz.dearo.domain.entities


import com.squareup.moshi.Json


data class Role(
	@Json(name = "appRoutes")
	var appRoutes: List<String>? = null,
	@Json(name = "created")
	var created: String? = null, // 2021-09-03T12:58:54.486Z
	@Json(name = "createdOn")
	var createdOn: String? = null, // 2021-09-03T12:58:54.487Z
	@Json(name = "defaultRoute")
	var defaultRoute: String? = null, // /shop-floor/initiated-jobcards
	@Json(name = "description")
	var description: String? = null, // workshop driver
	@Json(name = "id")
	var id: String? = null, // 61321c0e946e460363e6a362
	@Json(name = "modified")
	var modified: String? = null, // 2021-09-03T12:58:54.486Z
	@Json(name = "name")
	var name: String? = null, // $ws-driver
	@Json(name = "routes")
	var routes: List<String>? = null,
	@Json(name = "updatedOn")
	var updatedOn: String? = null // 2021-10-26T10:06:46.908Z)
               ){
	companion object{
		private const val CHAR_D ='$'
		val USER_ROLE_ADMIN = CHAR_D + "ws-admin"
		const val USER_ROLE_WS_ADVISOR=""
		val USER_ROLE_DRIVER=CHAR_D + "ws-driver"
		
	}
}