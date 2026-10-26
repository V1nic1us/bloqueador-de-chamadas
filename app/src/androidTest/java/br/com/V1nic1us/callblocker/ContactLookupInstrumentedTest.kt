package br.com.V1nic1us.callblocker

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactLookupInstrumentedTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var rawContactUri: android.net.Uri? = null

    @Before
    fun createContact() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        automation.adoptShellPermissionIdentity(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
        )
        rawContactUri = context.contentResolver.insert(
            ContactsContract.RawContacts.CONTENT_URI,
            android.content.ContentValues(),
        )
        val rawId = requireNotNull(rawContactUri).lastPathSegment!!.toLong()
        context.contentResolver.insert(
            ContactsContract.Data.CONTENT_URI,
            android.content.ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, "+1 (650) 555-0142")
            },
        )
    }

    @After
    fun removeContact() {
        val uri = rawContactUri
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        try {
            if (uri != null) context.contentResolver.delete(uri, null, null)
        } finally {
            automation.dropShellPermissionIdentity()
        }
    }

    @Test
    fun savedNumberIsFound() {
        assertTrue(ContactLookup(context).contains("+1 (650) 555-0142"))
    }

    @Test
    fun absentNumberIsNotFound() {
        assertFalse(ContactLookup(context).contains("+1 650 555 0199"))
    }

    @Test
    fun differentlyFormattedSavedNumberIsFound() {
        assertTrue(ContactLookup(context).contains("+16505550142"))
    }

    @Test
    fun deniedPermissionDoesNotExposeSavedContact() {
        val deniedContext = object : ContextWrapper(context) {
            override fun checkSelfPermission(permission: String): Int = PackageManager.PERMISSION_DENIED
        }
        assertFalse(ContactLookup(deniedContext).contains("+16505550142"))
    }

    @Test
    fun queryErrorDoesNotExposeSavedContact() {
        val failingContext = object : ContextWrapper(context) {
            override fun getContentResolver(): ContentResolver {
                throw IllegalStateException("Contacts provider unavailable")
            }
        }
        assertFalse(ContactLookup(failingContext).contains("+16505550142"))
    }

    @Test
    fun enterpriseLookupFindsContactOutsidePersonalProfile() {
        val queriedUris = mutableListOf<android.net.Uri>()
        val lookup = ContactLookup(context) { uri ->
            queriedUris += uri
            uri.toString().startsWith(ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI.toString())
        }

        assertTrue(lookup.contains("+1 (650) 555-0142"))
        assertEquals(1, queriedUris.size)
        assertTrue(queriedUris.single().toString().contains("%2B16505550142"))
    }

    @Test
    fun unsupportedEnterpriseLookupFallsBackToPersonalContacts() {
        val queriedUris = mutableListOf<android.net.Uri>()
        val lookup = ContactLookup(context) { uri ->
            queriedUris += uri
            if (uri.toString().startsWith(ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI.toString())) {
                throw IllegalArgumentException("Enterprise lookup unavailable")
            }
            true
        }

        assertTrue(lookup.contains("+16505550142"))
        assertEquals(2, queriedUris.size)
        assertTrue(queriedUris.last().toString().startsWith(ContactsContract.PhoneLookup.CONTENT_FILTER_URI.toString()))
    }
}
