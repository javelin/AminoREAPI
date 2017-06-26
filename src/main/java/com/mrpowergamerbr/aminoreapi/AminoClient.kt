package com.mrpowergamerbr.aminoreapi

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mrpowergamerbr.aminoreapi.entities.AminoCommunity
import com.mrpowergamerbr.aminoreapi.entities.AminoInvitation
import com.mrpowergamerbr.aminoreapi.utils.Endpoints
import com.mrpowergamerbr.aminoreapi.utils.responses.LoginResponse
import java.net.URLEncoder

fun main(args: Array<String>) {
	var aminoClient = AminoClient("leonardomalaman+loritta@gmail.com", "LorittaAminoAccountHey", "01F7C86E44138D5AA337F204B95599EBBBD15D4CBE53EBDAC0F032EA653EEFDF22D5A761C0517E1499");
	aminoClient.login();
}

class AminoClient(val login: String, val password: String, val deviceId: String) {
	lateinit var secret: String;
	lateinit var sid: String;

	/**
	 * Starts the login process using the provided login, password and deviceId
	 * @return the LoginResponse
	 */
	fun login(): LoginResponse {
		// Preparing Login Payload
		val innerObject = JsonObject()

		innerObject.addProperty("email", login) // TODO: How the phone login is handled?
		innerObject.addProperty("secret", "0 " + password) // TODO: Is the secret always prefixed by "0 "?
		innerObject.addProperty("deviceID", deviceId) // TODO: Auto generated device ID?
		innerObject.addProperty("clientType", 100) // TODO: Other client types?
		innerObject.addProperty("action", "normal") // TODO: What are the other actions? (register?)

		var response = HttpRequest
				.get(Endpoints.LOGIN)
				.acceptJson()
				.send(innerObject.toString())
				.body();

		val aminoResponse = Amino.gson.fromJson(response, LoginResponse::class.java);
		aminoResponse.jsonResponse = response;

		println(aminoResponse.jsonResponse)

		this.secret = aminoResponse.secret;
		this.sid = aminoResponse.sid;
		return aminoResponse;
	}

	fun getAffiliations() {
		var response = HttpRequest
				.get(Endpoints.AFFILIATIONS)
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getSuggestedCommunities(lang: String) {
		var response = HttpRequest
				.get(String.format(Endpoints.SUGGESTED_COMMUNITIES, lang))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getTrendingCommunities(start: Int, size: Int, lang: String) {
		var response = HttpRequest
				.get(String.format(Endpoints.TRENDING_COMMUNITIES, start, size, lang))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getSuggestedKeywords(keyword: String, start: Int, size: Int, lang: String) {
		var response = HttpRequest
				.get(String.format(Endpoints.SUGGESTED_KEYWORDS, keyword, start, size, lang))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getHeadlines(start: Int, size: Int) {
		var response = HttpRequest
				.get(String.format(Endpoints.HEADLINES, start, size))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getDeviceInfo() { // TODO: Fix
		var response = HttpRequest
				.post(Endpoints.DEVICE_INFO)
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getCommunityById(communityId: String): AminoCommunity {
		// First we are going to get the community info via the ID
		var response = HttpRequest
				.get(String.format(Endpoints.COMMUNITY_INFO, communityId))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		var parser = JsonParser();
		var parsedJson = parser.parse(response).asJsonObject.get("community");

		println(parsedJson.toString());

		var community = Amino.gson.fromJson(parsedJson.toString(), AminoCommunity::class.java);
		community.id = communityId;
		community.aminoClient = this;
		return community;
	}

	fun getInvitationInfo(invitationUrl: String): AminoInvitation {
		var invitationIdResponse = HttpRequest
				.get(String.format(Endpoints.LINK_IDENTIFY, URLEncoder.encode(invitationUrl, "UTF-8")))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(invitationIdResponse);

		var parser = JsonParser();
		var parsedJson = parser.parse(invitationIdResponse).asJsonObject.get("invitation").asJsonObject;

		return Amino.gson.fromJson(parsedJson.toString(), AminoInvitation::class.java);
	}

	fun getOnlineMembersInCommunity(communityId: String) {
		var response = HttpRequest
				.get(String.format(Endpoints.COMMUNITY_ONLINE_MEMBERS, communityId))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getNotificationsForCommunity(communityId: String, start: Int, size: Int, cv: Double) { // TODO: What is cv?
		var response = HttpRequest
				.get(String.format(Endpoints.COMMUNITY_NOTIFICATIONS, communityId, start, size, cv))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun sendMessageInChat(communityId: String, thread: String, chatMessage: String) {
		val innerObject = JsonObject()

		innerObject.addProperty("content", chatMessage)
		innerObject.addProperty("type", 0)
		innerObject.addProperty("clientRefId", 843397539)

		println(innerObject)

		var response = HttpRequest
				.post(String.format(Endpoints.COMMUNITY_CHAT_SEND_MESSAGE, communityId, thread, chatMessage))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.send(innerObject.toString())
				.body();

		println(response)
	}

	fun getMessagesInChat(communityId: String, threadId: String, start: Int, size: Int): String {
		var response = HttpRequest
				.get(String.format(Endpoints.COMMUNITY_CHAT_GET_MESSAGES, communityId, threadId, start, size))
				.header("NDCAUTH", "sid=" + sid)
				.acceptJson()
				.body();

		println(response)

		return response;
	}
}