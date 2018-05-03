package net.halawata.stamp.service

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import net.halawata.stamp.data.Location
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LocationService(private val helper: SQLiteOpenHelper) {

    /**
     * 位置を保存する
     */
    fun create(latitude: Double, longitude: Double, memo: String = ""): String {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val id = UUID.randomUUID().toString()
            val values = ContentValues().apply {
                put("id", id)
                put("latitude", latitude)
                put("longitude", longitude)
                put("create_date", System.currentTimeMillis())
                put("memo", memo)
            }

            db.insert("location", null, values)
            db.setTransactionSuccessful()

            return id

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /**
     * 位置を更新する
     */
    fun update(location: Location) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val values = ContentValues().apply {
                put("latitude", location.latitude)
                put("longitude", location.longitude)
                put("memo", location.memo)
            }

            db.update(LocationService.tableName, values, "id = ?", arrayOf(location.id))
            db.setTransactionSuccessful()

        } catch (ex: Exception) {
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /**
     * 位置を削除する
     */
    fun delete(id: String) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            db.delete(LocationService.tableName, "id = ?", arrayOf(id))

            db.setTransactionSuccessful()

        } catch (ex: Exception) {
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /**
     * ID で位置を取得
     */
    fun fetch(id: String): Location? {
        val result: Location?
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(LocationService.tableName, LocationService.tableColumns, "id = ?", arrayOf(id), null, null, null, null)

            cursor.moveToFirst()

            result = Location(
                    id = cursor.getString(0),
                    latitude = cursor.getDouble(1),
                    longitude = cursor.getDouble(2),
                    createDate = Date(cursor.getLong(3)),
                    memo = cursor.getString(4)
            )

        } catch (ex: Exception) {
            ex.printStackTrace()
            return null

        } finally {
            cursor?.close()
            db.close()
        }

        return result
    }

    /**
     * 位置を期間で取得
     */
    fun fetch(fromDate: Date, toDate: Date): List<Location> {
        val result: ArrayList<Location> = arrayListOf()
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(LocationService.tableName, LocationService.tableColumns, "create_date >= ? AND create_date < ?", arrayOf(fromDate.time.toString(), (toDate.time + 24 * 60 * 60 * 1000).toString()), null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                result.add(Location(
                        id = cursor.getString(0),
                        latitude = cursor.getDouble(1),
                        longitude = cursor.getDouble(2),
                        createDate = Date(cursor.getLong(3)),
                        memo = cursor.getString(4)
                ))

                eol = cursor.moveToNext()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            return result

        } finally {
            cursor?.close()
            db.close()
        }

        return result
    }

    /**
     * 位置を全件取得
     */
    fun fetchAll(): List<Location> {
        val result: ArrayList<Location> = arrayListOf()
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(LocationService.tableName, LocationService.tableColumns, null, null, null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                result.add(Location(
                        id = cursor.getString(0),
                        latitude = cursor.getDouble(1),
                        longitude = cursor.getDouble(2),
                        createDate = Date(cursor.getLong(3)),
                        memo = cursor.getString(4)
                ))

                eol = cursor.moveToNext()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            return result

        } finally {
            cursor?.close()
            db.close()
        }

        return result
    }

    companion object {
        const val tableName = "location"

        val tableColumns = arrayOf("id", "latitude", "longitude", "create_date", "memo")

        /**
         * 表示用に緯度経度を加工する
         */
        fun convertLatLng(latitude: Double, longitude: Double, convertType: ConvertType): String {
            return convertLatitude(latitude, convertType) + " " + convertLongitude(longitude, convertType)
        }

        /**
         * 表示用に緯度を加工する
         */
        fun convertLatitude(latitude: Double, convertType: ConvertType): String {
            return when (convertType) {
                ConvertType.PLAIN -> (if (latitude >= 0) "+" else "") + latitude.toString()
                ConvertType.DETAIL -> {
                    if (latitude >= 0) {
                        "北緯$latitude"
                    } else {
                        "南緯" + latitude.toString().drop(1)
                    }
                }
            }
        }

        /**
         * 表示用に経度を加工する
         */
        fun convertLongitude(longitude: Double, convertType: ConvertType): String {
            return when (convertType) {
                ConvertType.PLAIN -> (if (longitude >= 0) "+" else "") + longitude.toString()
                ConvertType.DETAIL -> {
                    if (longitude >= 0) {
                        "東経$longitude"
                    } else {
                        "西経" + longitude.toString().drop(1)
                    }
                }
            }
        }

        /**
         * 表示用に日付を加工する
         */
        fun convertDisplayDate(date: Date, convertType: ConvertType): String {
            val pattern = when (convertType) {
                ConvertType.PLAIN -> "yyyy/MM/dd"
                ConvertType.DETAIL -> "yyyy年MM月dd日"
            }

            return SimpleDateFormat(pattern, Locale.US).format(date)
        }

        /**
         * 表示用に時間を加工する
         */
        fun convertDisplayTime(time: Date, convertType: ConvertType): String {
            val pattern = when (convertType) {
                ConvertType.PLAIN -> "HH:mm:ss"
                ConvertType.DETAIL -> "HH時mm分ss秒"
            }

            return SimpleDateFormat(pattern, Locale.US).format(time)
        }

        /**
         * 表示用に日時を加工する
         */
        fun convertDisplayDatetime(datetime: Date, convertType: ConvertType): String {
            val pattern = when (convertType) {
                ConvertType.PLAIN -> "yyyy/MM/dd HH:mm:ss"
                ConvertType.DETAIL -> "yyyy年MM月dd日 HH時mm分ss秒"
            }

            return SimpleDateFormat(pattern, Locale.US).format(datetime)
        }
    }

    enum class ConvertType {
        PLAIN,
        DETAIL,
    }
}
