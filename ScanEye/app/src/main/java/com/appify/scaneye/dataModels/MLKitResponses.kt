package com.appify.scaneye.dataModels

sealed class MLKitResponses(
    val scanHistoryItem: ScanHistoryItem? = null,
    val errorMessage: String? = null
) {
    class SuccessResponse(scanHistoryItem: ScanHistoryItem) : MLKitResponses(scanHistoryItem)
    class ErrorResponse(errorMessage: String) : MLKitResponses(errorMessage = errorMessage)
    class LoadingResponse() : MLKitResponses()
}