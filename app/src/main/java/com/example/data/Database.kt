package com.example.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

// 1. MQTT Broker Configuration Table
@Entity(tableName = "broker_configurations")
data class MqttBrokerConfig(
    @PrimaryKey val id: Int = 1,
    val clientId: String = "NEXUS_CLIENT_001",
    val serverName: String = "EX: PRODUCAO_BRAZIL",
    val host: String = "broker.hivemq.com",
    val port: Int = 1883,
    val login: String = "",
    val senha: String = "",
    val keepAlive: Int = 60,
    val mqttVersion: String = "MQTT v3.1.1",
    val useTls: Boolean = false,
    val isActive: Boolean = true
)

// 2. Active Subscriptions Table
@Entity(tableName = "mqtt_subscriptions")
data class MqttSubscription(
    @PrimaryKey val topic: String,
    val qos: Int = 0
)

// 3. Telemetry and Logs Message Table
@Entity(tableName = "mqtt_message_logs")
data class MqttMessageLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOfflineTelemetry: Boolean = false
)

// 4.5. Telemetry Sources Table
@Entity(tableName = "telemetry_sources")
data class TelemetrySource(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val label: String,
    val colorHex: String = "#00DBE9"
)

// 5. Customizable Grid Widgets Table
@Entity(tableName = "dashboard_widgets")
data class WidgetConfig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val topic: String,
    val type: String, // "temperature", "humidity", "switch", "pressure", "gauge", "command"
    val payloadOn: String = "1",
    val payloadOff: String = "0",
    val iconName: String = "device_hub",
    val widgetSize: Int = 1, // 1 for Compact/Small, 2 for Wide/Large
    val colorHex: String = "#00dbe9", // Electric Cyan default
    val lastKnownValue: String = "",
    val imageOnUri: String = "",
    val imageOffUri: String = "",
    val subscribeTopic: String = "",
    val position: Int = 0,
    val imageSize: Float = 124f
)

// --- DAOs ---

@Dao
interface MqttDao {
    // Broker Configuration
    @Query("SELECT * FROM broker_configurations LIMIT 1")
    fun getBrokerConfigFlow(): Flow<MqttBrokerConfig?>

    @Query("SELECT * FROM broker_configurations LIMIT 1")
    suspend fun getBrokerConfig(): MqttBrokerConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBrokerConfig(config: MqttBrokerConfig)

    // Subscriptions
    @Query("SELECT * FROM mqtt_subscriptions")
    fun getAllSubscriptionsFlow(): Flow<List<MqttSubscription>>

    @Query("SELECT * FROM mqtt_subscriptions")
    suspend fun getAllSubscriptions(): List<MqttSubscription>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubscription(sub: MqttSubscription)

    @Delete
    suspend fun removeSubscription(sub: MqttSubscription)

    // Log Messages
    @Query("SELECT * FROM mqtt_message_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllMessageLogsFlow(): Flow<List<MqttMessageLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageLog(log: MqttMessageLog)

    @Query("DELETE FROM mqtt_message_logs")
    suspend fun clearMessageLogs()

    @Query("SELECT MAX(position) FROM dashboard_widgets")
    suspend fun getMaxPosition(): Int

    @Query("UPDATE dashboard_widgets SET position = :newPos WHERE id = :id")
    suspend fun updateWidgetPosition(id: Int, newPos: Int)

    // Dashboard Widgets
    @Query("SELECT * FROM dashboard_widgets ORDER BY position ASC, id ASC")
    fun getAllWidgetsFlow(): Flow<List<WidgetConfig>>

    @Query("SELECT COUNT(*) FROM dashboard_widgets")
    suspend fun getWidgetsCount(): Int

    @Query("SELECT * FROM dashboard_widgets ORDER BY position ASC, id ASC")
    suspend fun getAllWidgets(): List<WidgetConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetConfig)

    @Update
    suspend fun updateWidget(widget: WidgetConfig)

    @Delete
    suspend fun deleteWidget(widget: WidgetConfig)
    
    @Query("UPDATE dashboard_widgets SET lastKnownValue = :value WHERE topic = :topic")
    suspend fun updateWidgetValueByTopic(topic: String, value: String)

    @Query("UPDATE dashboard_widgets SET lastKnownValue = :value WHERE subscribeTopic = :subscribeTopic")
    suspend fun updateWidgetValueBySubscribeTopic(subscribeTopic: String, value: String)

    // Telemetry Sources
    @Query("SELECT * FROM telemetry_sources ORDER BY id ASC")
    fun getAllTelemetrySourcesFlow(): Flow<List<TelemetrySource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetrySource(source: TelemetrySource)

    @Delete
    suspend fun deleteTelemetrySource(source: TelemetrySource)
}

// --- App Database Class ---

@Database(
    entities = [
        MqttBrokerConfig::class,
        MqttSubscription::class,
        MqttMessageLog::class,
        TelemetrySource::class,
        WidgetConfig::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val mqttDao: MqttDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS ai_config")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE dashboard_widgets ADD COLUMN imageOnUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE dashboard_widgets ADD COLUMN imageOffUri TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE dashboard_widgets ADD COLUMN subscribeTopic TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE dashboard_widgets ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE dashboard_widgets ADD COLUMN imageSize REAL NOT NULL DEFAULT 124.0")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS telemetry_sources (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, topic TEXT NOT NULL, label TEXT NOT NULL, colorHex TEXT NOT NULL DEFAULT '#00DBE9')")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexus_command_db"
                )
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
