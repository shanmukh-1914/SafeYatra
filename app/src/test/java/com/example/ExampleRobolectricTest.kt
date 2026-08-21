package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserProfile
import com.example.data.repository.UserLocalDatabaseRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: UserLocalDatabaseRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        UserLocalDatabaseRepository.initialize(context)
        repository = UserLocalDatabaseRepository(context)
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("SafeYatra", appName)
    }

    @Test
    fun `detect provider role for tourist police number`() {
        val provider = repository.findProviderProfileByPhone("+91 11 2346 9526")
        assertNotNull(provider)
        assertEquals("provider", provider?.role)
        assertEquals("Tourist Police", provider?.providerType)
        assertTrue(provider?.isVerifiedProvider == true)
    }

    @Test
    fun `detect provider role for safe transport number`() {
        val provider = repository.findProviderProfileByPhone("+91 98110 54321")
        assertNotNull(provider)
        assertEquals("provider", provider?.role)
        assertEquals("Safe Transport", provider?.providerType)
    }

    @Test
    fun `save and retrieve traveler profile`() {
        val profile = UserProfile(
            uid = "test_traveler_101",
            phone = "+91 91234 56789",
            name = "Aarav Sharma",
            homeCountry = "India",
            preferredLanguage = "Hindi",
            role = "traveler"
        )

        repository.saveUserProfile(profile)
        val loaded = repository.loadUserProfile("test_traveler_101")
        assertNotNull(loaded)
        assertEquals("Aarav Sharma", loaded?.name)
        assertEquals("traveler", loaded?.role)
    }
}

