import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DataLoader {

    @Throws(IOException::class)
    fun downloadUrlContent(address: String): InputStream {
        Log.d("DataLoader", "Content download function is starting")
        val url = URL(address)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000
        conn.connectTimeout = 15000
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.connect()
        return conn.inputStream
    }
}