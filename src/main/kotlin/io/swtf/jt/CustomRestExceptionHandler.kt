package io.swtf.jt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@ControllerAdvice
open class CustomRestExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("CustomRestExceptionHandler")
    }

    override fun handleBindException(ex: BindException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleMissingServletRequestParameter(ex: MissingServletRequestParameterException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleMissingPathVariable(ex: MissingPathVariableException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleExceptionInternal(ex: Exception?, body: Any?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> = ResponseEntity(HttpStatus.NOT_FOUND)

    override fun handleServletRequestBindingException(ex: ServletRequestBindingException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleNoHandlerFoundException(ex: NoHandlerFoundException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleHttpMediaTypeNotAcceptable(ex: HttpMediaTypeNotAcceptableException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleMissingServletRequestPart(ex: MissingServletRequestPartException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleAsyncRequestTimeoutException(ex: AsyncRequestTimeoutException?, headers: HttpHeaders?, status: HttpStatus?, webRequest: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleConversionNotSupported(ex: ConversionNotSupportedException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleTypeMismatch(ex: TypeMismatchException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    override fun handleHttpMessageNotWritable(ex: HttpMessageNotWritableException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        LOGGER.warn("Exception occurred : ${ex?.message}")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
}