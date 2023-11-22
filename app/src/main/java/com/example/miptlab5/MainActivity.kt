package com.example.miptlab5

import Cube
import DataLoader
import XmlHandler
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.miptlab5.databinding.ActivityMainBinding
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.SAXParserFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var records: MutableList<String>
    private lateinit var originalRecords: List<String>

    private var cubeList: List<Cube> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainAppActions", "The app is starting...")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        originalRecords = mutableListOf("All")
        records = originalRecords.toMutableList()

        arrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, records
        )

        binding.listView.adapter = arrayAdapter

        val spinner: Spinner = binding.spinner
        val spinnerAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, originalRecords
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parentView?.getItemAtPosition(position).toString()
                filterData(selectedItem)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // nothing
            }
        })

        // Execute the AsyncTask to perform the network request and parse XML
        DataLoaderTask().execute()
    }


    private inner class DataLoaderTask : AsyncTask<Void, Void, List<Cube>>() {

        override fun doInBackground(vararg params: Void?): List<Cube> {
            try {
                val dataLoader = DataLoader()
                val inputStream = dataLoader.downloadUrlContent(address)
                return parseXml(inputStream)
            } catch (e: IOException) {
                // Handle IO exception
                e.printStackTrace()
                return emptyList()
            } catch (e: SAXException) {
                // Handle SAX exception
                e.printStackTrace()
                return emptyList()
            }
        }

        override fun onPostExecute(result: List<Cube>) {
            // Update UI with parsed data
            cubeList = result
            updateListView()
            updateSpinner()
        }
    }

    private fun parseXml(inputStream: InputStream): List<Cube> {
        val parser = XmlHandler()
        try {
            val factory = SAXParserFactory.newInstance()
            val saxParser = factory.newSAXParser()
            saxParser.parse(inputStream, parser)
        } finally {
            inputStream.close()
        }
        return parser.getCubeList()
    }

    private fun filterData(selectedItem: String) {
        Log.d("MainAppActions", "The data filtering process for spinner is starting...")
        if (selectedItem == "All") {
            records.clear()
            records.add("All")
            records.addAll(cubeList.map { "Currency: ${it.currency}, Rate: ${it.rate}" })
        } else {
            val filteredRecords = cubeList.filter { it.currency?.contains(selectedItem) == true }
            records.clear()
            records.add("All")
            records.addAll(filteredRecords.map { "Currency: ${it.currency}, Rate: ${it.rate}" })
        }
        arrayAdapter.notifyDataSetChanged()
    }

    private fun updateListView() {
        Log.d("MainAppActions", "The listView is being updated...")
        records.clear()
        records.add("All")
        records.addAll(cubeList.map { "Currency: ${it.currency}, Rate: ${it.rate}" })
        arrayAdapter.notifyDataSetChanged()
    }

    private fun updateSpinner() {
        Log.d("MainAppActions", "The spinner is being updated...")
        originalRecords = listOf("All") + cubeList.mapNotNull { it.currency }
        val spinnerAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, originalRecords
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
    }

    companion object {
        private const val address = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
    }
}
