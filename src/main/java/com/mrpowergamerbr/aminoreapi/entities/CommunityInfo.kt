package com.mrpowergamerbr.aminoreapi.entities

class CommunityInfo(
		val isCurrentUserJoined: Boolean,
		val currentUserInfo: UserInfo,
		val community: AminoCommunityInfo
)