package br.com.V1nic1us.callblocker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils

class ContactLookup(
    private val context: Context,
    private val query: (Uri) -> Boolean = { uri ->
        context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup._ID),
            null,
            null,
            null,
        )?.use { it.moveToFirst() } ?: false
    },
) {
    fun contains(phoneNumber: String): Boolean {
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        val normalized = PhoneNumberUtils.normalizeNumber(phoneNumber)
        if (normalized.isBlank()) return false

        val encodedNumber = Uri.encode(normalized)
        val enterpriseUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI,
            encodedNumber,
        )
        if (querySafely(enterpriseUri)) return true

        val personalUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            encodedNumber,
        )
        return querySafely(personalUri)
    }

    private fun querySafely(uri: Uri): Boolean = try {
        query(uri)
    } catch (_: Exception) {
        false
    }
}
