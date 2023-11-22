import android.util.Log
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

data class Cube(val currency: String?, val rate: String?, var time: String? = null)

class XmlHandler : DefaultHandler() {
    private val cubeList = mutableListOf<Cube>()
    private var currentCube: Cube? = null
    private var currentElement: String? = null

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        try {
            currentElement = qName
            if (qName == "Cube") {
                val currency = attributes?.getValue("currency")
                Log.d("Parser", "Currency $currency is found!")
                val rate = attributes?.getValue("rate")
                currentCube = Cube(currency, rate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        try {
            if (qName == "Cube" && currentCube != null) {
                cubeList.add(currentCube!!)
                currentCube = null
                Log.d("Parser", "Current cube is set to NULL")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        try {
            val value = String(ch!!, start, length).trim()
            Log.d("Parser", "Value has been converted from char symbols to string and whitespaces have been removed!")
            if (currentElement == "time" && currentCube != null) {
                currentCube?.time = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCubeList(): List<Cube> {
        return cubeList
    }
}
