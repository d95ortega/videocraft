package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.ProjectJsonParser
import com.example.database.VideoCraftDatabase
import com.example.model.VideoProject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: VideoCraftDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VideoCraftDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VideoCraft", appName)
    }

    @Test
    fun `test database insert and retrieve project`() = runBlocking {
        val project = VideoProject(title = "Video Robolectric")
        val entity = ProjectJsonParser.toEntity(project)
        db.projectDao().insertOrUpdate(entity)

        val retrieved = db.projectDao().getProjectById(project.id)
        assertNotNull(retrieved)
        assertEquals("Video Robolectric", retrieved?.title)
    }
}
