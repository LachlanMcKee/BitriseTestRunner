package net.lachlanmckee.flankci.core.domain.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import io.ktor.response.*
import javax.inject.Inject

internal class MultipartCallFactoryImpl @Inject constructor() : MultipartCallFactory {
  override suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit) {
    val multipart = call.receiveMultipart()
    if (call.request.isMultipart()) {
      multiPartDataFunc(multipart)
    } else {
      call.respondText("Request was not made by a form submission")
    }
  }
}
